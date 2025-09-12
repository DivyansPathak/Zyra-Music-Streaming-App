package com.zyra.music.zyra.domain.model

data class PlayList(
    val id : String,
    val title : String,
    val subtitle : String,
    val thumbnail : String,
    val songs : List<SingleTrack> = emptyList(),
    val genre : String
)
