package com.zyra.music.zyra.presentation.home

sealed interface HomeEvent {
    data object SearchClick : HomeEvent
    data object NavigateToProfile : HomeEvent
    data object ShowNotification : HomeEvent
}