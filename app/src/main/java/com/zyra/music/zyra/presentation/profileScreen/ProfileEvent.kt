package com.zyra.music.zyra.presentation.profileScreen

interface ProfileEvent {
    data class ShowMessage(val message : String) : ProfileEvent
}