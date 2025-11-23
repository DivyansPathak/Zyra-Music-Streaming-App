package com.zyra.music.zyra.domain.model

import com.zyra.music.zyra.presentation.playlistScreen.PlayListType

data class PlaylistDetails(
    val id : String,
    val title : String,
    val description : String,
    val coverImageUrl : String?,
    val tracks : List<TrackFullOne>,
    val type : PlayListType
)
