package com.zyra.music.zyra.navigation


import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    val title : String

    @Serializable
    data object LoginCheckScreen : Route{
        override val title: String = "LoginCheckScreen"
    }

    @Serializable
    data object LoginScreen : Route{
        override val title: String = "LoginScreen"
    }

    @Serializable
    data object HomeScreen : Route{
        override val title: String = "HomeScreen"
    }

//    @Serializable
//    data class PlayerScreen(val jsonTrack : String) : Route{
//        override val title: String = "PlayerScreen"
//    }

    @Serializable
    data object PlayerScreen: Route{
        override val title: String = "PlayerScreen"
    }

    @Serializable
    data object SearchScreen : Route{
        override val title: String = "SearchScreen"
    }

//    @Serializable
//    data object SearchScreenN : Route{
//        override val title: String = "SearchScreenN"
//    }

    @Serializable
    data object ProfileScreen : Route{
        override val title: String = "Profile"
    }

    @Serializable
    data class PlayListScreen(
        val id : String,
        val type : PlayListType,
    ) : Route{
        override val title: String = "Library"
    }
}