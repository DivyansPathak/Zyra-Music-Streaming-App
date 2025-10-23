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

                playlistDao.replaceGenrePlaylists(genre = genre, playlists = playlistEntities)
            }
            is Result.Failure -> {
                Log.e(TAG,"Failed to refresh playlists for $genre : ${result.error}")
            }
        }
    }
    override suspend fun getPlaylistById(playlistId: String): PlayList? {
       Log.d(TAG,"Fetching playlist by id: $playlistId from local DAO")
        return playlistDao.getPlaylistById(playlistId)?.toPlaylist()
    }


}
