package com.zyra.music.zyra.data.repository

import android.util.Log
import com.zyra.music.zyra.data.mapper.toLibraryPlaylists
import com.zyra.music.zyra.data.mapper.toListTrackFullOne
import com.zyra.music.zyra.data.mapper.toPlaylistDetail
import com.zyra.music.zyra.data.mapper.toUserPlaylist
import com.zyra.music.zyra.data.mapper.toUserPlaylistDto
import com.zyra.music.zyra.data.mapper.toUserPlaylistSongDto
import com.zyra.music.zyra.data.remote.RemoteSongDataSource
import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.model.PlaylistDetails
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylistSong
import com.zyra.music.zyra.domain.repository.LibraryRepository
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import com.zyra.music.zyra.navigation.PlayListType

class LibraryRepositoryImpl(
    private val remoteSongDataSource: RemoteSongDataSource,
    private val songRepository: SongRepository
) : LibraryRepository {

    override suspend fun getPersonalPlaylists(): Result<List<LibraryPlaylist>, DataError> {
        val playlistsResult = remoteSongDataSource.getLibraryPlaylists()
        if (playlistsResult is Result.Failure) {
            return playlistsResult
        }
        val playlistDtos = (playlistsResult as Result.Success).data
        val songIdsToFetch = playlistDtos.mapNotNull { it.thumbnailSongId }.distinct()
        if (songIdsToFetch.isEmpty()) {
            val mappedPlaylists = playlistDtos.map { it.toLibraryPlaylists("") }
            return Result.Success(mappedPlaylists)
        }

        val trackResult = songRepository.searchSongs(songIdsToFetch)
        if (trackResult is Result.Failure) {
            val playlistsWithNoThumbnails =
                playlistDtos.map { it.toLibraryPlaylists(finalImageUrl = "") }
            return Result.Success(playlistsWithNoThumbnails)

        }
        val thumbnailMap = (trackResult as Result.Success).data
            .associateBy({ it.url.substringAfter("v=") }, { it.thumbnail })
        val finalPlaylists = playlistDtos.map { dto ->
            val imageUrl = thumbnailMap[dto.thumbnailSongId] ?: ""
            dto.toLibraryPlaylists(finalImageUrl = imageUrl)
        }
        return Result.Success(finalPlaylists)
    }

    override suspend fun getPlaylistDetails(
        id: String,
        type: PlayListType
    ): Result<PlaylistDetails, DataError> {
        // pre - playlist id -> string
        if (type == PlayListType.PRESET) {
            val result = remoteSongDataSource.getPlaylistById(id = id)
            return when (result) {
                is Result.Success -> {
                    val dto = result.data
                    Log.d("DEBUG_TEST","DIRECT DATA : DTO $dto")
                    Result.Success(dto.toPlaylistDetail().copy(type = type))
                }

                is Result.Failure -> result
            }
        }

        val playlistsResult = getPersonalPlaylists()
        val playlistInfo = when(playlistsResult){
            is Result.Success -> playlistsResult.data.find { it.id.toString() == id }
            is Result.Failure -> return playlistsResult
        }

        if (playlistInfo == null){
            return Result.Failure(DataError.UnknownError("playlist not found"))
        }

        val songIdsResult = if (type == PlayListType.USER_CREATED){
            remoteSongDataSource.getPlaylistSongIds(id.toLong())
        }else{
            remoteSongDataSource.getFavoriteIds()
        }

        val songIds = when(songIdsResult){
            is Result.Success -> songIdsResult.data
            is Result.Failure -> return songIdsResult
        }
        if (songIds.isNotEmpty()){
            Log.d("LibraryRepoImpl","songIds are : $songIds")
        }

        if (songIds.isEmpty()){
            return Result.Success(
                PlaylistDetails(
                    id = playlistInfo.id.toString(),
                    title = playlistInfo.name,
                    description = playlistInfo.creator,
                    coverImageUrl = playlistInfo.imageUrl,
                    tracks = emptyList(),
                    type = type
                )
            )
        }
        val trackResult = songRepository.searchSongs(songIds.toList())
        Log.d("LibraryRepoImpl","trackResult : $trackResult")
        return when(trackResult){
            is Result.Success ->{
                val tracks = trackResult.data
                Result.Success(
                    PlaylistDetails(
                        id = playlistInfo.id.toString(),
                        title = playlistInfo.name,
                        description = playlistInfo.creator,
                        coverImageUrl = playlistInfo.imageUrl,
                        tracks = tracks.toListTrackFullOne(),
                        type = type
                    )
                )
            }
            is Result.Failure -> trackResult
        }

    }

    override suspend fun createPlaylist(playlist: UserPlaylist): Result<UserPlaylist, DataError> {
        val result = remoteSongDataSource.createPlaylist(playlist.toUserPlaylistDto())
        return when (result) {
            is Result.Success -> {
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
        if (playlistId == -1L) {
            return Result.Failure(DataError.UnknownError("Cannot delete Liked Songs playlist"))
        }
        return remoteSongDataSource.deletePlaylist(playlistId = playlistId)
    }
}