package com.zyra.music.zyra.data.repository

import android.util.Log
import com.zyra.music.zyra.data.local.dao.LibraryPlaylistDao
import com.zyra.music.zyra.data.mapper.toEntity
import com.zyra.music.zyra.data.mapper.toLibraryPlaylist
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "LibraryRepoImpl"

class LibraryRepositoryImpl(
    private val remoteSongDataSource: RemoteSongDataSource,
    private val songRepository: SongRepository,
    private val libraryDao: LibraryPlaylistDao
) : LibraryRepository {

    override fun observePersonalPlaylists(): Flow<List<LibraryPlaylist>> {
        return libraryDao.observePlaylists().map { entities ->
            entities.map { it.toLibraryPlaylist() }
        }
    }

    override suspend fun refreshPersonalPlaylists(): Result<Unit, DataError> {
        val remoteData = remoteSongDataSource.getLibraryPlaylists()
        return when (remoteData) {
            is Result.Success -> {
                val playlitstDtos = remoteData.data
                try {
                    val entities = coroutineScope {
                        playlitstDtos.map { dto ->
                            async {
                                val imageUrl = dto.thumbnail

                                dto.toEntity(finalImageUrl = imageUrl)
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
                Result.Failure(remoteData.error)
            }
        }
    }



    override suspend fun getPersonalPlaylists(): Result<List<LibraryPlaylist>, DataError> {
//        val playlistsResult = remoteSongDataSource.getLibraryPlaylists()
//        if (playlistsResult is Result.Failure) {
//            return playlistsResult
//        }
//        val playlistDtos = (playlistsResult as Result.Success).data
//        val songIdsToFetch = playlistDtos
//        if (songIdsToFetch.isEmpty()) {
//            val mappedPlaylists = playlistDtos.map { it.toLibraryPlaylists("") }
//            return Result.Success(mappedPlaylists)
//        }
//
//        val trackResult = songRepository.searchSongs(songIdsToFetch)
//        if (trackResult is Result.Failure) {
//            val playlistsWithNoThumbnails =
//                playlistDtos.map { it.toLibraryPlaylists(finalImageUrl = "") }
//            return Result.Success(playlistsWithNoThumbnails)
//
//        }
//        val thumbnailMap = (trackResult as Result.Success).data
//            .associateBy({ it.url.substringAfter("v=") }, { it.thumbnail })
//        val finalPlaylists = playlistDtos.map { dto ->
//            val imageUrl = thumbnailMap[dto.thumbnailSongId] ?: ""
//            dto.toLibraryPlaylists(finalImageUrl = imageUrl)
//        }
//        return Result.Success(finalPlaylists)

        TODO("Not yet implemented")
    }

    override suspend fun getPlaylistDetails(
        id: String,
        type: PlayListType
    ): Result<PlaylistDetails, DataError> {
        Log.d(TAG, "getPlaylistDetails: CALLED with id=[$id], type=[$type]")
        // pre - playlist id -> string
        if (type == PlayListType.PRESET) {
            Log.d(TAG, "getPlaylistDetails: PRESET path chosen.")
            val result = remoteSongDataSource.getPlaylistById(id = id)
            return when (result) {
                is Result.Success -> {
                    val dto = result.data
                    Log.d(TAG, "getPlaylistDetails: PRESET path chosen.")
                    Result.Success(dto.toPlaylistDetail().copy(type = type))
                }

                is Result.Failure -> {
                    Log.e(TAG, "getPlaylistDetails: PRESET failed. Error: ${result.error}")
                    result
                }
            }
        }
        Log.d(TAG, "getPlaylistDetails: Personal playlist path (USER_CREATED or FAVORITES).")
        val playlistsResult = getPersonalPlaylists()
        val playlistInfo = when (playlistsResult) {
            is Result.Success -> {
                Log.d(
                    TAG,
                    "getPlaylistDetails: Found ${playlistsResult.data.size} personal playlists."
                )
                playlistsResult.data.find { it.id.toString() == id }
            }

            is Result.Failure -> {
                Log.e(
                    TAG,
                    "getPlaylistDetails: Failed to getPersonalPlaylists. Error: ${playlistsResult.error}"
                )
                return playlistsResult
            }
        }

        if (playlistInfo == null) {
            Log.e(
                TAG,
                "getPlaylistDetails: CRITICAL_ERROR. Playlist with id=[$id] not found in personal playlists."
            )
            return Result.Failure(DataError.UnknownError("playlist not found"))
        }
        Log.d(TAG, "getPlaylistDetails: Found matching playlistInfo: $playlistInfo")
        val songIdsResult = if (type == PlayListType.USER_CREATED) {
            try {
                val numericPlaylistId = id.toLong()
                Log.d(
                    TAG,
                    "getPlaylistDetails: Calling getPlaylistSongIds with numericId=[$numericPlaylistId]"
                )
                remoteSongDataSource.getPlaylistSongIds(id.toLong())
            } catch (e: NumberFormatException) {
                Log.e(
                    TAG,
                    "getPlaylistDetails: CRITICAL_FAILURE. id=[$id] is not a Long but type is USER_CREATED.",
                    e
                )
                Result.Failure(DataError.UnknownError("Invalid playlist ID format for user playlist."))
            }
        } else {
            Log.d(TAG, "getPlaylistDetails: FAVORITES path chosen for song IDs.")
            remoteSongDataSource.getFavoriteIds()
        }

        val songIds = when (songIdsResult) {
            is Result.Success -> songIdsResult.data
            is Result.Failure -> {
                Log.e(
                    TAG,
                    "getPlaylistDetails: Failed to get song IDs. Error: ${songIdsResult.error}"
                )
                return songIdsResult
            }
        }
        if (songIds.isNotEmpty()) {
            Log.d("LibraryRepoImpl", "songIds are : $songIds")
        }
        Log.d(TAG, "getPlaylistDetails: Found ${songIds.size} song IDs.")
        if (songIds.isEmpty()) {
            Log.w(TAG, "getPlaylistDetails: Playlist is empty. Returning empty list.")
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
        Log.d(TAG, "getPlaylistDetails: Fetching track details for ${songIds.size} songs.")
        val trackResult = songRepository.searchSongs(songIds.toList())
        Log.d(TAG, "getPlaylistDetails: trackResult: $trackResult")
        Log.d("LibraryRepoImpl", "trackResult : $trackResult")
        return when (trackResult) {
            is Result.Success -> {
                val tracks = trackResult.data
                Log.i(
                    TAG,
                    "getPlaylistDetails: SUCCESS. Returning complete PlaylistDetails with ${tracks.size} tracks."
                )
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

            is Result.Failure -> {
                Log.e(TAG, "Error in fetcing data : ${trackResult.error}")
                trackResult
            }
        }

    }

    override suspend fun createPlaylist(playlist: UserPlaylist): Result<UserPlaylist, DataError> {
        val result = remoteSongDataSource.createPlaylist(playlist.toUserPlaylistDto())
        if (result is Result.Success) {
            refreshPersonalPlaylists()
        }
        return when (result) {
            is Result.Success -> {
                val dto = result.data
                Result.Success(dto.toUserPlaylist())
            }

            is Result.Failure -> result
        }
    }

    override suspend fun addSongToPlaylist(playlist: UserPlaylistSong): Result<Unit, DataError> {

        val result = remoteSongDataSource.addSongToPlaylist(playlist.toUserPlaylistSongDto())
        if (result is Result.Success) {
            refreshPersonalPlaylists()
        }
        return result
    }

    override suspend fun removeSongToPlaylist(playlist: UserPlaylistSong): Result<Unit, DataError> {
        val result = remoteSongDataSource.removeSongToPlaylist(playlist.toUserPlaylistSongDto())
        if (result is Result.Success) {
            refreshPersonalPlaylists()
        }
        return result
    }

    override suspend fun deletePlaylist(playlistId: Long): Result<Unit, DataError> {
        if (playlistId == -1L) {
            return Result.Failure(DataError.UnknownError("Cannot delete Liked Songs playlist"))
        }
        val result = remoteSongDataSource.deletePlaylist(playlistId = playlistId)
        if (result is Result.Success) {
            refreshPersonalPlaylists()
        }
        return result
    }
}