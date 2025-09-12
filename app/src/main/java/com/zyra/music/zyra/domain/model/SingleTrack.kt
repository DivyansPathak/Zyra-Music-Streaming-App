package com.zyra.music.zyra.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SingleTrack(
    val title : String,
    val artistName : String? = null,
    val url : String,
    val thumbnail : String,
    val duration : Int
)
