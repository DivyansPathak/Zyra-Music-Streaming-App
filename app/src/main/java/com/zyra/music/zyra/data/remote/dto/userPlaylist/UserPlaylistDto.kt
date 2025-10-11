package com.zyra.music.zyra.data.remote.dto.userPlaylist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPlaylistDto(
    val id: Long? = null, // Nullable when creating a new playlist
    @SerialName("user_id")
    val userId: String? = null,
    val name: String,
    val description : String
)