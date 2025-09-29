package com.zyra.music.zyra.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    @SerialName("videoId")
    val videoId: String,
    @SerialName("title")
    val title: String,
    @SerialName("artist_name")
    val artistName: String?,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String?,
    @SerialName("duration")
    val duration: Int?,
    @SerialName("artist_id")
    val artistId : String? = null,
    @SerialName("album_name")
    val albumName : String? = null,
    @SerialName("albumId")
    val albumId : String? = null,
)
