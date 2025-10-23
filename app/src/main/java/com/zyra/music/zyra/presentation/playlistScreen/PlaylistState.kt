package com.zyra.music.zyra.presentation.playlistScreen

import com.zyra.music.zyra.domain.model.PlaylistDetails
import com.zyra.music.zyra.domain.model.TrackFullOne

data class PlaylistState(
    val isLoading : Boolean = false,
    val playlistDetails: PlaylistDetails? = null,
    val error : String? = null
)

