package com.zyra.music.zyra.domain.repository

import com.zyra.music.zyra.domain.model.PlayList

interface PlaylistRepository {

    suspend fun getAllPlaylist(genre : String?) : List<PlayList>
    suspend fun getPlaylistById(playlistId : String) : PlayList?
}