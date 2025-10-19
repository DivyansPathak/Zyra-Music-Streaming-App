package com.zyra.music.zyra.presentation.playlistScreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.domain.repository.LibraryRepository
import com.zyra.music.zyra.domain.utils.getErrorMessage
import com.zyra.music.zyra.domain.utils.onFailure
import com.zyra.music.zyra.domain.utils.onSuccess
import com.zyra.music.zyra.navigation.PlayListType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PlaylistViewModelTest"
class PlaylistViewModel(
    private val libraryRepo : LibraryRepository,
    playlistId : String,
    playlistType : PlayListType
) : ViewModel(){

    private val _uiState = MutableStateFlow(PlaylistState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<PlaylistEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

   init {
       Log.d(TAG,"PlaylistViewModel initiated")
      fetchPlaylistDetails(playlistId,playlistType)
   }
    fun onAction(action : PlaylistAction){
        when(action){
            is PlaylistAction.PlayPausePlaylist ->{
                _uiState.value.playlistDetails?.tracks?.let { tracks ->
                    if (tracks.isNotEmpty()){
                        viewModelScope.launch {
                            Log.d(TAG,"playlist is going to play unshuffled : ${tracks.size}")
                            _uiEvent.send(PlaylistEvent.PlayPlaylist(tracks = tracks, shuffle = false))
                        }
                    }
                }
            }
            is PlaylistAction.OnShuffleClicked ->{
                _uiState.value.playlistDetails?.tracks?.let { tracks ->
                    if (tracks.isNotEmpty()){
                        viewModelScope.launch {
                            Log.d(TAG,"playlist is going to play shuffled : ${tracks.size}")
                            _uiEvent.send(PlaylistEvent.PlayPlaylist(tracks = tracks, shuffle = true))
                        }
                    }
                }
            }
            is PlaylistAction.OnSongClicked ->{
                _uiState.value.playlistDetails?.tracks?.getOrNull(action.index)?.let { track ->
                    viewModelScope.launch {
                        Log.d(TAG,"playlist's song is going to play unshuffled : $track")
                        _uiEvent.send(PlaylistEvent.PlayTrackAsRadio(track = track))
                    }
                }
            }
        }


    }
     fun fetchPlaylistDetails(playlistId : String,playlistType : PlayListType){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            libraryRepo.getPlaylistDetails(id = playlistId, type = playlistType)
                .onSuccess { playlistDetails ->
                    Log.d(TAG,"the playlist details are : $playlistDetails")
                    _uiState.update { it.copy(isLoading = false, playlistDetails = playlistDetails,error = null) }
                }
                .onFailure { error ->
                    Log.e(TAG,"Error in fetching playlist Details. $error")
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = error.getErrorMessage()
                    ) }
                }
        }
    }
}