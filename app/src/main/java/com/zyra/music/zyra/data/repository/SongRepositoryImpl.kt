package com.zyra.music.zyra.data.repository

import android.util.Log
import com.zyra.music.zyra.data.mapper.toPlaylistYts
import com.zyra.music.zyra.data.mapper.toTrackFullOneList
import com.zyra.music.zyra.data.remote.RemoteSongDataSource
import com.zyra.music.zyra.domain.model.SongResult
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.model.playlistData.PlaylistYT
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.stream.StreamInfo

private const val TAG = "SongRepository"

class SongRepositoryImpl(private val remoteSongDataSource: RemoteSongDataSource) : SongRepository {
    override suspend fun getSong(url: String): Result<SongResult, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch the stream info. NewPipe is already initialized.
                val streamInfo = StreamInfo.getInfo(url)

                // Find the best audio stream
                val audioStream = streamInfo.audioStreams
                    .maxByOrNull { it.averageBitrate } // Get highest quality audio

                if (audioStream?.content != null) {
                    Log.i(TAG, "NewPipe extractor success for URL: $url")
                    Log.i(
                        TAG,
                        "NewPipe extractor success for URL: ${audioStream.content.toString()}"
                    )
                    Result.Success(SongResult(streamUrl = audioStream.content.toString()))
                } else {
                    Log.w(TAG, "NewPipe could not find an audio stream for URL: $url")
                    Result.Failure(DataError.ServerError) // Or a more specific error
                }
            } catch (e: Exception) {
                Log.e(TAG, "NewPipe extractor failed for URL: $url", e)
                Result.Failure(DataError.UnknownError(e.message))
            }
        }
    }

    override suspend fun searchSongFromYt(query: String): Result<List<TrackFullOne>, DataError> {
        Log.d(TAG, "1. (YT) Asking for songs with query: '$query")
        val result = remoteSongDataSource.searchSongFromYt(query = query)
        Log.d(TAG, "2. (YT) Got result from DataSource: $result")
        return when (result) {
            is Result.Success -> {
                val trackFullOneDto = result.data
                Log.i(
                    TAG,
                    "3. (YT) Mapped ${trackFullOneDto.size} DTOs to Domain models successfully."
                )
                Result.Success(trackFullOneDto.toTrackFullOneList())
            }

            is Result.Failure -> result
        }
    }

    override suspend fun searchSongFromYoutube(query: String): Result<List<TrackFullOne>, DataError> {
        Log.d(TAG,"1. (Youtube) Asking for songs with query : $query")
        val result = remoteSongDataSource.searchSongFromYoutube(query = query)
        Log.d(TAG,"2. (YOUTUBE) Got result from DataSource $result")
        return when(result){
            is Result.Success -> {
                val trackFullOneDto = result.data
                Log.i(TAG,"3. (YOUTUBE) ${trackFullOneDto.size} DTOs to Domain models successfully")
                Result.Success(trackFullOneDto.toTrackFullOneList())
            }
            is Result.Failure -> result
        }

    }

    override suspend fun getUpNext(videoId: String): Result<List<TrackFullOne>, DataError> {
        Log.d(TAG, "1. (YT) Asking for UpNext with videoId: '$videoId")
        val result = remoteSongDataSource.getUpNext(videoId = videoId)
        Log.d(TAG, "2. (YT) Got result from DataSource: $result")
        return when(result){
            is Result.Success ->{
                val trackFullOneDto = result.data
                Log.i(TAG, "3. (YT) Mapped ${trackFullOneDto.size} DTOs to Domain models successfully.")
                Result.Success(trackFullOneDto.toTrackFullOneList())
            }
            is Result.Failure -> result
        }
    }

    override suspend fun searchPlaylistFromYoutube(query: String): Result<List<PlaylistYT>, DataError> {
        val result = remoteSongDataSource.getPlaylistFromYoutube(query = query)
        Log.d(TAG,"Playlists from Youtube Playlist for $query is $result ")
        return when(result){
            is Result.Success ->{
                val playlistDto = result.data
                Log.d(TAG,"Playlists from the $query is $playlistDto")
                Result.Success(playlistDto.toPlaylistYts())
            }
            is Result.Failure -> {
                Log.e(TAG,"we got some error : ${result.error}")
                result
            }
        }
    }

    override suspend fun getSongsFromYoutubePlaylist(playlistId: String): Result<List<TrackFullOne>, DataError> {
        val result = remoteSongDataSource.getSongsFromPlaylist(playlistId = playlistId)
        Log.d(TAG,"Result for playlist id $playlistId is fetching")
        return when(result){
            is Result.Success -> {
                val trackDto = result.data
                Log.d(TAG,"list of tracks for playlist : $trackDto")
                Result.Success(trackDto.toTrackFullOneList())
            }
            is Result.Failure ->{
                Log.e(TAG,"error in getting songs : ${result.error}")
                result
            }
        }
    }

    override suspend fun getSearchSuggestions(query: String): Result<List<String>, DataError> {
        return remoteSongDataSource.getSearchSuggestions(query = query)
    }

    override suspend fun getFavoriteIds(): Result<Set<String>, DataError> {
        return remoteSongDataSource.getFavoriteIds()
    }

    override suspend fun addFavorite(videoId: String): Result<Unit, DataError> {
        return remoteSongDataSource.addFavorite(videoId = videoId)
    }

    override suspend fun removeFavorite(videoId: String): Result<Unit, DataError> {
        return remoteSongDataSource.removeFavorite(videoId = videoId)
    }



}
