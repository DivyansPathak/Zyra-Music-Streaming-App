package com.zyra.music.zyra.domain.model

import com.zyra.music.zyra.presentation.playlistScreen.PlayListType

data class PlayList(
    val id : String,
    val title : String,
    val subtitle : String,
    val thumbnail : String,
    val songs : List<TrackFullOne> = emptyList(),
    val genre : String,
    val type : PlayListType
)
