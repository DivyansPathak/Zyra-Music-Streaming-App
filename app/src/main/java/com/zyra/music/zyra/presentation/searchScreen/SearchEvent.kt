package com.zyra.music.zyra.presentation.searchScreen

sealed interface SearchEvent{
    data object NavigateToPlayerScreen : SearchEvent
}
