package com.zyra.music.zyra.exoplayer.utils

import android.content.Context
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@androidx.media3.common.util.UnstableApi
object CacheUtils {

    @Volatile
    private var instance : SimpleCache? = null

    fun getInstance(context : Context) : SimpleCache{
        return instance ?: synchronized(this) {
            instance ?: run {
                val cacheDir = File(context.cacheDir,"media_cache")
                val dataBaseProvider = StandaloneDatabaseProvider(context)
                val evictor = LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024)
                SimpleCache(cacheDir,evictor,dataBaseProvider).also {
                    instance = it
                }

            }
        }
    }

    fun release() {
        synchronized(this) {
            // 🔹 Call this in Application.onTerminate() or similar
            instance?.release()
            instance = null
        }
    }

}