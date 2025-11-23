package com.zyra.music.zyra.data.remote.dto.playlistFromYoutubeDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    @SerialName("title")
    val title : String,
    @SerialName("playlistId")
    val playlistId : String,
    @SerialName("author")
    val author : String? = null,
    @SerialName("itemCount")
    val itemCount : Int? = null,
    @SerialName("thumbnail")
    val thumbnail : String
)
