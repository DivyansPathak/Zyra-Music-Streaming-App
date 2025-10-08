package com.zyra.music.zyra.domain.model

import com.zyra.music.zyra.navigation.PlayListType

data class LibraryPlaylist(
    val id : String, // here also
    val name : String,
    val creator : String,
    val imageUrl : String,
    val trackCount : Long,
    val playlistType : PlayListType,
)
