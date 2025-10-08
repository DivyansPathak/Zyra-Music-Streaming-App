package com.zyra.music.zyra.data.remote.dto.userPlaylist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPlaylistSongDto(
    @SerialName("playlist_id")
    val playlistId : String,
    @SerialName("song_id")
    val songId : String
)
