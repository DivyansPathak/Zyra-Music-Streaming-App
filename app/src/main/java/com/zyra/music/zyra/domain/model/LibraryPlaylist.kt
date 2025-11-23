package com.zyra.music.zyra.domain.model

import com.zyra.music.zyra.presentation.playlistScreen.PlayListType

data class LibraryPlaylist(
    val id : Long, // here also
    val name : String,
    val creator : String,
    val imageUrl : String,
    val trackCount : Long,
    val playlistType : PlayListType,
)
