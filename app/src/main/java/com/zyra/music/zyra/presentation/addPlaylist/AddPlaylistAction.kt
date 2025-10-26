package com.zyra.music.zyra.presentation.addPlaylist

import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.model.TrackFullOne

interface AddPlaylistAction {
    data class SetSongAndShowSheet(val track : TrackFullOne) : AddPlaylistAction
    data class AddSongToPlaylist(val playlistId : Long) : AddPlaylistAction
    data class RemoveSongFromPlaylist(val playlistId : Long, val songId : String) : AddPlaylistAction
    data object ShowCreateDialog : AddPlaylistAction
    data object HideCreateDialog : AddPlaylistAction
    data class CreatePlaylistAndAddSong(val title : String, val description : String) : AddPlaylistAction

    data class ShowDeleteDialog(val playlist : LibraryPlaylist) : AddPlaylistAction
    data object ConfirmDelete : AddPlaylistAction
    data object HideDeleteDialog : AddPlaylistAction

}
