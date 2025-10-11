package com.zyra.music.zyra.presentation.addPlaylist

import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.model.TrackFullOne

data class AddPlaylistState(
    val playlists : List<LibraryPlaylist> = emptyList(),
    val songToAdd : TrackFullOne? = null,
    val isCreateDialogOpen : Boolean = false,
    val playlistToDelete : LibraryPlaylist? = null,
    val isLoading : Boolean = false
)
