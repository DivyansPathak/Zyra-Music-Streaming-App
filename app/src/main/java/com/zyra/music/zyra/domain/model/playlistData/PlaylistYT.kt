package com.zyra.music.zyra.domain.model.playlistData


data class PlaylistYT(
    val title : String,
    val playlistId : String,
    val author : String? = null,
    val itemCount : Int,
    val thumbnail : String
)
