package com.zyra.music.zyra.data.remote.dto.favoriteDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LibraryPlaylistDto(
    val id : String, // i changed it from String to Long
    val title : String,
    val subtitle : String?,
    @SerialName("thumbnail_song_id")
    val thumbnailSongId : String?,
    @SerialName("track_count")
    val trackCount : Long,
    @SerialName("playlist_type")
    val playlistType : String,
)
