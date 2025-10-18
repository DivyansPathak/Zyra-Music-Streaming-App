package com.zyra.music.zyra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zyra.music.zyra.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrePlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlaylist(playlists: List<PlaylistEntity>)

    @Query("SELECT * FROM pre_playlists WHERE genre = :genre")
    fun observePlaylistByGenre(genre: String): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM pre_playlists WHERE genre = :genre")
    suspend fun getPlaylistByGenre(genre: String): List<PlaylistEntity>

    @Query("DELETE FROM pre_playlists  WHERE genre = :genre")
    suspend fun deleteByGenre(genre: String)

    @Transaction
    suspend fun replaceGenrePlaylists(genre: String, playlists: List<PlaylistEntity>) {
        deleteByGenre(genre)
        insertAllPlaylist(playlists = playlists)
    }
}