package com.zyra.music.zyra.exoplayer.download//package com.zyra.music.zyra.exoplayer.download
//
//import android.content.Context
//import android.util.Log
//import androidx.media3.exoplayer.offline.Download
//import androidx.media3.exoplayer.offline.DownloadManager
//import androidx.media3.exoplayer.offline.DownloadRequest
//import androidx.media3.exoplayer.offline.DownloadService
//import com.zyra.music.zyra.domain.model.TrackFullOne
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import java.util.concurrent.Executor
//
//private const val TAG = "ZyraDownloadManager"
//class ZyraDownloadManager(context : Context) {
//    // A flow to hold the download status of all tracks, mapped by their videoId.
//    private val _downloadStatusFlow = MutableStateFlow<Map<String, DownloadState>>(emptyMap())
//    val downloadStatusFlow = _downloadStatusFlow.asStateFlow()
//
//    private val downloadManager: DownloadManager = DownloadService.getDownloadManager(
//        context,
//        ExoDownloadService::class.java, // Your Media3 DownloadService class
//        Executor(Runnable::run) // A direct executor is fine for this listener
//    )
//
//    init {
//        // Load initial statuses and add a listener to get real-time updates.
//        loadDownloads()
//        downloadManager.addListener(object : DownloadManager.Listener {
//            override fun onDownloadsChanged(downloadManager: DownloadManager, downloads: List<Download>) {
//                updateDownloadStatuses(downloads)
//            }
//        })
//    }
//
//    // Public function to start a download
//    fun startDownload(track: TrackFullOne) {
//        val downloadRequest = DownloadRequest.Builder(track.videoId, track.thumbnail.toUri()).build()
//        DownloadService.sendAddDownload(
//            context,
//            ExoDownloadService::class.java,
//            downloadRequest,
//            false // false = not a foreground service request
//        )
//        Log.d(TAG, "Download requested for: ${track.title}")
//    }
//
//    // Public function to remove a download
//    fun removeDownload(videoId: String) {
//        DownloadService.sendRemoveDownload(
//            context,
//            ExoDownloadService::class.java,
//            videoId,
//            false // false = not a foreground service request
//        )
//        Log.d(TAG, "Removal requested for videoId: $videoId")
//    }
//
//    // Loads the initial state of all downloads.
//    private fun loadDownloads() {
//        val downloads = downloadManager.currentDownloads
//        updateDownloadStatuses(downloads)
//    }
//
//    // Helper to convert Media3's state to our simple enum and update the flow.
//    private fun updateDownloadStatuses(downloads: List<Download>) {
//        val newStatusMap = downloads.associate { download ->
//            val status = when (download.state) {
//                Download.STATE_QUEUED -> DownloadState.QUEUED
//                Download.STATE_DOWNLOADING -> DownloadState.DOWNLOADING
//                Download.STATE_COMPLETED -> DownloadState.COMPLETED
//                Download.STATE_FAILED -> DownloadState.FAILED
//                Download.STATE_REMOVING -> DownloadState.REMOVING
//                else -> DownloadState.NOT_DOWNLOADED
//            }
//            download.request.id to status
//        }
//        _downloadStatusFlow.value = newStatusMap
//        Log.d(TAG, "Download statuses updated: $newStatusMap")
//    }
//
//}