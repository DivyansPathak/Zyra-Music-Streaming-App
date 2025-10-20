package com.zyra.music.zyra.domain.repository

import com.zyra.music.zyra.data.remote.dto.TrackFullOneDto
import com.zyra.music.zyra.data.remote.dto.favoriteDto.LibraryPlaylistDto
import com.zyra.music.zyra.data.remote.dto.playlistDetails.PlaylistDetailSongs
import com.zyra.music.zyra.data.remote.dto.userPlaylist.UserPlaylistDto
import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface LibraryRepositoryNew {

    /**
     * Fetches the user's personal playlists, including a static entry for "Liked Songs".
     */
    suspend fun getLibraryPlaylists(): Result<List<LibraryPlaylistDto>, DataError>

    /**
     * Fetches all the songs for a given user-created playlist ID.
     */
    suspend fun getPlaylistSongs(playlistId: Long): Result<List<PlaylistDetailSongs>, DataError>

    /**
     * Fetches all the songs the user has marked as a favorite.
     */
    suspend fun getFavoriteSongs(): Result<List<PlaylistDetailSongs>, DataError>

    /**
     * Creates a new playlist for the current user.
     */
    suspend fun createPlaylist(userPlaylist : UserPlaylistDto): Result<UserPlaylistDto, DataError>

    /**
     * Deletes a user-created playlist by its ID.
     */
    suspend fun deletePlaylist(playlistId: Long): Result<Unit, DataError>

    /**
     * Adds a song to a specific playlist.
     */
    suspend fun addSongToPlaylist(playlistId: Long, songId: String): Result<Unit, DataError>

    /**
     * Removes a song from a specific playlist.
     */
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String): Result<Unit, DataError>

    /**
     * Adds a song to the user's favorites.
     */
    suspend fun addFavorite(songId: String): Result<Unit, DataError>

    /**
     * Removes a song from the user's favorites.
     */
    suspend fun removeFavorite(songId: String): Result<Unit, DataError>

    /**
     * observe the playlist for cached playlist
     */
    fun observePersonalPlaylist() : Flow<List<LibraryPlaylist>>

    /**
     * refresh playlists for cached playlist
     */
    suspend fun refreshPersonalPlaylists() : Result<Unit, DataError>
}