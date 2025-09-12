package com.zyra.music.zyra.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface PlayListIdentifier {
    @Serializable
    data class Present(val id: String) : PlayListIdentifier

    @Serializable
    data class Album(val id: String) : PlayListIdentifier

    @Serializable
    data class TopArtists(val artistId: String) : PlayListIdentifier

    @Serializable
    data object Favorite : PlayListIdentifier
}