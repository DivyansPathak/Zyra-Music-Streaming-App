package com.zyra.music.zyra.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequestBody(
    val queries: List<String>
)
