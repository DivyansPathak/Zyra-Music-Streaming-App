package com.zyra.music.zyra.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThumbnailDto(
    @SerialName("")
    val thumbnailUrl: String,
)
