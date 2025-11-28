package com.zyra.music.zyra.exoplayer

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.upstream.DefaultAllocator
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.Result
import com.zyra.music.zyra.exoplayer.utils.CacheUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

private const val TAG = "MusicService"

@androidx.media3.common.util.UnstableApi
class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private val songRepository: SongRepository by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

        val mediaCache = CacheUtils.getInstance(this)
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(mediaCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(cacheDataSourceFactory)


        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, 16 * 1024))
            .setBufferDurationsMs(
                10_000,
                30_000,
                1000,
                2000
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
