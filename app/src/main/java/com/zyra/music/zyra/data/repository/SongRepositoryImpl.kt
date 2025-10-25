package com.zyra.music.zyra.data.repository

import android.util.Log
import com.zyra.music.zyra.data.mapper.toSingleTrackList
import com.zyra.music.zyra.data.mapper.toTrackFullOneList
import com.zyra.music.zyra.data.remote.RemoteSongDataSource
import com.zyra.music.zyra.domain.model.SingleTrack
import com.zyra.music.zyra.domain.model.SongResult
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.timeago.patterns.vi

private const val TAG = "SongRepository"

class SongRepositoryImpl(private val remoteSongDataSource: RemoteSongDataSource) : SongRepository {
    override suspend fun searchSong(query: String): Result<List<SingleTrack>, DataError> {

        Log.d(TAG, "2. Asking DataSource for songs with query: '$query'")

        val result = remoteSongDataSource.searchSong(query = query)
        Log.d(TAG, "3. Got result from DataSource: $result")
        return when (result) {
            is Result.Success -> {
                val singleTrackDto = result.data
                Log.i(TAG, "3.1. Mapped ${singleTrackDto.size} DTOs to Domain models successfully.")
                Result.Success(singleTrackDto.toSingleTrackList())
            }

            is Result.Failure -> result

        }
    }

    override suspend fun searchSongs(queries: List<String>): Result<List<SingleTrack>, DataError> {
        Log.d(TAG, "2. Asking DataSource for songs with queries: '$queries'")
        val result = remoteSongDataSource.searchSongs(queries = queries)
        Log.d(TAG, "3. Got result from DataSource: $result")
        return when(result){
            is Result.Failure -> result
            is Result.Success -> {
                val singleTrackDto = result.data
                Log.i(TAG, "3.1. Mapped ${singleTrackDto.size} DTOs to Domain models successfully.")
                Result.Success(singleTrackDto.toSingleTrackList())
            }
        }
    }

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
