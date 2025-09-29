package com.zyra.music.zyra.presentation.playlistDetail

import com.zyra.music.zyra.domain.model.PlayList

data class PlaylistDetailState(
    val playlist : PlayList? = null,
    val isLoading : Boolean = false,
    val error : String? = null,
    val isPlaying : Boolean = false
)
