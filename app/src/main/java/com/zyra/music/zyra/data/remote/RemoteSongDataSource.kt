package com.zyra.music.zyra.data.remote

import com.zyra.music.zyra.data.remote.dto.PrePlaylistDto
import com.zyra.music.zyra.data.remote.dto.SingleTrackDto
import com.zyra.music.zyra.data.remote.dto.ThumbnailDto
import com.zyra.music.zyra.data.remote.dto.TrackFullOneDto
import com.zyra.music.zyra.data.remote.dto.favoriteDto.LibraryPlaylistDto
import com.zyra.music.zyra.data.remote.dto.userPlaylist.UserPlaylistDto
import com.zyra.music.zyra.data.remote.dto.userPlaylist.UserPlaylistSongDto
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result

interface RemoteSongDataSource {

    suspend fun searchSong(query: String): Result<List<SingleTrackDto>, DataError>
    suspend fun searchSongs(queries: List<String>): Result<List<SingleTrackDto>, DataError>

    suspend fun searchSongFromYt(query : String) : Result<List<TrackFullOneDto>, DataError>
    suspend fun getUpNext(videoId : String) : Result<List<TrackFullOneDto>, DataError>
//    suspend fun getRelated(videoId : String) : Result<List<SingleTrackDto>, DataError>

    suspend fun getSearchSuggestions(query : String) : Result<List<String>, DataError>
    // supabase
    //Favorite
    suspend fun getFavoriteIds() : Result<Set<String>, DataError>
    suspend fun addFavorite(videoId: String) : Result<Unit, DataError>
    suspend fun removeFavorite(videoId : String) : Result<Unit, DataError>
    //UserPlaylist
    suspend fun createPlaylist(playlistDto : UserPlaylistDto) : Result<UserPlaylistDto, DataError>
    suspend fun addSongToPlaylist(playlistSong : UserPlaylistSongDto) : Result<Unit, DataError>
    suspend fun removeSongToPlaylist(playlistSong: UserPlaylistSongDto) : Result<Unit, DataError>
    suspend fun deletePlaylist(playlistId : Long) : Result<Unit, DataError>

    suspend fun getLibraryPlaylists() : Result<List<LibraryPlaylistDto>, DataError>
    suspend fun getPlaylistSongIds(playlistId : Long) : Result<List<String>, DataError>

    // PrePlaylist
    suspend fun getPrePlaylist(genre : String?) : Result<List<PrePlaylistDto>, DataError>
    suspend fun getPlaylistById(id : String) : Result<PrePlaylistDto, DataError>

    //MetaDataOfSong
    suspend fun getMetadataOfSong(videoId : String) : Result<TrackFullOneDto, DataError>
}