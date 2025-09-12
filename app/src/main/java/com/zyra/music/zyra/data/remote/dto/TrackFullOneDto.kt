package com.zyra.music.zyra.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.IDN

@Serializable
data class TrackFullOneDto(
    @SerialName("title")
    val title: String,
    @SerialName("artist_name")
    val artistName: String,
    @SerialName("videoId")
    val videoId: String,
    @SerialName("thumbnail")
    val thumbnail: String,
    @SerialName("duration")
    val duration: Int,
    @SerialName("artistId")
    val artistId : String? = null,
    @SerialName("album_name")
    val albumName : String? = null,
    @SerialName("albumId")
    val albumId : String? = null,
)
