package com.zyra.music.zyra.presentation.home

sealed interface HomeAction {

    data object ProfileClick : HomeAction
    data class PlaylistClick(val playlistId: String) : HomeAction

}