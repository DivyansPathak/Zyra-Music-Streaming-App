package com.zyra.music.zyra.data.repository

import android.util.Log
import com.zyra.music.zyra.data.local.dao.PrePlaylistDao
import com.zyra.music.zyra.data.mapper.toPlaylist
import com.zyra.music.zyra.data.mapper.toPlaylistEntity
import com.zyra.music.zyra.data.remote.RemoteSongDataSource
import com.zyra.music.zyra.domain.model.PlayList
import com.zyra.music.zyra.domain.repository.PlaylistRepository
import com.zyra.music.zyra.domain.utils.Result
import io.ktor.client.plugins.logging.Logging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "PlaylistRepositoryImpl"

class PlaylistRepositoryImpl(
    private val remoteSongDataSource: RemoteSongDataSource,
    private val playlistDao : PrePlaylistDao
) : PlaylistRepository {



//    override suspend fun getAllPlaylist(genre: String?): List<PlayList> {
//        Log.d(TAG, "1. Asking DataSource for songs with query: '$genre'")
//        val result = remoteSongDataSource.getPrePlaylist(genre = genre)
//        Log.d(TAG, "2. Got result from DataSource: $result")
//        return when (result) {
//            is Result.Success -> {
//                val playListDto = result.data
//                Log.i(TAG, "3. Mapped ${playListDto.size} DTOs to Domain models successfully.")
//                playListDto.map { it.toPlaylist() }
//            }
//
//            is Result.Failure -> {
//                Log.e(TAG, "3. Failed to fetch playlists: ${result.error}")
//                emptyList()
//            }
//        }
//
//
//    }

//    override suspend fun getAllPlaylist(genre: String?, forceRefresh : Boolean): List<PlayList> {
//        if (genre == null) return emptyList()
//        Log.d(TAG,"Fetching playlists for genre : $genre and force refresh : $forceRefresh")
//
//        val localPlaylists = playlistDao.getPlaylistByGenre(genre).map { it.toPlaylist() }
//
//        if(localPlaylists.isNotEmpty() && !forceRefresh){
//            Log.i(TAG,"Returing ${localPlaylists.size} playlists from local caches")
//            return localPlaylists
//        }
//        Log.d(TAG,"Cache empty. Fetching from remote for genre : $genre")
//        val result = remoteSongDataSource.getPrePlaylist(genre)
//        Log.d(TAG,"got result from remote $result")
//        return when (result) {
//            is Result.Success-> {
//                val remoteData = result.data
//                Log.i(TAG,"Successfully fetched ${remoteData.size} playlists from remote")
//                val playlistEntities = remoteData.map { it.toPlaylistEntity() }
//                playlistDao.deleteByGenre(genre)
//                playlistDao.insertAllPlaylist(playlistEntities)
//
//                playlistEntities.map { it.toPlaylist() }
//            }
//            is Result.Failure ->{
//                Log.e(TAG,"Failed to fetch from remote : ${result.error}. Will return stale local data if availble.")
//                localPlaylists
//            }
//        }
//    }
//
    override fun observePlaylists(genre: String): Flow<List<PlayList>> {
        return playlistDao.observePlaylistByGenre(genre = genre).map { entities -> entities.map { it.toPlaylist() } }
    }
    override suspend fun refreshPlaylists(genre: String) {
        Log.d(TAG,"Refreshing playlists for genre : $genre")
        val result = remoteSongDataSource.getPrePlaylist(genre)
        when(result){
            is Result.Success -> {
                val remoteData = result.data
                Log.i(TAG,"Fetched ${remoteData.size} playlists from remote for $genre")
                val playlistEntities = remoteData.map { it.toPlaylistEntity() }

                playlistDao.deleteByGenre(genre)
                playlistDao.insertAllPlaylist(playlistEntities)
            }
            is Result.Failure -> {
                Log.e(TAG,"Failed to refresh playlists for $genre : ${result.error}")
            }
        }
    }
    override suspend fun getPlaylistById(playlistId: String): PlayList? {
        TODO("Not yet implemented")
    }


}
