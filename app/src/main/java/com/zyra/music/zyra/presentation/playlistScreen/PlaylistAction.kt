package com.zyra.music.zyra.presentation.playlistScreen

interface PlaylistAction {
    data object PlayPausePlaylist : PlaylistAction
    data object OnBackClicked : PlaylistAction
    data object OnShuffleClicked : PlaylistAction
    data class OnSongClicked(val index : Int) : PlaylistAction
}