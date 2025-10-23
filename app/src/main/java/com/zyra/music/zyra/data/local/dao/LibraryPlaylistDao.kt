package com.zyra.music.zyra.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zyra.music.zyra.data.local.entity.LibraryPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryPlaylistDao {

    @Query("SELECT * FROM library_playlists")
    fun observePlaylists() : Flow<List<LibraryPlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlists : List<LibraryPlaylistEntity>)

    @Query("DELETE FROM library_playlists")
    suspend fun clearAll()

    @Query("SELECT * FROM library_playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId : Long) : LibraryPlaylistEntity?

    @Transaction
    suspend fun replaceAll(playlists : List<LibraryPlaylistEntity>){
        clearAll()
        insertAll(playlists)
    }
}