package com.zyra.music.zyra.navigation

import kotlinx.serialization.Serializable

@Serializable
enum class PlayListType {
    // Fetched from your API Supabase
    PRESET,

    // Fetched from Supabase for the logged-in user
    FAVORITES,
    USER_CREATED
}