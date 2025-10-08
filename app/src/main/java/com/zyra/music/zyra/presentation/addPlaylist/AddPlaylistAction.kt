package com.zyra.music.zyra.presentation.addPlaylist

import com.zyra.music.zyra.domain.model.TrackFullOne

interface AddPlaylistAction {
    data class SetSongAndShowSheet(val track : TrackFullOne) : AddPlaylistAction
    data class AddSongToPlaylist(val playlistId : String) : AddPlaylistAction
    data object ShowCreateDialog : AddPlaylistAction
    data object HideCreateDialog : AddPlaylistAction
    data class CreatePlaylistAndAddSong(val title : String, val description : String) : AddPlaylistAction
}
