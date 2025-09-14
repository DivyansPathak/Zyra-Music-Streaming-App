package com.zyra.music.zyra.domain.repository

import com.zyra.music.zyra.domain.model.SingleTrack
import com.zyra.music.zyra.domain.model.SongResult
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result

interface SongRepository{

    suspend fun searchSong(query : String) : Result<List<SingleTrack>, DataError>
    suspend fun getSong(url : String) : Result<SongResult, DataError>
//    suspend fun getRecommendations(songTitle : String) : Result<List<SingleTrack>, DataError>

    suspend fun searchSongFromYt(query : String) : Result<List<TrackFullOne>, DataError>
    suspend fun getUpNext(videoId : String) : Result<List<TrackFullOne>, DataError>
    suspend fun getHighQualityThumbnail(videoId : String) : Result<String, DataError>

    suspend fun getSearchSuggestions(query : String) : Result<List<String>, DataError>

    //supabase functions
    suspend fun getFavoriteIds(): Result<Set<String>, DataError>
    suspend fun addFavorite(videoId: String): Result<Unit, DataError>
    suspend fun removeFavorite(videoId: String): Result<Unit, DataError>

}