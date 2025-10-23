package com.zyra.music.zyra.data.remote

import android.util.Log
import com.zyra.music.zyra.data.mapper.toPlaylistDetailSongs
import com.zyra.music.zyra.data.remote.SupabaseClient.supabase
import com.zyra.music.zyra.data.remote.dto.FavoriteDto
import com.zyra.music.zyra.data.remote.dto.PrePlaylistDto
import com.zyra.music.zyra.data.remote.dto.SearchRequestBody
import com.zyra.music.zyra.data.remote.dto.SingleTrackDto
import com.zyra.music.zyra.data.remote.dto.TrackFullOneDto
import com.zyra.music.zyra.data.remote.dto.favoriteDto.LibraryPlaylistDto
import com.zyra.music.zyra.data.remote.dto.playlistDetails.PlaylistDetailSongs
import com.zyra.music.zyra.data.remote.dto.userPlaylist.UserPlaylistDto
import com.zyra.music.zyra.data.remote.dto.userPlaylist.UserPlaylistSongDto
import com.zyra.music.zyra.data.utils.BASE_URL
import com.zyra.music.zyra.data.utils.PRE_PLAYLIST_URL
import com.zyra.music.zyra.data.utils.YT_BASE_URL
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
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
        return withContext(Dispatchers.IO) {
            safeCall<List<SingleTrackDto>> {
                httpClient.get(urlString = "$BASE_URL/search") {
                    parameter("query", "$query song")
                }
            }
        }
    }

    override suspend fun searchSongs(queries: List<String>): Result<List<SingleTrackDto>, DataError> {
        return withContext(Dispatchers.IO) {
            safeCall<List<SingleTrackDto>> {
                httpClient.post(urlString = "$BASE_URL/search-songs/") {
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
                    parameter("limit", 10)
                }
            }
        }

    }

    override suspend fun getUpNext(videoId: String): Result<List<TrackFullOneDto>, DataError> {
        return withContext(Dispatchers.IO) {
            safeCall<List<TrackFullOneDto>> {
                httpClient.get(urlString = "$YT_BASE_URL/upnext") {
                    parameter("video_id", videoId)
                    parameter("limit", 15)
                }
            }
        }
    }

    override suspend fun getFavoriteIds(): Result<Set<String>, DataError> {
        return safeSupabaseCall {
            val favorites = supabase.from("favorites")
                .select()
                .decodeList<FavoriteDto>()
            favorites.map { it.songId }.toSet()
        }
    }

    override suspend fun fetchAndCacheSongMetadata(videoId: String): Result<Unit, DataError> {
        val metadataResult = getMetadataOfSong(videoId)
        when (metadataResult) {
            is Result.Success -> {
                val songDto = metadataResult.data
                return cacheSongMetadata(songDto.toPlaylistDetailSongs())
            }

            is Result.Failure -> {
                return metadataResult
            }
        }
    }

    override suspend fun addFavorite(videoId: String): Result<Unit, DataError> {
        val cacheResult = fetchAndCacheSongMetadata(videoId)
        if (cacheResult is Result.Failure) {
            return cacheResult
        }
        return safeSupabaseCall {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: throw IllegalStateException("User must be logged in.")
            val favorite = FavoriteDto(userId = currentUser.id, songId = videoId)
            supabase.from("favorites").insert(favorite)
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

    override suspend fun createPlaylist(playlistDto: UserPlaylistDto): Result<UserPlaylistDto, DataError> {
        return safeSupabaseCall {
            supabase.from("playlists").insert(playlistDto) {
                select()
            }.decodeSingle<UserPlaylistDto>()
        }
    }

    override suspend fun addSongToPlaylist(playlistSong: UserPlaylistSongDto): Result<Unit, DataError> {
        val cacheResult = fetchAndCacheSongMetadata(playlistSong.songId)
        if (cacheResult is Result.Failure){
            return cacheResult
        }
        return safeSupabaseCall {
            supabase.from("playlist_songs").insert(value = playlistSong)
        }
    }

    override suspend fun removeSongToPlaylist(playlistSong: UserPlaylistSongDto): Result<Unit, DataError> {
        return safeSupabaseCall {
            supabase.from("playlist_songs").delete {
                filter {
                    eq("playlist_id", playlistSong.playlistId)
                    eq("song_id", playlistSong.songId)
                }
            }
        }
    }

    override suspend fun deletePlaylist(playlistId: Long): Result<Unit, DataError> {
        return safeSupabaseCall {
            supabase.from("playlists").delete {
                filter {
                    eq("id", playlistId)
                }
            }
        }
    }

    override suspend fun cacheSongMetadata(song: PlaylistDetailSongs): Result<Unit, DataError> {
        return safeSupabaseCall {
            supabase.from("songs").upsert(song)
        }
    }

    override suspend fun getLibraryPlaylists(): Result<List<LibraryPlaylistDto>, DataError> {
        return safeSupabaseCall {
            supabase.postgrest.rpc(
                function = "get_user_personal_playlists",
                parameters = emptyMap<String, String>()
            ).decodeList<LibraryPlaylistDto>()
        }
//        return try {
//            val response = supabase.postgrest.rpc(
//                function = "get_user_personal_playlists",
//                parameters = emptyMap<String,String>()
//            )
//
//            val dtoList = response.decodeList<LibraryPlaylistDto>()
//            Log.d("SupabaseResponse","decoded playlists: $dtoList")
//            Result.Success(dtoList)
//        }catch (e : Exception){
//            Log.e("SupabaseResponse","Error Fetching playlists",e)
//            Result.Failure(DataError.UnknownError(e.message))
//        }
    }

    override suspend fun getPlaylistSongs(playlistId: Long): Result<List<PlaylistDetailSongs>, DataError> {
        return safeSupabaseCall {
            supabase.postgrest.rpc(
                function = "get_playlist_songs",
                parameters = mapOf("playlist_id" to playlistId)
            ).decodeList<PlaylistDetailSongs>()
        }
//        return try {
//            val result = supabase.postgrest.rpc(
//                function = "get_playlist_songs",
//                parameters = mapOf("playlist_id" to playlistId)
//            ).decodeList<PlaylistDetailSongs>()
//            Log.d("RemoteSongDataSource", "Successfully fetched data : $result")
//            Result.Success(result)
//        } catch (e: Exception){
//            Log.e("RemoteSongDataSource", "Error getting playlist songs: ${e.message}", e)
//            Log.e("RemoteSongDataSource", "Error type: ${e::class.simpleName}")
//            Result.Failure(DataError.ServerError)
//        }

    }

    override suspend fun getFavoriteSongs(): Result<List<PlaylistDetailSongs>, DataError> {
        return getPlaylistSongs(playlistId = -1L)
    }


    override suspend fun getPlaylistSongIds(playlistId: Long): Result<List<String>, DataError> {
        return safeSupabaseCall {
            supabase.postgrest.rpc(
                function = "get_playlist_song_ids",
                parameters = mapOf("p_playlist_id" to playlistId)
            ).decodeList<String>()
        }
    }


    override suspend fun getSearchSuggestions(query: String): Result<List<String>, DataError> {
        if (query.isBlank()) {
            return Result.Success(emptyList())
        }
        return withContext(Dispatchers.IO) {
            try {
                // 1. Make the network call and get the raw response as text
                val responseBody =
                    httpClient.get("https://suggestqueries.google.com/complete/search") {
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

    override suspend fun getPrePlaylist(genre: String?): Result<List<PrePlaylistDto>, DataError> {
        return withContext(Dispatchers.IO) {
            safeCall<List<PrePlaylistDto>> {
                httpClient.get(urlString = "$PRE_PLAYLIST_URL/playlists") {
                    genre?.let {
                        parameter("genre", it)
                    }
                }
            }
        }
    }

    override suspend fun getPlaylistById(id: String): Result<PrePlaylistDto, DataError> {
        return withContext(Dispatchers.IO) {
            safeCall<PrePlaylistDto> {
                httpClient.get(urlString = "$PRE_PLAYLIST_URL/playlists/$id")
            }
        }
    }

    override suspend fun getMetadataOfSong(videoId: String): Result<TrackFullOneDto, DataError> {
        return withContext(Dispatchers.IO) {
            safeCall<TrackFullOneDto> {
                httpClient.get(urlString = "$YT_BASE_URL/song/metadata/$videoId")
            }
        }
    }

}