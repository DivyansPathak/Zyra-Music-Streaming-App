package com.zyra.music.zyra.presentation.libraryScreen


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.data.mapper.toTrackFull
import com.zyra.music.zyra.domain.repository.LibraryRepositoryNew
import com.zyra.music.zyra.domain.utils.getErrorMessage
import com.zyra.music.zyra.domain.utils.onFailure
import com.zyra.music.zyra.domain.utils.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "LibraryViewModelNew"

class LibraryViewModelNew(
    private val libraryRepo: LibraryRepositoryNew
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<LibraryEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        Log.d(TAG, "LibraryViewModelNew initiated")
        observeLibraryContent()
        silentRefreshLibraryContent()
    }

    fun silentRefreshLibraryContent() {
        viewModelScope.launch {
            libraryRepo.refreshPersonalPlaylists()
                .onFailure { error ->
                    Log.e(TAG, "Silent refresh failed : $error")
                }
        }
    }

    private fun observeLibraryContent() {
        viewModelScope.launch {
            libraryRepo.observePersonalPlaylist()
                .collect { playlists ->
                    Log.d(TAG, "Ui updated from local cache with ${playlists.size}")
                    _uiState.update { it.copy(playlists = playlists) }
                }
        }
    }

    fun loadPlaylistSong(){
        viewModelScope.launch {
            val playlistId = -1L
            libraryRepo.getPlaylistSongs(playlistId)
                .onSuccess { songs ->
                    val track = songs.map { it.toTrackFull() }
                    Log.d(TAG,"songs of playlist TrackFullOne : $track")
                }
                .onFailure { error ->
                    Log.e(TAG,"error in getting error : $error")
                }
        }
    }

    fun refreshLibraryContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            libraryRepo.refreshPersonalPlaylists()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    Log.e(TAG, "Error library content")
                    _uiState.update { it.copy(isLoading = false, error = error.getErrorMessage()) }
                }
        }
    }

}