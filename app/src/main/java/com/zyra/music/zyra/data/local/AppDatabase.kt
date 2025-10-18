package com.zyra.music.zyra.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zyra.music.zyra.data.local.dao.PrePlaylistDao
import com.zyra.music.zyra.data.local.entity.PlaylistEntity
import com.zyra.music.zyra.data.local.utils.Converters


@Database(entities = [PlaylistEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao() : PrePlaylistDao
}