package com.zyra.music.zyra.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TrackFullOne(
    val title: String,
    val artistName: String,
    val videoId: String,
    val thumbnail: String,
    val duration: Int,
    val artistId : String,
    val albumName : String,
    val albumId : String,
    val queueInstanceId : String = UUID.randomUUID().toString()
)
