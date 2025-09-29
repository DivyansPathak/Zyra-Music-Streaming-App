package com.zyra.music.zyra.navigation


import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {


    val isBottomBarVisible : Boolean
        get() = false

    @Serializable
    data object MainGraph : Route { // <-- Add this

        override val isBottomBarVisible: Boolean = true

    }

    @Serializable
    data object LoginCheckScreen : Route{
    }

    @Serializable
    data object LoginScreen : Route{

    }

    @Serializable
    data object HomeScreen : Route{
        override val isBottomBarVisible: Boolean = true
    }

//    @Serializable
//    data class PlayerScreen(val jsonTrack : String) : Route{
//        override val title: String = "PlayerScreen"
//    }

    @Serializable
    data object PlayerScreen: Route{
    }

//    @Serializable
//    data object SearchScreen : Route{
//        override val isBottomBarVisible: Boolean = true
//
//    }

    @Serializable
    data object SearchScreenN : Route{
    }

    @Serializable
    data object ProfileScreen : Route{
        override val isBottomBarVisible: Boolean = true

    }

    @Serializable
    data class PlayListScreen(
        val id : String,
        val type : PlayListType,
    ) : Route{
        override val isBottomBarVisible: Boolean = true

    }

//    @Serializable
//    data class PlaylistDetailScreen(val identifier : PlayListIdentifier) : Route{
//        override val isBottomBarVisible: Boolean = true
//
//    }
}