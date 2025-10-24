package com.zyra.music.zyra.presentation.newPlayer

interface NewPlayerAction {

    data object PlayPause : NewPlayerAction
    data object SkipToNext : NewPlayerAction
    data object SkipToPrevious : NewPlayerAction
    data object ToggleShuffle : NewPlayerAction
    data object CycleRepeatMode : NewPlayerAction
    data object ToggleFavorite : NewPlayerAction
    data object Share : NewPlayerAction
    data object Back : NewPlayerAction
    data object DownloadCurrentTrack : NewPlayerAction
    data object RemoveDownloadFromCurrentTrack : NewPlayerAction
    data class SeekTo(val position: Float) : NewPlayerAction
    data class PlayFromQueue(val index: Int) : NewPlayerAction
    data class RemoveFromQueue(val index: Int) : NewPlayerAction
    data class MoveQueueItem(val fromIndex : Int, val toIndex : Int) : NewPlayerAction
    data object ExpandPlayer : NewPlayerAction
    object CollapsePlayer : NewPlayerAction
    data class SetSleepTimer(val durationInMinutes: Long) : NewPlayerAction
    object SetSleepTimerToEndOfTrack : NewPlayerAction
    object CancelSleepTimer : NewPlayerAction
}