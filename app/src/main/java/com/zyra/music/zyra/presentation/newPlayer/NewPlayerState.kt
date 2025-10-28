package com.zyra.music.zyra.presentation.newPlayer

import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.newPlayer.component.RepeatMode
import com.zyra.music.zyra.exoplayer.download.DownloadState

data class NewPlayerState(
    val currentTrack : TrackFullOne? = null,
    val isPlaying : Boolean = false,
    val currentPosition : Long = 0L,
    val totalDuration : Long = 0L,
    val bufferPosition : Long = 0L,
    val isLoading : Boolean = false,
    val isBuffering : Boolean = false,
    val isReady : Boolean = false,
    val repeatMode : RepeatMode = RepeatMode.OFF,
    val shuffleModeEnabled : Boolean = false,
    val isFavorite : Boolean = false,
    val errorMessage : String? = null,
    val queue : List<TrackFullOne> = emptyList(),
    val favoriteIds : Set<String> = emptySet(),
    val playbackMode: PlaybackMode = PlaybackMode.PLAYLIST,
    val isManuallyTriggered : Boolean = false,
//    val downloadStatus : DownloadState? = DownloadState.NOT_DOWNLOADED,
    val sleepTimeRemaining : Long? = null,
    val isEndTrackTimerActive : Boolean = false,
    val toggleAutoPlay : Boolean = true
)
