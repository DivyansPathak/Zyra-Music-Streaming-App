package com.zyra.music.zyra.presentation.playlistScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.data.mapper.toTrackFull
import com.zyra.music.zyra.domain.model.PlaylistDetails
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.repository.LibraryRepositoryNew
import com.zyra.music.zyra.domain.repository.PlaylistRepository
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
    private val libraryRepo: LibraryRepositoryNew,
    private val playlistRep: PlaylistRepository,
    playlistId: String,
    playlistType: PlayListType
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<PlaylistEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        Log.d(TAG, "PlaylistViewModel initiated")
        getPlaylistSongsDetails(playlistId, playlistType)
    }

    fun getPlaylistSongsDetails(playlistId: String, playlistType: PlayListType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            if (playlistType == PlayListType.USER_CREATED || playlistType ==  PlayListType.FAVORITES) {
                libraryRepo.getLibraryPlaylists()
                    .onSuccess { playlistDtos ->
                        val playlists = playlistDtos.find { it.id.toString() == playlistId }
                        if (playlists == null) {
                            Log.e(TAG, "No user Playlist with id : $playlistId")
                            _uiState.update {
                                it.copy(
                                    isLoading = true,
                                    error = "Playlist not Found"
                                )
                            }
                            return@onSuccess
                        }
                        libraryRepo.getPlaylistSongs(playlistId = playlistId.toLong())
                            .onSuccess { songs ->
                                val tracks = songs.map { it.toTrackFull() }
                                Log.d(TAG,"song successfully fetched from the User playlist : $songs")
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        playlistDetails = PlaylistDetails(
                                            id = playlistId,
                                            title = playlists.title,
                                            description = playlists.subtitle ?: "",
                                            tracks = tracks,
                                            coverImageUrl = songs.firstOrNull()?.thumbnail,
                                            type = playlistType
                                        )
                                    )
                                }
                            }
                            .onFailure { error ->
                                Log.e(TAG,"Error in fetching USER playlist songs : $error")
                                _uiState.update {
                                    it.copy(isLoading = false,error = error.getErrorMessage())
                                }
                            }
                    }
                    .onFailure { error ->
                        Log.e(TAG,"Error in fetching USER playlist : $error")
                        _uiState.update { it.copy(isLoading = false, error = error.getErrorMessage()) }
                    }
            } else if (playlistType == PlayListType.PRESET){
                val presetPlaylist = playlistRep.getPlaylistById(playlistId)

                if (presetPlaylist == null){
                    Log.e(TAG,"No PRESET playlist with id : $playlistId, found in local cache")
                    _uiState.update { it.copy(isLoading = false, error = "Playlist not found") }
                }else{
                    Log.d(TAG,"PRESET playlist found : ${presetPlaylist.title}")
                    _uiState.update { it.copy(
                        isLoading = false,
                        playlistDetails = PlaylistDetails(
                            id = playlistId,
                            title = presetPlaylist.title,
                            description = presetPlaylist.subtitle,
                            coverImageUrl = presetPlaylist.thumbnail,
                            tracks = presetPlaylist.songs,
                            type = playlistType
                        ),
                        error = null
                    ) }

                }
            }
        }
    }

    fun removeSongFromPlaylist(track : TrackFullOne){
        val currentPlaylist = _uiState.value.playlistDetails
        val currentPlaylistId = currentPlaylist?.id?.toLongOrNull()

        if (currentPlaylistId == null || currentPlaylist.type != PlayListType.USER_CREATED){
            viewModelScope.launch {
                _uiEvent.send(PlaylistEvent.ShowMessage(message = "You can not remove song from this playlist"))
            }
            return
        }

        viewModelScope.launch {
            libraryRepo.removeSongFromPlaylist(playlistId = currentPlaylistId, songId = track.videoId)
                .onSuccess {
                    _uiState.update {newState ->
                        newState.copy(
                        playlistDetails = newState.playlistDetails?.copy(
                            tracks = newState.playlistDetails.tracks.filterNot {  it.videoId == track.videoId}
                        )
                    ) }
                    _uiEvent.send(PlaylistEvent.ShowMessage(message = "${track.title} removed from ${currentPlaylist.title}"))
                }
                .onFailure { error ->
                    Log.e(TAG,"Error in removing song from ${currentPlaylist.title} error : $error")
                    _uiEvent.send(PlaylistEvent.ShowMessage(message = "Error in removing song from playlist"))
                }
        }

    }

    fun showDeleteDialog(){
        _uiState.update { it.copy(showDeleteDialog = true) }
    }
    fun hideDeleteDialog(){
        _uiState.update { it.copy(showDeleteDialog = false) }
    }
    fun deletePlaylist(){

        val currentPlaylist = _uiState.value.playlistDetails
        val currentPlaylistId = currentPlaylist?.id?.toLongOrNull()

        _uiState.update { it.copy(showDeleteDialog = false) }
        if (currentPlaylistId == null || currentPlaylist.type != PlayListType.USER_CREATED){
            viewModelScope.launch {

                _uiEvent.send(PlaylistEvent.ShowMessage(message = "You can not remove this playlist"))
            }
            return
        }
        viewModelScope.launch {
            libraryRepo.deletePlaylist(playlistId = currentPlaylistId)
                .onSuccess {
                    _uiEvent.send(PlaylistEvent.DeletePlaylist)
                    _uiEvent.send(PlaylistEvent.ShowMessage(message = "'${currentPlaylist.title}' deleted"))
                }
                .onFailure { error ->
                    Log.e(TAG,"playlist ${currentPlaylist.title} can not be deleted error : $error")
                    _uiEvent.send(PlaylistEvent.ShowMessage(message = "Error in deleting playlist"))
                }
        }

    }
}