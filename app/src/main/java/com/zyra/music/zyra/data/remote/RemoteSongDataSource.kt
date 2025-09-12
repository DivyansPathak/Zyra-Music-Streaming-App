package com.zyra.music.zyra.data.remote

import com.zyra.music.zyra.data.remote.dto.SingleTrackDto
import com.zyra.music.zyra.data.remote.dto.TrackFullOneDto
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result

interface RemoteSongDataSource {

    suspend fun searchSong(query: String): Result<List<SingleTrackDto>, DataError>
//    suspend fun getRecommendations(songTitle: String): Result<List<String>, DataError>
    suspend fun searchSongs(queries: List<String>): Result<List<SingleTrackDto>, DataError>

    suspend fun searchSongFromYt(query : String) : Result<List<TrackFullOneDto>, DataError>
    suspend fun getUpNext(videoId : String) : Result<List<TrackFullOneDto>, DataError>
//    suspend fun getRelated(videoId : String) : Result<List<SingleTrackDto>, DataError>

    suspend fun getSearchSuggestions(query : String) : Result<List<String>, DataError>

    // supabase
    suspend fun getFavoriteIds() : Result<Set<String>, DataError>
    suspend fun addFavorite(videoId: String) : Result<Unit, DataError>
    suspend fun removeFavorite(videoId : String) : Result<Unit, DataError>


}