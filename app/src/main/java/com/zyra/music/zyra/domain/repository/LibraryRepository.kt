package com.zyra.music.zyra.domain.repository

import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylistSong
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result

interface LibraryRepository {
    suspend fun createPlaylist(playlist : UserPlaylist) : Result<Unit, DataError>
    suspend fun createPlaylistM(playlist : UserPlaylist) : Result<UserPlaylist, DataError>
    suspend fun addSongToPlaylist(playlist : UserPlaylistSong) : Result<Unit, DataError>
    suspend fun removeSongToPlaylist(playlist : UserPlaylistSong) : Result<Unit, DataError>
    suspend fun deletePlaylist(playlistId : String) : Result<Unit, DataError>
    suspend fun getPersonalPlaylists() : Result<List<LibraryPlaylist>, DataError>
}