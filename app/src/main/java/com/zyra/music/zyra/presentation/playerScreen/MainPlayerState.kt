package com.zyra.music.zyra.presentation.playerScreen

import com.zyra.music.zyra.domain.model.SingleTrack

data class MainPlayerState(
    val currentTrack: SingleTrack? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val bufferPosition : Long = 0L,
    val isLoading: Boolean = false,
    val isBuffering: Boolean = false,
    val isReady: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffleModeEnabled: Boolean = false,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null,
    val queue : List<SingleTrack> = emptyList()
)