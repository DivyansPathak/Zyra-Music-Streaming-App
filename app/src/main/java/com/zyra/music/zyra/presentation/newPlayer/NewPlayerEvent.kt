package com.zyra.music.zyra.presentation.newPlayer

interface NewPlayerEvent {
    data class ShowMessage(val message: String) : NewPlayerEvent
    data class OpenShareDialog(val songTitle: String) : NewPlayerEvent
    data object NavigateToBack : NewPlayerEvent
}