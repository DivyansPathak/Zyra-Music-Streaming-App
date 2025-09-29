package com.zyra.music.zyra.data.repository

import android.util.Log
import com.zyra.music.zyra.data.mapper.toPlaylist
import com.zyra.music.zyra.data.remote.RemoteSongDataSource
import com.zyra.music.zyra.domain.model.PlayList
import com.zyra.music.zyra.domain.repository.PlaylistRepository
import com.zyra.music.zyra.domain.utils.Result

private const val TAG = "PlaylistRepositoryImpl"

class PlaylistRepositoryImpl(
    private val remoteSongDataSource: RemoteSongDataSource
) : PlaylistRepository {



    override suspend fun getAllPlaylist(genre: String?): List<PlayList> {
        Log.d(TAG, "1. Asking DataSource for songs with query: '$genre'")
        val result = remoteSongDataSource.getPrePlaylist(genre = genre)
        Log.d(TAG, "2. Got result from DataSource: $result")
        return when (result) {
            is Result.Success -> {
                val playListDto = result.data
                Log.i(TAG, "3. Mapped ${playListDto.size} DTOs to Domain models successfully.")
                playListDto.map { it.toPlaylist() }
            }

            is Result.Failure -> {
                Log.e(TAG, "3. Failed to fetch playlists: ${result.error}")
                emptyList()
            }
        }


    }

    override suspend fun getPlaylistById(playlistId: String): PlayList? {
        TODO("Not yet implemented")
    }
}
