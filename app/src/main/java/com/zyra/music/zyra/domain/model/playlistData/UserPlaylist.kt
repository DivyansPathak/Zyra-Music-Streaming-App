package com.zyra.music.zyra.domain.model.playlistData

data class UserPlaylist(
    val id: String? = null, // Nullable when creating a new playlist
    val userId: String? = null,
    val name: String,
    val description : String
)
