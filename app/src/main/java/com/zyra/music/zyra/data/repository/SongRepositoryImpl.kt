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

class SongRepositoryImpl(
    private val remoteSongDataSource: RemoteSongDataSource
) : SongRepository {
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


//    override suspend fun getRecommendations(songTitle: String): Result<List<SingleTrack>, DataError> {
//        // STEP 1: Get the list of recommended song titles from the first API call.
//        // STEP 1: Get the list of recommended song titles.
//        Log.d(TAG, "Step 1: Fetching recommendation titles for song: $songTitle")
//        when (val titlesResult = remoteSongDataSource.getRecommendations(songTitle)) {
//            is Result.Failure -> {
//                Log.e(TAG, "Step 1 Failed: Could not get recommendation titles.")
//                // Bug Fix 1: Return the correct error type
//                Log.e(
//                    TAG,
//                    "Step 1 Failed: Could not get recommendation titles. Error: ${titlesResult.error}"
//                )
//                return titlesResult
//            }
//
//            is Result.Success -> {
//                val titles = titlesResult.data
//                if (titles.isEmpty()) {
//                    Log.w(TAG, "Step 1 Success: Got an empty list of titles.")
//                    return Result.Success(emptyList())
//                }
//
//                // STEP 2: Use the titles to get the full song metadata.
//                Log.d(TAG, "Step 2: Fetching metadata for ${titles.size} titles.")
//                Log.d(TAG, "the data is $titles")
//                // Bug Fix 2: Add 'return' to send the final result back
//                return when (val metadataResult =
//                    remoteSongDataSource.searchSongs(queries = titles)) {
//                    is Result.Success -> Result.Success(metadataResult.data.toSingleTrackList())
//                    is Result.Failure -> {
//                        Log.e(TAG, "Step 2 Failed: Could not get metadata for titles.")
//                        Log.e(TAG, "the data is $metadataResult")
//                        metadataResult
//                    }
//                }
//            }
//        }
//
//
//    }
