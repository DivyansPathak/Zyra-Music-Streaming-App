package com.zyra.music.zyra.presentation.playlistScreen

interface PlaylistEvent {
    data object NavigateBack : PlaylistEvent
    data class ShowMessage(val message : String) : PlaylistEvent
}