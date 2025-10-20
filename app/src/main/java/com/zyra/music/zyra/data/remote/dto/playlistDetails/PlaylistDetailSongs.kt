package com.zyra.music.zyra.data.remote.dto.playlistDetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDetailSongs(
    @SerialName("title")
    val title: String,
    @SerialName("artist_name")
    val artistName: String,
    @SerialName("video_id")
    val videoId: String,
    @SerialName("thumbnail")
    val thumbnail: String,
    @SerialName("duration")
    val duration: Int,
    @SerialName("artist_id")
    val artistId : String? = null,
    @SerialName("album_name")
    val albumName : String? = null,
    @SerialName("album_id")
    val albumId : String? = null,
)
