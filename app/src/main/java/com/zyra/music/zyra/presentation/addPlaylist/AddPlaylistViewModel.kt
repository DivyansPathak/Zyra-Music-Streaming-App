package com.zyra.music.zyra.presentation.addPlaylist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.data.mapper.toUserPlaylistDto
import com.zyra.music.zyra.data.remote.SupabaseClient
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylist
import com.zyra.music.zyra.domain.repository.LibraryRepositoryNew
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

class AddPlaylistViewModel(private val libraryRepo: LibraryRepositoryNew, ) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPlaylistState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<AddPlaylistEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        Log.d(TAG, "AddPlaylistViewModel is initiated")
        Log.d(TAG, "AddPlaylistViewModel is initiated, hash=${this.hashCode()}")
        observePlaylists()
        silentRefreshLibraryContent()

    }

    private fun observePlaylists() {
        viewModelScope.launch {
            libraryRepo.observePersonalPlaylist().collect { playlists ->
                _uiState.update { it.copy(playlists = playlists) }
//                Log.d(TAG, "Local playlists updated. Count : ${playlists.size}")
            }
        }
    }
    fun silentRefreshLibraryContent(){
        viewModelScope.launch {
            libraryRepo.refreshPersonalPlaylists()
                .onFailure { error ->
//                    Log.e(TAG,"Silent refresh failed : $error")
                }
        }
    }

    fun onAction(action: AddPlaylistAction) {
        when (action) {
            is AddPlaylistAction.SetSongAndShowSheet -> {
                _uiState.update { it.copy(songToAdd = action.track) }
                refreshPlaylistInBackground()
            }

            is AddPlaylistAction.AddSongToPlaylist -> {
                addSongToPlaylist(action.playlistId)
            }
            is AddPlaylistAction.RemoveSongFromPlaylist ->{
                removeSongFromPlaylist(playlistId = action.playlistId, songId = action.songId)
            }
            is AddPlaylistAction.ShowCreateDialog -> {
//                Log.d(TAG, "Playlist sheet is opening")
                _uiState.update { it.copy(isCreateDialogOpen = true) }
            }

            is AddPlaylistAction.HideCreateDialog -> {
                _uiState.update { it.copy(isCreateDialogOpen = false) }
//                Log.d(TAG, "Playlist sheet is closing")
            }

            is AddPlaylistAction.CreatePlaylistAndAddSong -> {
                createNewPlaylist(title = action.title, description = action.description)
                Log.d(TAG, "Playlist is created and song is added")
            }

            is AddPlaylistAction.ShowDeleteDialog -> {
                _uiState.update {
                    it.copy(
                        playlistToDelete = action.playlist
                    )
                }
            }

            is AddPlaylistAction.ConfirmDelete -> {
                deletePlaylist()

            }

            is AddPlaylistAction.HideDeleteDialog -> {
                _uiState.update { it.copy(playlistToDelete = null) }
            }
        }

    }

    private fun addSongToPlaylist(playlistId: Long) {
        val song = _uiState.value.songToAdd ?: return
        viewModelScope.launch {
            if (playlistId == -1L) {
                libraryRepo.addFavorite(songId = song.videoId)
                    .onSuccess {
                        Log.d(TAG, "song ${song.title} added successfully in Like playlist")
                        _uiEvent.send(AddPlaylistEvent.ShowMessage("Added to Liked Playlist"))
                    }
                    .onFailure { error ->
                        Log.e(TAG, "song ${song.title} can not added in Like playlist")
                        _uiEvent.send(AddPlaylistEvent.ShowMessage(message = error.getErrorMessage()))
                    }
            } else {
                libraryRepo.addSongToPlaylist(playlistId = playlistId, songId = song.videoId)
                    .onSuccess {
                        val playlistName =
                            _uiState.value.playlists.find { it.id == playlistId }?.name ?: ""
                        _uiEvent.send(AddPlaylistEvent.ShowMessage("Song :${song.title} added to $playlistName"))
                        Log.d(TAG, "Song :${song.title} added to $playlistName")
                    }
                    .onFailure { error ->
                        Log.e(TAG, "song ${song.title} can not added in playlist : $playlistId")
                        val errorMessage = error.getErrorMessage()
                        if (errorMessage.contains("duplicate", ignoreCase = true) ||
                            errorMessage.contains("already exixts", ignoreCase = true)){
                            _uiEvent.send(AddPlaylistEvent.ShowMessage("Song ${song.title} is already in this playlist"))
                        }else{
                            _uiEvent.send(AddPlaylistEvent.ShowMessage(message = error.getErrorMessage()))
                        }
                    }
            }
        }
    }

    private fun removeSongFromPlaylist(playlistId: Long, songId : String){
        viewModelScope.launch {
            libraryRepo.removeSongFromPlaylist(playlistId = playlistId, songId = songId)
                .onSuccess {
                    val playlist = _uiState.value.playlists
                    val thisPlaylist = playlist.find { it.id == playlistId }
                    Log.d(TAG,"song removed successfully")
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(message = "Song successfully removed from ${thisPlaylist?.name}"))
                    refreshPlaylistInBackground()
                }
                .onFailure { error ->
                    Log.e(TAG,"Error in deleting song from playlist : $playlistId")
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(message = "Error in removing song from playlist"))
                }
        }
    }

    private fun createNewPlaylist(title: String, description: String) {
        viewModelScope.launch {
            val userId = SupabaseClient.supabase.auth.currentUserOrNull()?.id ?: return@launch
            val playlist = UserPlaylist(userId = userId, name = title, description = description)
            val playlistDto = playlist.toUserPlaylistDto()
            libraryRepo.createPlaylist(userPlaylist = playlistDto)
                .onSuccess { newPlaylist ->
                    Log.d(TAG, "new playist is created with name : ${playlist.name} ")
                    onAction(AddPlaylistAction.HideCreateDialog)
                    newPlaylist.id?.let { newPlaylistId ->
                        Log.d(TAG, "Adding song to newly created playlist ${newPlaylist.name}")
                        addSongToPlaylist(newPlaylistId)
                    }
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(message = "$title playlist is created"))

                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to create new playlist : $error")
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(message = "Failed to create new playlist"))
                }
        }
    }

    private fun deletePlaylist() {
        val playlistToDelete = _uiState.value.playlistToDelete ?: return
        viewModelScope.launch {
            libraryRepo.deletePlaylist(playlistId = playlistToDelete.id)
                .onSuccess {
                    Log.d(TAG, "Successfully deleted playlist ${playlistToDelete.name}")
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(message = "${playlistToDelete.name} is deleted successfully"))
                }
                .onFailure { error ->
                    Log.e(TAG, "Error deleting playlist $error")
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(message = "Failed to delete ${playlistToDelete.name}"))
                }
        }
    }

    private fun refreshPlaylistInBackground() {
        Log.d(TAG, "Refreshing playlist list...")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            libraryRepo.getLibraryPlaylists()
                .onSuccess { userPlaylists ->
                    Log.d(TAG, "Playlist list is refreshed. Total count : ${userPlaylists.size}")
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(error.getErrorMessage()))
                    Log.e(TAG, "Failed to refresh playlist : ${error.getErrorMessage()}")
                }
        }
    }

}