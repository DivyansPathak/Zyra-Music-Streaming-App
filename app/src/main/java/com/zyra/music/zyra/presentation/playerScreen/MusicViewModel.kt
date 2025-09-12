package com.zyra.music.zyra.presentation.playerScreen

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.zyra.music.zyra.domain.model.SingleTrack
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.Result
import com.zyra.music.zyra.exoplayer.MusicQueueManager
import com.zyra.music.zyra.exoplayer.MusicService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "MusicViewModel"

@androidx.media3.common.util.UnstableApi
class MusicViewModel(
    private val songRepository: SongRepository,
    private val queueManager: MusicQueueManager,
    context: Context
) : ViewModel() {


    private val _uiState = MutableStateFlow(MainPlayerState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<PlayerEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var mediaController: MediaController? = null

    init {
        Log.d(TAG, "ViewModel init")
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
                mediaController?.addListener(playerListener)
                updateStateFromController()
            },
            MoreExecutors.directExecutor()
        )

        viewModelScope.launch {
            Log.d(TAG, "Start")
            while (true) {
                if (_uiState.value.isPlaying) {
                    _uiState.update {
                        it.copy(
                            currentPosition = mediaController?.currentPosition?.coerceAtLeast(0L)
                                ?: 0L,
                            bufferPosition = mediaController?.bufferedPosition?.coerceAtLeast(0L)
                                ?: 0L
                        )
                    }
                }
                delay(1000L)
            }
        }
    }

    fun loadSong(track: SingleTrack) {
        Log.d(TAG, "Loading Song")
        _uiState.update {
            it.copy(
                isLoading = true,
                isBuffering = false,
                isReady = false,
                currentTrack = track
            )
        }

        // We only need to send the YouTube URL as the mediaId.
        // The MusicService callback will handle fetching the real stream URL
        val mediaItem = MediaItem.Builder()
            .setMediaId(track.url)
            .build()

        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        mediaController?.play()
    }


    fun playSongAddCreateQueue(clickedTrack: SingleTrack) {
        // Step 1: Start playing the clicked song immediately.
        // We create a temporary queue containing only this song.
        queueManager.setQueueAndPlay(mediaController, listOf(clickedTrack), 0)

        // Step 2: In the background, fetch the real "Up Next" queue.
        fetchRecommendationsAndUpdateQueue(clickedTrack)
    }

    fun onAction(action: PlayerAction) {
        when (action) {
            PlayerAction.PlayPause -> {
                Log.d(TAG, "Action received: PlayPause")
                if (mediaController?.isPlaying == true) mediaController?.pause() else mediaController?.play()
            }

            is PlayerAction.Seek -> mediaController?.seekTo(action.position.toLong())
            PlayerAction.SkipToNext -> mediaController?.seekToNextMediaItem()
            PlayerAction.SkipToPrevious -> mediaController?.seekToPreviousMediaItem()
            PlayerAction.ToggleShuffle -> mediaController?.shuffleModeEnabled =
                !(mediaController?.shuffleModeEnabled ?: false)

            PlayerAction.CycleRepeatMode -> cycleRepeatMode()
            PlayerAction.ToggleFavorite -> toggleFavorite()
            PlayerAction.Back -> {
                viewModelScope.launch { _uiEvent.send(PlayerEvent.NavigateBack) }
            }

            PlayerAction.Share -> {
                val trackTitle = uiState.value.currentTrack?.title ?: "Check out this Song"
                viewModelScope.launch { _uiEvent.send(PlayerEvent.OpenShareDialog(songTitle = trackTitle)) }
            }

            is PlayerAction.PlayFromQueue -> queueManager.playSongAtIndex(
                mediaController,
                action.index
            )

            is PlayerAction.RemoveFromQueue -> queueManager.removeSongFromQueue(
                mediaController,
                action.index
            )

        }
    }

    // Listens for state changes from the MediaController and updates the UI state.
    private val playerListener = object : Player.Listener {

        // This function is called whenever the queue (timeline) changes,
        // for example, when new songs are added or removed.
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            Log.d(TAG, "Queue is updating")
            updateStateFromController()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateStateFromController()

//            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO || reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
//                _uiState.value.currentTrack?.let {
//                    fetchRecommendationsAndUpdateQueue(it)
//                }
//            }
            val currentIndex = mediaController?.currentMediaItemIndex ?: -1
            val queueSize = mediaController?.mediaItemCount ?: 0

            if (queueSize > 1 && (currentIndex == queueSize - 2 || currentIndex == queueSize - 1)) {
                val lastSongInQueue = _uiState.value.queue.lastOrNull()

                lastSongInQueue?.let { track ->
                    Log.d(
                        TAG,
                        "Last Song in queue : $lastSongInQueue and fetching the latest recommendation"
                    )
                    fetchRecommendationsAndUpdateQueue(track)

                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateStateFromController()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateStateFromController()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            updateStateFromController()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            updateStateFromController()
        }
    }

//    fun cleanToLettersOnlyUnicode(input: String): String {
//        return input
//            .lowercase()                          // make lowercase
//            .replace(Regex("[^\\p{L}]+"), " ")    // keep only letters from ANY script
//            .trim()
//    }

    fun cleanToLettersOnlyUnicode(input: String): String {
        return input
            .lowercase()
            .replace(Regex("[^\\p{L}\\p{M}]+"), " ")   // keep letters + vowel marks
            .replace(Regex("\\s+"), " ")               // collapse multiple spaces
            .trim()
    }

    private fun fetchRecommendationsAndUpdateQueue(track: SingleTrack) {
//        viewModelScope.launch {
//
//            val cleanTitle = cleanToLettersOnlyUnicode(track.title)
//            Log.d(TAG, "Fetching recommendations For : ${track.title}")
//            when (val result = songRepository.getRecommendations(songTitle = cleanTitle)) {
//                is Result.Success -> {
//                    Log.d(TAG, "Got ${result.data.size} recommendations updating queue")
//                    queueManager.replaceUpcomingQueue(mediaController, result.data)
//                }
//
//                is Result.Failure -> Log.d(TAG, "Error fetching recommendations : ${result.error}")
//            }
//        }
    }

    fun addSongToNextUp(track: SingleTrack) {
        queueManager.addSongToPlayNext(mediaController, track)
    }

    fun addSongToEndOfQueue(track: SingleTrack) {
        queueManager.addSongToQueue(mediaController, track)
    }

    // A single helper function to read all relevant data from the controller and update the state.
    private fun updateStateFromController() {
        mediaController?.let { controller ->

            val currentMediaItem = controller.currentMediaItem

            val currentTrack = currentMediaItem?.let {
                SingleTrack(
                    url = it.mediaId,
                    title = it.mediaMetadata.title?.toString() ?: "Unknown Title",
                    artistName = it.mediaMetadata.artist?.toString() ?: "Unknown Artist",
                    thumbnail = it.mediaMetadata.artworkUri?.toString() ?: "",
                    duration = controller.duration.coerceAtLeast(0L).toInt()
                )
            }

            // 3. Get the full queue directly from the controller
            val currentQueue = (0 until controller.mediaItemCount).map { index ->
                val item = controller.getMediaItemAt(index)
                SingleTrack( // Convert each MediaItem to a SingleTrack
                    url = item.mediaId,
                    title = item.mediaMetadata.title?.toString() ?: "",
                    artistName = item.mediaMetadata.artist?.toString() ?: "",
                    thumbnail = item.mediaMetadata.artworkUri?.toString() ?: "",
                    duration = 0L.toInt() // Duration isn't needed for the queue list display
                )
            }


            val currentRepeatMode = when (controller.repeatMode) {
                Player.REPEAT_MODE_OFF -> RepeatMode.OFF
                Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else -> RepeatMode.OFF
            }

            _uiState.update {
                it.copy(
                    currentTrack = currentTrack,
                    queue = currentQueue,
                    isPlaying = controller.isPlaying,
                    currentPosition = controller.currentPosition.coerceAtLeast(0L),
                    totalDuration = controller.duration.coerceAtLeast(0L),
                    bufferPosition = controller.bufferedPosition.coerceAtLeast(0L),
                    isLoading = controller.playbackState == Player.STATE_BUFFERING && !it.isReady,
                    isBuffering = controller.playbackState == Player.STATE_BUFFERING && it.isReady,
                    isReady = controller.playbackState == Player.STATE_READY,
                    repeatMode = currentRepeatMode,
                    shuffleModeEnabled = controller.shuffleModeEnabled
                )
            }
        }
    }


    private fun cycleRepeatMode() {
        val nextMode = when (mediaController?.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
        mediaController?.repeatMode = nextMode
    }

    private fun toggleFavorite() {
        // TODO: You can add your database logic here.
        // val currentTrackId = uiState.value.currentTrack?.videoId ?: return
        // viewModelScope.launch {
        //     songRepository.toggleFavorite(currentTrackId)
        // }
        // For now, we just toggle the UI state.
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
    }

    // Clean up the controller when the ViewModel is destroyed.
    override fun onCleared() {
        mediaController?.release()
        super.onCleared()
    }


}