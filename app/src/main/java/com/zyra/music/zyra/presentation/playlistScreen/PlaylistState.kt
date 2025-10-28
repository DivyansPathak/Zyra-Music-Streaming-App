package com.zyra.music.zyra.presentation.playlistScreen

import com.zyra.music.zyra.domain.model.PlaylistDetails
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.addPlaylist.AddPlaylistAction

data class PlaylistState(
    val isLoading : Boolean = false,
    val playlistDetails: PlaylistDetails? = null,
    val showDeleteDialog: Boolean = false,
    val error : String? = null
)

