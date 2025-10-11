package com.zyra.music.zyra.data.repository

import com.zyra.music.zyra.data.mapper.toLibraryPlaylists
import com.zyra.music.zyra.data.mapper.toUserPlaylist
import com.zyra.music.zyra.data.mapper.toUserPlaylistDto
import com.zyra.music.zyra.data.mapper.toUserPlaylistSongDto
import com.zyra.music.zyra.data.remote.RemoteSongDataSource
import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylistSong
import com.zyra.music.zyra.domain.repository.LibraryRepository
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result

class LibraryRepositoryImpl(
    private val remoteSongDataSource : RemoteSongDataSource,
    private val songRepository : SongRepository
) : LibraryRepository{

    override suspend fun getPersonalPlaylists(): Result<List<LibraryPlaylist>, DataError> {
        val playlistsResult = remoteSongDataSource.getLibraryPlaylists()
        if (playlistsResult is Result.Failure){
            return playlistsResult
        }
        val playlistDtos = (playlistsResult as Result.Success).data
        val songIdsToFetch = playlistDtos.mapNotNull { it.thumbnailSongId }.distinct()
        if (songIdsToFetch.isEmpty()){
            val mappedPlaylists = playlistDtos.map { it.toLibraryPlaylists("") }
            return Result.Success(mappedPlaylists)
        }

        val trackResult = songRepository.searchSongs(songIdsToFetch)
        if (trackResult is Result.Failure){
            val playlistsWithNoThumbnails = playlistDtos.map { it.toLibraryPlaylists(finalImageUrl = "")}
            return Result.Success(playlistsWithNoThumbnails)

        }
        val thumbnailMap = (trackResult as Result.Success).data
            .associateBy({it.url.substringAfter("v=")},{it.thumbnail})
        val finalPlaylists = playlistDtos.map { dto ->
            val imageUrl = thumbnailMap[dto.thumbnailSongId] ?: ""
            dto.toLibraryPlaylists(finalImageUrl = imageUrl)
        }
        return Result.Success(finalPlaylists)
    }

    override suspend fun createPlaylist(playlist: UserPlaylist): Result<Unit, DataError> {
       return remoteSongDataSource.createPlaylist(playlist.toUserPlaylistDto())
    }

    override suspend fun createPlaylistM(playlist: UserPlaylist): Result<UserPlaylist, DataError> {
        val result = remoteSongDataSource.createPlaylistM(playlist.toUserPlaylistDto())
        return when(result){
            is Result.Success ->{
                val dto = result.data
                Result.Success(dto.toUserPlaylist())
            }
            is Result.Failure -> result
        }
    }

    override suspend fun addSongToPlaylist(playlist: UserPlaylistSong): Result<Unit, DataError> {
        return remoteSongDataSource.addSongToPlaylist(playlist.toUserPlaylistSongDto())
    }

    override suspend fun removeSongToPlaylist(playlist: UserPlaylistSong): Result<Unit, DataError> {
        return remoteSongDataSource.removeSongToPlaylist(playlist.toUserPlaylistSongDto())
    }

    override suspend fun deletePlaylist(playlistId: Long): Result<Unit, DataError> {
        if (playlistId == -1L){
            return Result.Failure(DataError.UnknownError("Cannot delete Liked Songs playlist"))
        }
        return remoteSongDataSource.deletePlaylist(playlistId = playlistId)
    }
}