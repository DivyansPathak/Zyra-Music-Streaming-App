package com.zyra.music.zyra.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppScreens : NavKey

@Serializable
data object LoginScreen : AppScreens

@Serializable
data object MainGraph : AppScreens

@Serializable
data object PlayerScreen : AppScreens


sealed interface MainScreens : NavKey

@Serializable
data object HomeScreen : MainScreens

@Serializable
data object SearchScreen : MainScreens

@Serializable
data object LibraryScreen : MainScreens

@Serializable
data object ProfileScreen : MainScreens



@Serializable
data class PlaylistScreen(
    val id: String,
    val type: PlayListType
) : MainScreens