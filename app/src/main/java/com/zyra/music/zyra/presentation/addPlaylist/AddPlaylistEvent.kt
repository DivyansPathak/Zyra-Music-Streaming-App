package com.zyra.music.zyra.presentation.addPlaylist

interface AddPlaylistEvent {
    data class ShowMessage(val message : String) : AddPlaylistEvent
    data class ShowToast(val message : String) : AddPlaylistEvent
}