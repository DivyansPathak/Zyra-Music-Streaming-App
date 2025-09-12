package com.zyra.music.zyra.presentation.playerScreen

sealed interface PlayerAction {
    data object PlayPause : PlayerAction
    data object SkipToNext : PlayerAction
    data object SkipToPrevious : PlayerAction
    data object ToggleShuffle : PlayerAction
    data object CycleRepeatMode : PlayerAction
    data object ToggleFavorite : PlayerAction
    data class Seek(val position: Float) : PlayerAction
    data object Share : PlayerAction
    data object Back : PlayerAction
    data class PlayFromQueue(val index: Int) : PlayerAction
    data class RemoveFromQueue(val index: Int) : PlayerAction
}