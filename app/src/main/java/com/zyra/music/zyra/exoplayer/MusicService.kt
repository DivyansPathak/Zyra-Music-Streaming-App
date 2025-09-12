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
import com.zyra.music.zyra.domain.utils.onSuccess
import com.zyra.music.zyra.exoplayer.utils.CacheUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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
            // 1. Create a SettableFuture. This is our "promise" to the media session.
            val future: SettableFuture<List<MediaItem>> = SettableFuture.create()

            // 2. Launch the coroutine to do the network work.
            serviceScope.launch {
                try {
                    val updatedMediaItems = mutableListOf<MediaItem>()
                    mediaItems.forEach { mediaItem ->
                        val youtubeUrl = mediaItem.mediaId
                        val youtubeBase = "https://www.youtube.com/watch?v="
                        Log.d(TAG, "Fetching stream URL for: $youtubeUrl")
                        songRepository.getSong(youtubeBase+youtubeUrl).onSuccess { songResult ->
                            Log.d(TAG, "Successfully got stream URL: ${songResult.streamUrl}")
                            val newItem = mediaItem.buildUpon()
                                .setUri(songResult.streamUrl)
                                .build()
                            updatedMediaItems.add(newItem)
                        }
                    }
                    // 3. Once the loop is done, fulfill the promise by setting the future's result.
                    future.set(updatedMediaItems)
                } catch (e: Exception) {
                    // If something goes wrong, set an exception on the future.
                    Log.e(TAG, "Error fetching stream URLs", e)
                    future.setException(e)
                }
            }
            // 4. Immediately return the future. The MediaSession will now wait for it to be completed.
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
        serviceScope.cancel() // Cancel the coroutine scope
        super.onDestroy()
    }

}
