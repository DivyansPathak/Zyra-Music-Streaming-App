package com.zyra.music.zyra.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingleTrackDto(
    @SerialName("name")
    val title: String,
    @SerialName("artist_name")
    val artistName: String? = null,
    @SerialName("url")
    val url: String = "https://picsum.photos/200",
    @SerialName("thumbnail")
    val thumbnail: String,
    @SerialName("duration")
    val duration: Int
)
