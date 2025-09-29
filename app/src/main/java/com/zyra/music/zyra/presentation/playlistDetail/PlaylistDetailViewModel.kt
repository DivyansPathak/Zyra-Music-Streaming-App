package com.zyra.music.zyra.presentation.playlistDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import com.zyra.music.zyra.domain.repository.PlaylistRepository
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@UnstableApi
class PlaylistDetailViewModel (
    private val repository: PlaylistRepository,
    private val newMainViewModel : MainMusicViewModel,
    saveStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetailState())
    val uiState = _uiState.asStateFlow()

    private val playlistId = checkNotNull(saveStateHandle["playlistId"])

    init {

    }
    private fun loadPlaylist(){

    }
}