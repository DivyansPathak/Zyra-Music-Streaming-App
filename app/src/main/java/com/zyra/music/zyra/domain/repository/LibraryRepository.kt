package com.zyra.music.zyra.domain.repository

import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.model.PlaylistDetails
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylistSong
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import com.zyra.music.zyra.navigation.PlayListType

interface LibraryRepository {

    suspend fun createPlaylist(playlist : UserPlaylist) : Result<UserPlaylist, DataError>
    suspend fun addSongToPlaylist(playlist : UserPlaylistSong) : Result<Unit, DataError>
    suspend fun removeSongToPlaylist(playlist : UserPlaylistSong) : Result<Unit, DataError>
    suspend fun deletePlaylist(playlistId : Long) : Result<Unit, DataError>
    suspend fun getPersonalPlaylists() : Result<List<LibraryPlaylist>, DataError>
    suspend fun getPlaylistDetails(id : String, type : PlayListType) : Result<PlaylistDetails, DataError>
}