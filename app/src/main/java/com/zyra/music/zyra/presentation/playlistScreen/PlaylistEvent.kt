package com.zyra.music.zyra.presentation.playlistScreen

import com.zyra.music.zyra.domain.model.TrackFullOne

interface PlaylistEvent {
    data object NavigateBack : PlaylistEvent
    data class ShowMessage(val message : String) : PlaylistEvent
    data object DeletePlaylist : PlaylistEvent
}