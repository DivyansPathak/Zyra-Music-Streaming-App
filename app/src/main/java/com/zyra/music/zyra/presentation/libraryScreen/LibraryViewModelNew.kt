package com.zyra.music.zyra.presentation.libraryScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.domain.repository.LibraryRepositoryNew
import com.zyra.music.zyra.domain.utils.onFailure
import com.zyra.music.zyra.domain.utils.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "LibraryViewModelNew"
class LibraryViewModelNew(
    private val libraryRepo : LibraryRepositoryNew
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<LibraryEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        Log.d(TAG,"LibraryViewModelNew initiated")
        loadPlaylists()
    }
    fun loadPlaylists(){
        viewModelScope.launch {
            libraryRepo.getLibraryPlaylists()
                .onSuccess { favoriteSongs ->
                    Log.d(TAG,"Favorite songs are $favoriteSongs")
                }
                .onFailure { error ->
                    Log.e(TAG,"Failed to get favorite songs error : $error")
                }
        }
    }

    fun loadPlaylistSongs(){
        viewModelScope.launch {

            libraryRepo.getPlaylistSongs(playlistId = -1)
                .onSuccess { songs ->
                    Log.d(TAG,"list of songs : $songs")
                }
                .onFailure { error ->
                    Log.e(TAG,"Failed to get songs from playlist : $error")
                }
        }
    }
}