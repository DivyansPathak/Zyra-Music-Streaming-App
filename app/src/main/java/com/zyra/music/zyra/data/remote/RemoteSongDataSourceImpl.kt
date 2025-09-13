package com.zyra.music.zyra.data.remote

import com.zyra.music.zyra.data.remote.SupabaseClient.supabase
import com.zyra.music.zyra.data.remote.dto.FavoriteDto
import com.zyra.music.zyra.data.remote.dto.SearchRequestBody
import com.zyra.music.zyra.data.remote.dto.SingleTrackDto
import com.zyra.music.zyra.data.remote.dto.TrackFullOneDto
import com.zyra.music.zyra.data.utils.BASE_URL
import com.zyra.music.zyra.data.utils.YT_BASE_URL
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.net.UnknownHostException


class RemoteSongDataSourceImpl(
    private val httpClient: HttpClient,
) : RemoteSongDataSource {
    private val TAG = "YTDL"
    override suspend fun searchSong(query: String): Result<List<SingleTrackDto>, DataError> {
        return withContext(Dispatchers.IO){
            safeCall<List<SingleTrackDto>> {
                httpClient.get(urlString = "$BASE_URL/search") {
                    parameter("query", "$query song")
                }
            }
        }
    }

//    override suspend fun getRecommendations(songTitle: String): Result<List<String>, DataError> {
//        return withContext(Dispatchers.IO) {
//            safeCall<List<String>> {
//                httpClient.get(urlString = "$BASE_URL/recommendations") {
//                    parameter("song", songTitle)
//                }
//            }
//        }
//    }

    override suspend fun searchSongs(queries: List<String>): Result<List<SingleTrackDto>, DataError> {
        return withContext(Dispatchers.IO) {
            safeCall<List<SingleTrackDto>> {
                httpClient.post(urlString = "$BASE_URL/search-songs/") {
//                    parameter("queries", queries)
                    setBody(SearchRequestBody(queries = queries))
                }
            }
        }


    }

    override suspend fun searchSongFromYt(query: String): Result<List<TrackFullOneDto>, DataError> {
        return withContext(Dispatchers.IO) {
            safeCall<List<TrackFullOneDto>> {
                httpClient.get(urlString = "$YT_BASE_URL/search") {
                    parameter("query", query)
                    parameter("limit",10)
                }
            }
        }

    }
    override suspend fun getUpNext(videoId: String): Result<List<TrackFullOneDto>, DataError> {
        return withContext (Dispatchers.IO){
            safeCall<List<TrackFullOneDto>> {
                httpClient.get (urlString = "$YT_BASE_URL/upnext"){
                    parameter("video_id", videoId)
                    parameter("limit", 15)
                }
            }
        }
    }

    override suspend fun getFavoriteIds(): Result<Set<String>, DataError> {
        return safeSupabaseCall {
            // We select only the 'song_id' column for network efficiency.
//            val favorites = supabase.from("favorites")
//                .select { columns(Columns.list("song_id")) } // Correct way to select specific columns
//                .decodeList<FavoriteDto>()

            val favorites = supabase.from("favorites")
//                .select(columns = Columns.list("song_id"))
                .select()
                .decodeList<FavoriteDto>()

            // Map the resulting list of DTOs to a simple Set of Strings.
            favorites.map { it.songId }.toSet()
        }
    }

    override suspend fun addFavorite(videoId: String): Result<Unit, DataError> {
      // Use the new helper. The lambda contains only the Supabase call.
        return safeSupabaseCall {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: throw IllegalStateException("User must be logged in.")

            val favorite = FavoriteDto(userId = currentUser.id, songId = videoId)

            supabase.from("favorites").insert(favorite)
            // For insert/update/delete, we don't need to return anything, so the
            // inferred return type of this block is Unit, which matches our function signature.
        }
    }

    override suspend fun removeFavorite(videoId: String): Result<Unit, DataError> {
        return safeSupabaseCall {
            // Delete from the 'favorites' table where the song_id matches.
            // The RLS policy automatically adds the "AND user_id = 'current-user-id'" condition.
            supabase.from("favorites").delete {
                filter {
                    eq("song_id", videoId)
                }
            }
        }
    }

    override suspend fun getSearchSuggestions(query: String): Result<List<String>, DataError> {
        if(query.isBlank()){
            return Result.Success(emptyList())
        }
        return withContext(Dispatchers.IO) {
            try {
                // 1. Make the network call and get the raw response as text
                val responseBody = httpClient.get("https://suggestqueries.google.com/complete/search") {
                    parameter("client", "firefox")
                    parameter("ds", "yt")
                    parameter("q", query)
                }.bodyAsText()

                // 2. Perform your custom JSON parsing
                val jsonArray = Json.parseToJsonElement(responseBody).jsonArray
                val suggestions = if (jsonArray.size > 1) {
                    val suggestionsArray = jsonArray[1].jsonArray
                    suggestionsArray.map { it.jsonPrimitive.content }
                } else {
                    emptyList()
                }

                // 3. Return the Success result with your manually parsed list
                Result.Success(suggestions)

            } catch (e: Exception) {
                // 4. Catch common network exceptions, just like safeCall does
                when (e) {
                    is UnknownHostException, is UnresolvedAddressException -> {
                        Result.Failure(DataError.NoInternet)
                    }
                    is SocketTimeoutException -> {
                        Result.Failure(DataError.RequestTimeOut)
                    }
                    else -> {
                        Result.Failure(DataError.UnknownError(e.message))
                    }
                }
            }
        }
    }

}