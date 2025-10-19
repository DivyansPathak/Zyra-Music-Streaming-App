package com.zyra.music.zyra.presentation.libraryScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.domain.repository.LibraryRepository
import com.zyra.music.zyra.domain.utils.getErrorMessage
import com.zyra.music.zyra.domain.utils.onFailure
import com.zyra.music.zyra.domain.utils.onSuccess
import com.zyra.music.zyra.navigation.PlayListType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


private const val TAG = "LibraryViewModel"
class LibraryViewModel(
    private val libraryRepo : LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryState())
    val uiState = _uiState.asStateFlow()

    init {
        Log.d(TAG,"LibraryViewModel initiated")
        observeLibraryContent()
        refreshLibraryContent()

    }

    private fun observeLibraryContent(){
        viewModelScope.launch {
            libraryRepo.observePersonalPlaylists()
                .collect { playlists ->
                    Log.d(TAG,"Ui updated from local cache with ${playlists.size}")
                    _uiState.update {
                        it.copy(playlists = playlists)
                    }

                }
        }
    }
    fun refreshLibraryContent(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            libraryRepo.refreshPersonalPlaylists()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    Log.e(TAG,"Error refreshing library content : $error")
                    _uiState.update { it.copy(isLoading = false,error = error.getErrorMessage()) }
                }
        }
    }
     fun loadLibraryContent(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            libraryRepo.getPersonalPlaylists()
                .onSuccess { playlists ->
                    Log.d(TAG,"Library content loaded successfully $playlists")
                    _uiState.update { it.copy(isLoading = false, playlists = playlists, error = null) }
                }
                .onFailure { error ->
                    Log.d(TAG,"Error loading library content $error")
                    _uiState.update { it.copy(isLoading = false, error = error.getErrorMessage()) }
                }
        }
    }
    fun getPlaylistDetails(id : String, playlistType : PlayListType){
        viewModelScope.launch {
            libraryRepo.getPlaylistDetails(id = id, type = playlistType)
                .onSuccess { playlistDetails ->
                    Log.d(TAG,"Playlist detail for $id and type : $playlistType is Details : $playlistDetails")
                }
                .onFailure { error ->
                    Log.e(TAG,"Error in getting the playlist details $error")
                }
        }
    }

}