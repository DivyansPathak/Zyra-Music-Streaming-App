package com.zyra.music.zyra.presentation.addPlaylist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.data.remote.SupabaseClient
import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylistSong
import com.zyra.music.zyra.domain.repository.LibraryRepository
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.getErrorMessage
import com.zyra.music.zyra.domain.utils.onFailure
import com.zyra.music.zyra.domain.utils.onSuccess
import com.zyra.music.zyra.navigation.PlayListType
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.playlist.PlaylistInfo

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
      }

    }
    private fun setSongAndLoadPlaylists(track: TrackFullOne) {
        _uiState.update { it.copy(songToAdd = track, isLoading = true) }
        viewModelScope.launch {
            libraryRepo.getPersonalPlaylists()
                .onSuccess { userCreatedPlaylist ->
                    val likedSongs = LibraryPlaylist(
                        id = "-1",
                        name = "Liked Songs",
                        imageUrl = "",
                        creator = "",
                        trackCount = 0,
                        playlistType = PlayListType.FAVORITES
                    )
                    val fullList = listOf(likedSongs) + userCreatedPlaylist
                    _uiState.update { it.copy(playlists = fullList, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AddPlaylistEvent.ShowMessage(error.getErrorMessage()))
                }
        }
    }
    private fun addSongToPlaylist(playlistId : String){
        val song = _uiState.value.songToAdd ?: return

        viewModelScope.launch {
            if (playlistId == "-1"){
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
                        Log.d(TAG,"Error adding song to playlist $error")
                    }
            }

        }
    }

    private fun createPlaylist(title : String, description : String){
     viewModelScope.launch {
         val userId = SupabaseClient.supabase.auth.currentUserOrNull()?.id ?: return@launch
            val playlist = UserPlaylist(userId = userId, name = title, description = description)
         libraryRepo.createPlaylistM(playlist = playlist)
             .onSuccess { newPlaylist ->
                 Log.d(TAG,"Playlist is created with title $title")
                 _uiEvent.send(AddPlaylistEvent.ShowMessage("Playlist $title created"))
                 onAction(AddPlaylistAction.HideCreateDialog)
                 newPlaylist.id?.let { newPlaylistId->
                     addSongToPlaylist(newPlaylistId)
                 }
             }
             .onFailure {error ->
                 Log.e(TAG,"Error creating playlist $error")
                 _uiEvent.send(AddPlaylistEvent.ShowMessage(error.getErrorMessage()))
             }
     }
    }

//    private fun createPlaylist(title : String, description : String){
//        viewModelScope.launch {
//            val userId = SupabaseClient.supabase.auth.currentUserOrNull()?.id ?: return@launch
//            val playlist = UserPlaylist(userId = userId, name = title, description = description)
//            libraryRepo.createPlaylist(playlist = playlist)
//                .onSuccess {
//                    _uiEvent.send(AddPlaylistEvent.ShowMessage("Playlist $title created"))
//                    refreshAndAddSongToNewPlaylist(playlistTitle = title)
//                    Log.d(TAG,"Playlist is created with title $title")
//                }
//                .onFailure { error ->
//                    Log.e(TAG,"Error creating playlist $error")
//                    _uiEvent.send(AddPlaylistEvent.ShowMessage(error.getErrorMessage()))
//                }
//
//
//        }
//    }
//    private fun refreshAndAddSongToNewPlaylist(playlistTitle : String){
//        viewModelScope.launch {
//            libraryRepo.getPersonalPlaylists()
//                .onSuccess { playlists ->
//                    _uiState.update { it.copy(playlists = playlists) }
//                    playlists.find { it.name == playlistTitle }?.let { newPlaylist ->
//                        addSongToPlaylist(newPlaylist.id)
//                    }
//                }
//        }
//    }
}