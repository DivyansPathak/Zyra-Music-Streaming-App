package com.zyra.music.zyra.presentation.profileScreen

interface ProfileAction {
    data class OnNameChanged(val updateName: String) : ProfileAction
    data object LoadProfile : ProfileAction
    data object LogOut : ProfileAction
    data object ShowNameChangeDialog : ProfileAction
    data object HideNameChangeDialog : ProfileAction
}