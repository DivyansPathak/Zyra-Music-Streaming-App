package com.zyra.music.zyra.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongResultDto(
    @SerialName("stream_url")
    val streamUrl: String,
)
