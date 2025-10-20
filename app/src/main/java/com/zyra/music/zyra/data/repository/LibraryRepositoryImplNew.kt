package com.zyra.music.zyra.data.repository

import com.zyra.music.zyra.data.local.dao.LibraryPlaylistDao
import com.zyra.music.zyra.data.mapper.toEntity
import com.zyra.music.zyra.data.mapper.toLibraryPlaylist
import com.zyra.music.zyra.data.remote.RemoteSongDataSource
import com.zyra.music.zyra.data.remote.dto.favoriteDto.LibraryPlaylistDto
import com.zyra.music.zyra.data.remote.dto.playlistDetails.PlaylistDetailSongs
import com.zyra.music.zyra.data.remote.dto.userPlaylist.UserPlaylistDto
import com.zyra.music.zyra.data.remote.dto.userPlaylist.UserPlaylistSongDto
import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.repository.LibraryRepositoryNew
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryRepositoryImplNew(
    private val remoteSongDataSource: RemoteSongDataSource,
    private val libraryDao: LibraryPlaylistDao
) : LibraryRepositoryNew {
    override suspend fun getLibraryPlaylists(): Result<List<LibraryPlaylistDto>, DataError> {
        return remoteSongDataSource.getLibraryPlaylists()
    }

    override suspend fun getPlaylistSongs(playlistId: Long): Result<List<PlaylistDetailSongs>, DataError> {
        return remoteSongDataSource.getPlaylistSongs(playlistId = playlistId)
    }

    override suspend fun getFavoriteSongs(): Result<List<PlaylistDetailSongs>, DataError> {
        return remoteSongDataSource.getFavoriteSongs()
    }

    override suspend fun createPlaylist(
        userPlaylist : UserPlaylistDto
    ): Result<UserPlaylistDto, DataError> {
        val result =remoteSongDataSource.createPlaylist(playlistDto = userPlaylist)
        if (result is Result.Success){
            refreshPersonalPlaylists()
        }
        return result
    }

    override suspend fun deletePlaylist(playlistId: Long): Result<Unit, DataError> {
        if (playlistId == -1L) {
            return Result.Failure(DataError.UnknownError("Cannot delete Liked Songs playlist"))
        }
        val result = remoteSongDataSource.deletePlaylist(playlistId = playlistId)
        return when (result) {
            is Result.Success -> {
                refreshPersonalPlaylists()
                Result.Success(Unit)
            }

            is Result.Failure -> result
        }
    }

    override suspend fun addSongToPlaylist(
        playlistId: Long,
        songId: String
    ): Result<Unit, DataError> {
        val playlistSongDto = UserPlaylistSongDto(playlistId = playlistId, songId = songId)
        val result =  remoteSongDataSource.addSongToPlaylist(playlistSong = playlistSongDto)
        if (result is Result.Success) {
            refreshPersonalPlaylists()
        }
        return result
    }

    override suspend fun removeSongFromPlaylist(
        playlistId: Long,
        songId: String
    ): Result<Unit, DataError> {
        val playlistSongDto = UserPlaylistSongDto(playlistId = playlistId, songId = songId)
        val result = remoteSongDataSource.removeSongToPlaylist(playlistSong = playlistSongDto)
        if (result is Result.Success) {
            refreshPersonalPlaylists()
        }
        return result
    }

    override suspend fun addFavorite(songId: String): Result<Unit, DataError> {
        return remoteSongDataSource.addFavorite(videoId = songId)
    }

    override suspend fun removeFavorite(songId: String): Result<Unit, DataError> {
        return remoteSongDataSource.removeFavorite(videoId = songId)
    }

    override fun observePersonalPlaylist(): Flow<List<LibraryPlaylist>> {
        return libraryDao.observePlaylists().map { entities ->
            entities.map { it.toLibraryPlaylist() }
        }

    }

    override suspend fun refreshPersonalPlaylists(): Result<Unit, DataError> {
        val result = remoteSongDataSource.getLibraryPlaylists()
        return when (result) {
            is Result.Success -> {
                val playlistDto = result.data
                val latestThumbnail = playlistDto.firstOrNull()?.thumbnail

                try {
                    val entities = coroutineScope {
                        playlistDto.map { dto ->
                            async {
                                dto.toEntity(finalImageUrl = latestThumbnail)
                            }
                        }.awaitAll()
                    }
                    libraryDao.replaceAll(entities)
                    Result.Success(Unit)
                } catch (e: Exception) {
                    Result.Failure(DataError.UnknownError(e.message))
                }

            }

            is Result.Failure -> {
                Result.Failure(result.error)
            }
        }
    }

}