package com.zyra.music.zyra.presentation.playerScreen

sealed interface PlayerEvent {
    data class ShowMessage(val message: String) : PlayerEvent
    data object NavigateBack : PlayerEvent
    data class OpenShareDialog(val songTitle : String) : PlayerEvent
}