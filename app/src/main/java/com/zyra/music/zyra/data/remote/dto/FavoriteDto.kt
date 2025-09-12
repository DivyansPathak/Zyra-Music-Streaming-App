package com.zyra.music.zyra.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("song_id")
    val songId: String
)
