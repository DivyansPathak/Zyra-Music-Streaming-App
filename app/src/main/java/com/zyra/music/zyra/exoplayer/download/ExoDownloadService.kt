package com.zyra.music.zyra.exoplayer.download//package com.zyra.music.zyra.exoplayer.download
//
//import androidx.media3.exoplayer.offline.DownloadService
//import com.zyra.music.zyra.R
//
//@androidx.media3.common.util.UnstableApi
//class ExoDownloadService: DownloadService(
//    FOREGROUND_NOTIFICATION_ID,
//    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
//    "download_channel",
//    R.string.download_channel_name,
//    R.string.download_channel_description
//) {
//    override fun getDownloadManager(): androidx.media3.exoplayer.offline.DownloadManager {
//        return DownloadUtil.getDownloadManager(this)
//    }
//
//    override fun getDownloadNotificationHelper(): DownloadNotificationHelper {
//        return DownloadUtil.getDownloadNotificationHelper(this)
//    }
//}