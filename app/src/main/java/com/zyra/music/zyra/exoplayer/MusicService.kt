package com.zyra.music.zyra.exoplayer

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.upstream.DefaultAllocator
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.Result
import com.zyra.music.zyra.domain.utils.onSuccess
import com.zyra.music.zyra.exoplayer.utils.CacheUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

private const val TAG = "MusicService"

@androidx.media3.common.util.UnstableApi
class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private val songRepository: SongRepository by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // The MediaSession.Callback is the key to linking our repository to the player.
    private val callback = object : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {

            val future: SettableFuture<List<MediaItem>> = SettableFuture.create()

            val youtubeBase = "https://www.youtube.com/watch?v="

            serviceScope.launch {
                try {
                    val differentMediaItems = mediaItems.map { mediaItem ->
                        async(Dispatchers.IO) {
                            Log.d(TAG, "Fetching the url of ${mediaItem.mediaMetadata.title}")
                            songRepository.getSong(youtubeBase + mediaItem.mediaId)
                        }
                    }

                    val songResults = differentMediaItems.awaitAll()

                    val updatedMediaItems = mediaItems.zip(songResults) { mediaItem, songResult ->
                        when (songResult) {
                            is Result.Success -> {
                                mediaItem.buildUpon()
                                    .setUri(songResult.data.streamUrl)
                                    .build()
                            }

                            is Result.Failure -> {
                                Log.e(
                                    TAG,
                                    "Error fetching song for ${mediaItem.mediaMetadata.title}"
                                )
                                mediaItem
                            }
                        }
                    }

                    future.set(updatedMediaItems)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching stream URLs : ${e.message}", e)
                    future.setException(e)
                }
            }

            return future
        }
    }

    override fun onCreate() {
        super.onCreate()

        // --- CACHING SETUP ---
        val mediaCache = CacheUtils.getInstance(this)
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(mediaCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(cacheDataSourceFactory)


        // 1. Create a custom LoadControl with larger buffer sizes
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, 16 * 1024))
            .setBufferDurationsMs(
                32 * 1024,  // minBufferMs - Minimum duration of media that must be buffered.
                64 * 1024,  // maxBufferMs - Maximum duration of media that can be buffered.
                1500,       // bufferForPlaybackMs - Duration of media needed to start playback.
                2000        // bufferForPlaybackAfterRebufferMs - Duration needed to resume after a rebuffer.
            )
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()


        val player: Player = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(callback) // Set the custom callback here
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            this.player.release()
            release()
            mediaSession = null
        }
        serviceScope.cancel()
        super.onDestroy()
    }

}


//            val isNewQueue = mediaSession.player.mediaItemCount == 0
//
//            if(isNewQueue){
//            serviceScope.launch {
//
////                val firstItem = mediaItems.firstOrNull() ?: return@launch
//
////                Log.d(TAG, "Fetching stream URL for tapped song :${firstItem.mediaMetadata.title}")
////                songRepository.getSong("https://www.youtube.com/watch?v=${firstItem.mediaId}").onSuccess { songResult ->
////                    val updatedFirstItem = firstItem.buildUpon().setUri(songResult.streamUrl).build()
////                    mediaSession.player.replaceMediaItem(0,updatedFirstItem)
////                }
////
////                for (i in 1 until mediaItems.size){
////                    val mediaItem = mediaItems[i]
////                    Log.d(TAG, "Fetching stream URL for:${mediaItem.mediaMetadata.title}")
////                    songRepository.getSong("https://www.youtube.com/watch?v=${mediaItem.mediaId}").onSuccess { songResult ->
////                        val updateItem = mediaItem.buildUpon().setUri(songResult.streamUrl).build()
////                        mediaSession.player.replaceMediaItem(i,updateItem)
////                    }
////                }
//                val updatedItems = mediaItems.toMutableList()
//
//                if (mediaItems.isNotEmpty()){
//                    val firstItem = mediaItems[0]
//                    Log.d(TAG, "Fetching stream URL for tapped song :${firstItem.mediaMetadata.title}")
//                    val firstSongResult = songRepository.getSong(youtubeUrl+firstItem.mediaId)
//                    firstSongResult.onSuccess { songResult ->
//                        updatedItems[0] = firstItem.buildUpon().setUri(songResult.streamUrl).build()
//                    }
//                }
//                future.set(updatedItems)
//
//                mediaItems.drop(1).forEachIndexed{index,mediaItem ->
//                    Log.d(TAG, "Fetching stream URL for:${mediaItem.mediaMetadata.title}")
//                    val mediaItemResult = songRepository.getSong(youtubeUrl+mediaItem.mediaId)
//                    mediaItemResult.onSuccess { songResult ->
//                        val updated = mediaItem.buildUpon().setUri(songResult.streamUrl).build()
//                        withContext(Dispatchers.Main){
//                            mediaSession.player.replaceMediaItem(index +1,updated)
//                        }
//                    }
//                }
//            }
//            } else{
//                Log.d(TAG, "Handling an ADD request to the existing queue")
//                val startIndex = mediaSession.player.mediaItemCount
//                future.set(mediaItems)
//                serviceScope.launch(Dispatchers.IO){
//                    mediaItems.forEachIndexed { index, mediaItem ->
//                        Log.d(TAG,"Background fetching for Added song : ${mediaItem.mediaMetadata.title}")
//                        songRepository.getSong(youtubeUrl+mediaItem.mediaId).onSuccess { songResult ->
//                            val updated = mediaItem.buildUpon().setUri(songResult.streamUrl).build()
//                            withContext(Dispatchers.Main) {
//                                mediaSession.player.replaceMediaItem(startIndex,updated)
//                            }
//                        }
//                    }
//                }
//            }