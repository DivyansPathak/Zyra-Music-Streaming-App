package com.zyra.music.zyra.data.remote.dto

import com.zyra.music.zyra.presentation.playlistScreen.PlayListType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrePlaylistDto(
    val id : String,
    val title : String,
    @SerialName("description")
    val subtitle : String,
    @SerialName("cover_image_url")
    val thumbnail : String,
    val tracks : List<TrackDto>,
    val genre : String,
    val type : PlayListType
)
