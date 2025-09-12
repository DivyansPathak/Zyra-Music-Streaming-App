package com.zyra.music.zyra.presentation.newPlayer

import android.content.ComponentName
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.zyra.music.zyra.data.remote.SupabaseClient.supabase
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.Result
import com.zyra.music.zyra.domain.utils.onFailure
import com.zyra.music.zyra.domain.utils.onSuccess
import com.zyra.music.zyra.exoplayer.MusicService
import com.zyra.music.zyra.exoplayer.NewMusicQueueManager
import com.zyra.music.zyra.presentation.playerScreen.RepeatMode
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "NewMusicViewModel"

@androidx.media3.common.util.UnstableApi
class MainMusicViewModel(
    private val repository: SongRepository,
    private val queueManager: NewMusicQueueManager,
    context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewPlayerState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<NewPlayerEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var mediaController: MediaController? = null

    private var sleepTimer: CountDownTimer? = null

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
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        Log.d(TAG, "User is authenticated. Fetching favorite...")
                        fetchUserFavorites()
                    }

                    is SessionStatus.NotAuthenticated -> {
                        Log.d(TAG, "User is not authenticated. Clearing local favorites.")
                        _uiState.update { it.copy(favoriteIds = emptySet(), isFavorite = false) }

                    }

                    else -> {}
                }
            }
        }

    }

    fun playPlayList(tracks: List<TrackFullOne>, startIndex: Int = 0, shuffle: Boolean) {
        _uiState.update {
            it.copy(
                playbackMode = PlaybackMode.PLAYLIST,
                isManuallyTriggered = true
            )
        }
        queueManager.setQueueAndPlay(
            mediaController,
            tracks = tracks,
            startIndex = startIndex,
            shuffle = shuffle
        )
    }

    fun playRadioForSong(clickedTrack: TrackFullOne) {
        _uiState.update { it.copy(playbackMode = PlaybackMode.RADIO, isManuallyTriggered = true) }
        queueManager.setQueueAndPlay(mediaController, tracks = listOf(clickedTrack), startIndex = 0)
        viewModelScope.launch {
            delay(1500L)
            fetchRecommendationsAndUpdateQueue(clickedTrack)
        }
    }

    fun addSongToPlayNext(track: TrackFullOne) {
        queueManager.addSongToPlayNext(mediaController, track)
        viewModelScope.launch {
            _uiEvent.send(NewPlayerEvent.ShowMessage("${track.title} is added to next"))
        }
    }

    fun addSongToQueue(track: TrackFullOne) {
        queueManager.addSongToQueue(mediaController, track)
        viewModelScope.launch {
            _uiEvent.send(NewPlayerEvent.ShowMessage("${track.title} is added to queue"))
        }
    }

    // This is the core logic for the timer.
    private fun startSleepTimer(durationInMillis: Long) {
        // Cancel any previous timer before starting a new one.
        sleepTimer?.cancel()

        sleepTimer = object : CountDownTimer(durationInMillis, 1000) {
            // This is called every second.
            override fun onTick(millisUntilFinished: Long) {
                _uiState.update { it.copy(sleepTimeRemaining = millisUntilFinished) }
            }

            // This is called when the timer finishes.
            override fun onFinish() {
                mediaController?.pause() // Stop the music
                _uiState.update {
                    it.copy(
                        sleepTimeRemaining = null,
                        isEndTrackTimerActive = false
                    )
                }
                sleepTimer = null
            }
        }.start()

        // Immediately update the UI to show the initial time.
        _uiState.update { it.copy(sleepTimeRemaining = durationInMillis) }
    }

    // --- ADDED ---
    // A helper function to handle the "End of Track" case.
    private fun setTimerToEndOfTrack() {
        val remainingTime = _uiState.value.totalDuration - _uiState.value.currentPosition
        if (remainingTime > 0) {
            startSleepTimer(remainingTime)
        }
        _uiState.update { it.copy(isEndTrackTimerActive = true) }
    }

    fun onAction(action: NewPlayerAction) {
        when (action) {
            NewPlayerAction.PlayPause -> {
                Log.d(TAG, "Action received: PlayPause")
                if (mediaController?.isPlaying == true) mediaController?.pause() else mediaController?.play()
            }

            is NewPlayerAction.SeekTo -> {
                mediaController?.seekTo(action.position.toLong())

                if (sleepTimer != null && uiState.value.sleepTimeRemaining != null) {
                    val remainingTime = uiState.value.totalDuration - action.position.toLong()
                    if (remainingTime > 0) {
                        startSleepTimer(remainingTime)
                    }
                }
            }

            NewPlayerAction.SkipToNext -> {
                mediaController?.seekToNextMediaItem()
                mediaController?.play()
            }

            NewPlayerAction.SkipToPrevious -> {
                mediaController?.seekToPreviousMediaItem()
                mediaController?.play()
            }

            NewPlayerAction.ToggleShuffle -> mediaController?.shuffleModeEnabled =
                !(mediaController?.shuffleModeEnabled ?: false)

            NewPlayerAction.CycleRepeatMode -> cycleRepeatMode()
            NewPlayerAction.ToggleFavorite -> toggleToFavorite()
            is NewPlayerAction.PlayFromQueue -> queueManager.playSongAtIndex(
                mediaController,
                action.index
            )

            is NewPlayerAction.RemoveFromQueue -> queueManager.removeSongFromQueue(
                mediaController,
                action.index
            )

            NewPlayerAction.ExpandPlayer -> _uiState.update { it.copy(playerState = PlayerDraggableState.EXPANDED) }
            NewPlayerAction.CollapsePlayer -> _uiState.update { it.copy(playerState = PlayerDraggableState.COLLAPSED) }
            NewPlayerAction.Back -> {
                viewModelScope.launch { _uiEvent.send(NewPlayerEvent.NavigateToBack) }
            }

            NewPlayerAction.Share -> {
                val trackTitle = uiState.value.currentTrack?.title ?: "Check out this Song"
                viewModelScope.launch { _uiEvent.send(NewPlayerEvent.OpenShareDialog(songTitle = trackTitle)) }
            }

            NewPlayerAction.DownloadCurrentTrack -> _uiState.value.currentTrack?.let { }
            NewPlayerAction.RemoveDownloadFromCurrentTrack -> _uiState.value.currentTrack?.let { }

            is NewPlayerAction.SetSleepTimer -> {
                val durationInMillis = action.durationInMinutes * 60 * 1000
                startSleepTimer(durationInMillis)
            }

            is NewPlayerAction.SetSleepTimerToEndOfTrack -> {
                setTimerToEndOfTrack()
            }

            is NewPlayerAction.CancelSleepTimer -> {
                sleepTimer?.cancel()
                _uiState.update { it.copy(sleepTimeRemaining = null) }
                sleepTimer = null
            }

        }
    }

    fun onTrackManuallyTriggered() {
        _uiState.update { it.copy(isManuallyTriggered = false) }
    }

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateStateFromController()
            handleProactiveFetching()

        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            updateStateFromController()

        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            Log.d(TAG, "Queue is updating")
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

    private fun handleProactiveFetching() {
        if (_uiState.value.playbackMode != PlaybackMode.RADIO) return

        val controller = mediaController ?: return
        val queueSize = controller.mediaItemCount
        val currentIndex = controller.currentMediaItemIndex

        if (queueSize > 1 && currentIndex >= queueSize - 2) {
            _uiState.value.queue.lastOrNull()?.let { track ->
                Log.d(TAG, "Queue nearing end. Proactively fetching recommendation ")
                fetchRecommendationsAndUpdateQueue(track)
            }
        }
    }

    private fun fetchRecommendationsAndUpdateQueue(track: TrackFullOne) {
        viewModelScope.launch {
            when (val result = repository.getUpNext(track.videoId)) {
                is Result.Success -> queueManager.replaceUpComingQueue(mediaController, result.data)
                is Result.Failure -> Log.e(TAG, "Error fetching recommendations : ${result.error}")
            }
        }
    }

    private fun fetchUserFavorites() {
        viewModelScope.launch {
            repository.getFavoriteIds()
                .onSuccess { ids ->
                    _uiState.update { it.copy(favoriteIds = ids) }
                    updateIsFavoriteStatus()
                }
                .onFailure { error ->
                    Log.e(TAG, "Error fetching favorites: $error")
                }
        }
    }

    // A helper function to call from fetchUserFavorites
    private fun updateIsFavoriteStatus() {
        val currentTrackId = _uiState.value.currentTrack?.videoId ?: return
        val isFavorite = _uiState.value.favoriteIds.contains(currentTrackId)
        _uiState.update { it.copy(isFavorite = isFavorite) }
    }

    private fun updateStateFromController() {
        mediaController?.let { controller ->

            val currentMediaItem = controller.currentMediaItem
            val currentTrack = currentMediaItem?.let {
                TrackFullOne(
                    videoId = it.mediaId,
                    title = it.mediaMetadata.title?.toString() ?: "Unknown Title",
                    artistName = it.mediaMetadata.artist?.toString() ?: "Unknown Artist",
                    thumbnail = it.mediaMetadata.artworkUri?.toString() ?: "",
                    duration = controller.duration.coerceAtLeast(0L).toInt(),
                    artistId = it.mediaMetadata.artist?.toString() ?: "",
                    albumName = it.mediaMetadata.albumTitle?.toString() ?: "Unknown Album",
                    albumId = it.mediaMetadata.albumTitle?.toString() ?: "",

                    )
            }
            val currentQueue = (0 until controller.mediaItemCount).map { index ->
                val item = controller.getMediaItemAt(index)
                TrackFullOne(
                    videoId = item.mediaId,
                    title = item.mediaMetadata.title?.toString() ?: "",
                    artistName = item.mediaMetadata.artist?.toString() ?: "",
                    thumbnail = item.mediaMetadata.artworkUri?.toString() ?: "",
                    duration = 0L.toInt(),
                    artistId = item.mediaMetadata.artist?.toString() ?: "",
                    albumId = item.mediaMetadata.albumTitle?.toString() ?: "",
                    albumName = item.mediaMetadata.albumTitle?.toString() ?: "Unknown Album"
                )
            }
            val currentDownloadStatus = controller?.let {

            }

            _uiState.update {
                val isCurrentlyFavorite = it.favoriteIds.contains(currentTrack?.videoId)
                it.copy(
                    currentTrack = currentTrack,
                    queue = currentQueue,
                    isPlaying = controller.isPlaying,
                    currentPosition = controller.currentPosition.coerceAtLeast(0L),
                    totalDuration = controller.duration.coerceAtLeast(0L),
                    bufferPosition = controller.bufferedPosition.coerceAtLeast(0L),
                    isLoading = controller.playbackState == Player.STATE_BUFFERING && !it.isPlaying,
                    isBuffering = controller.playbackState == Player.STATE_BUFFERING && it.isPlaying,
                    isReady = controller.playbackState == Player.STATE_READY,
                    repeatMode = when (controller.repeatMode) {
                        Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                        Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                        else -> RepeatMode.OFF
                    },
                    shuffleModeEnabled = controller.shuffleModeEnabled,
                    isFavorite = isCurrentlyFavorite

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

    private fun toggleToFavorite() {
        val currentTrackId = uiState.value.currentTrack?.videoId ?: return
        val isCurrentlyFavorite = uiState.value.isFavorite
        val currentFavoriteIds = uiState.value.favoriteIds

        // 1. Create the new "Memory" (the updated set) optimistically.
        val newFavoriteIds = if (isCurrentlyFavorite) {
            currentFavoriteIds - currentTrackId
        } else {
            currentFavoriteIds + currentTrackId
        }

        // 2. Update both the "Display" (isFavorite) and the "Memory" (favoriteIds) at the same time.
        _uiState.update {
            it.copy(
                isFavorite = !isCurrentlyFavorite,
                favoriteIds = newFavoriteIds
            )
        }
        viewModelScope.launch {
            val result = if (isCurrentlyFavorite) {
                repository.removeFavorite(currentTrackId)
            } else {
                repository.addFavorite(currentTrackId)
            }

            result.onFailure { error ->
                Log.e(TAG, "Failed to toggle favorite: $error")
                // Optionally send a UI event to show an error message
            }
        }
    }

    override fun onCleared() {
        sleepTimer?.cancel()
        mediaController?.release()
        super.onCleared()
    }

}