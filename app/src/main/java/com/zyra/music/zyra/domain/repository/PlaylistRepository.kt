package com.zyra.music.zyra.domain.repository

import com.zyra.music.zyra.domain.model.PlayList
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

//    suspend fun getAllPlaylist(genre : String?,forceRefresh : Boolean) : List<PlayList>
    suspend fun getPlaylistById(playlistId : String) : PlayList?


    fun observePlaylists(genre : String) : Flow<List<PlayList>>
    suspend fun refreshPlaylists(genre: String)
}