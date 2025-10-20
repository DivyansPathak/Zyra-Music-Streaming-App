package com.zyra.music.zyra.data.remote.dto.favoriteDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LibraryPlaylistDto(
    val id : Long, // i changed it from String to Long
    val title : String,
    val subtitle : String?,
    val thumbnail : String?,
    @SerialName("track_count")
    val trackCount : Long,
    @SerialName("playlist_type")
    val playlistType : String,
)
