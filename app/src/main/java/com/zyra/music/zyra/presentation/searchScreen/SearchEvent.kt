package com.zyra.music.zyra.presentation.searchScreen

sealed interface SearchEvent{
    data object NavigateToBack : SearchEvent
    data object HideKeyboard : SearchEvent
}
