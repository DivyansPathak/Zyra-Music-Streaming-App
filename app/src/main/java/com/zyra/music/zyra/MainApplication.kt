package com.zyra.music.zyra

import android.app.Application
import com.zyra.music.zyra.di.koinModule
import com.zyra.music.zyra.exoplayer.KtorDownloader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.Localization
import java.util.Locale

class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        val localization = Localization.fromLocale(Locale.getDefault())
        // Initialize the NewPipe Extractor library once when the app starts.
        // This sets up the extractor to use your custom KtorDownloader for all its network requests.
        NewPipe.init(KtorDownloader.getInstance(), localization)

        // Initialize Koin for dependency injection.
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(koinModule)
        }
    }
}
