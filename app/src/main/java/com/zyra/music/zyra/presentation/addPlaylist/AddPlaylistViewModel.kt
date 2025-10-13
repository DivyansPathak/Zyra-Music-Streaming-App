package com.zyra.music.zyra.presentation.addPlaylist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.data.remote.SupabaseClient
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylistSong
import com.zyra.music.zyra.domain.repository.LibraryRepository
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.getErrorMessage
import com.zyra.music.zyra.domain.utils.onFailure
import com.zyra.music.zyra.domain.utils.onSuccess
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "AddPlaylistViewModel"
class AddPlaylistViewModel(
    private val libraryRepo: LibraryRepository,
    private val songRepo : SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPlaylistState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<AddPlaylistEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        Log.d(TAG,"AddPlaylistViewModel is initiated")
    }

    fun onAction(action : AddPlaylistAction){
      when(action){
          is AddPlaylistAction.SetSongAndShowSheet -> setSongAndLoadPlaylists(action.track)
          is AddPlaylistAction.AddSongToPlaylist -> addSongToPlaylist(action.playlistId)
          is AddPlaylistAction.ShowCreateDialog -> {
              Log.d(TAG,"Playlist sheet is opening")
              _uiState.update { it.copy(isCreateDialogOpen = true) }
          }
          is AddPlaylistAction.HideCreateDialog -> {
              _uiState.update { it.copy(isCreateDialogOpen = false) }
              Log.d(TAG,"Playlist sheet is closing")
          }
          is AddPlaylistAction.CreatePlaylistAndAddSong -> {
              createPlaylist(title = action.title, description = action.description)
              Log.d(TAG,"Playlist is created and song is added")
          }
          is AddPlaylistAction.ShowDeleteDialog -> {
              _uiState.update { it.copy(
                  playlistToDelete = action.playlist
              ) }
          }
          is AddPlaylistAction.ConfirmDelete -> {
              deletePlaylist()

          }
          is AddPlaylistAction.HideDeleteDialog -> {
              _uiState.update { it.copy(playlistToDelete = null) }
          }
      }

    }
    private fun setSongAndLoadPlaylists(track: TrackFullOne) {
        _uiState.update { it.copy(songToAdd = track, isLoading = true) }
        viewModelScope.launch {
            libraryRepo.getPersonalPlaylists()
                .onSuccess { userPlaylist ->

                    _uiState.update { it.copy(playlists = userPlaylist, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(error.getErrorMessage()))
                }
        }
    }
    private fun addSongToPlaylist(playlistId : Long){
        val song = _uiState.value.songToAdd ?: return

        viewModelScope.launch {
            if (playlistId == -1L){
                songRepo.addFavorite(song.videoId)
                    .onSuccess {
                        _uiEvent.send(AddPlaylistEvent.ShowMessage("Added to Liked Songs"))
                    }
                    .onFailure { error ->
                        _uiEvent.send(AddPlaylistEvent.ShowMessage(error.getErrorMessage()))
                    }
            } else{
                val playlistSong = UserPlaylistSong(playlistId = playlistId, songId = song.videoId)
                libraryRepo.addSongToPlaylist(playlist = playlistSong)
                    .onSuccess {
                        val playlistName = _uiState.value.playlists.find { it.id == playlistId }?.name ?: ""
                        _uiEvent.send(AddPlaylistEvent.ShowMessage("Song added to $playlistName"))
                        Log.d(TAG,"Song added to $playlistName")
                    }
                    .onFailure {error ->
                        _uiEvent.send(AddPlaylistEvent.ShowMessage(error.getErrorMessage()))
                        Log.e(TAG,"Error adding song to playlist $error")
                    }
            }

        }
    }

    private fun createPlaylist(title : String, description : String){
     viewModelScope.launch {
         val userId = SupabaseClient.supabase.auth.currentUserOrNull()?.id ?: return@launch
            val playlist = UserPlaylist(userId = userId, name = title, description = description)
         libraryRepo.createPlaylist(playlist = playlist)
             .onSuccess { newPlaylist ->
                 Log.d(TAG,"Playlist is created with title $title")
                 _uiEvent.send(AddPlaylistEvent.ShowMessage("Playlist $title created"))
                 onAction(AddPlaylistAction.HideCreateDialog)
                 newPlaylist.id?.let { newPlaylistId->
                     Log.d(TAG,"Adding song to newly created playlist $newPlaylistId")
                     addSongToPlaylist(newPlaylistId)
                     refreshPlaylist()
                 } ?: run {
                     Log.e(TAG,"Created playlist has no ID!")
                 }
             }
             .onFailure {error ->
                 Log.e(TAG,"Error creating playlist $error")
                 _uiEvent.send(AddPlaylistEvent.ShowMessage(error.getErrorMessage()))
             }
     }

    }

    private fun deletePlaylist(){
        val playlistToDelete = _uiState.value.playlistToDelete ?: return
        viewModelScope.launch {
            libraryRepo.deletePlaylist(playlistId = playlistToDelete.id)
                .onSuccess {
                    Log.d(TAG,"Successfully deleted playlist ${playlistToDelete.id}")
                    val currentPlaylist = _uiState.value.playlists
                    _uiState.update { it.copy(playlists = currentPlaylist.filter { it.id != playlistToDelete.id }) }
                }
                .onFailure {error ->
                    Log.e(TAG,"Error deleting playlist $error")
                }
        }
    }

    private fun refreshPlaylist(){
        Log.d(TAG,"Refreshing playlist list...")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            libraryRepo.getPersonalPlaylists()
                .onSuccess { userPlaylists ->
                    _uiState.update { it.copy(playlists = userPlaylists, isLoading = false) }
                    Log.d(TAG,"Playlist list is refreshed. Total count : ${userPlaylists.size}")
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(error.getErrorMessage()))
                    Log.e(TAG,"Failed to refresh playlist : ${error.getErrorMessage()}")
                }
        }
    }

}