package com.zyra.music.zyra.presentation.playlistDetail

import com.zyra.music.zyra.domain.model.TrackFullOne

interface PlaylistDetailAction {

    data object PlayPlaylist : PlaylistDetailAction
    data object ShufflePlaylist : PlaylistDetailAction
    data object NavigateBack : PlaylistDetailAction
    data class PlaySongAtIndex(val index: Int) : PlaylistDetailAction
    data class AddSongToQueue(val track: TrackFullOne) : PlaylistDetailAction
    data class AddSongToNextPlay(val track: TrackFullOne) : PlaylistDetailAction
}