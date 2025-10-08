package com.zyra.music.zyra.presentation.libraryScreen

import com.zyra.music.zyra.domain.model.LibraryPlaylist

data class LibraryState(
    val isLoading : Boolean = false,
    val playlists : List<LibraryPlaylist> = emptyList(),
    val error : String? = null,
    val selectedScreen : LibraryScreenType = LibraryScreenType.PLAYLISTS
)
