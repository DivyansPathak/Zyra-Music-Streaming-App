package com.zyra.music.zyra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zyra.music.zyra.data.utils.LIBRARY_PLAYLIST_TABLE_ENTITY
import com.zyra.music.zyra.navigation.PlayListType

@Entity(tableName = LIBRARY_PLAYLIST_TABLE_ENTITY)
data class LibraryPlaylistEntity(
    @PrimaryKey
    val id : Long,
    val name: String,
    val imageUrl: String?, // The final, resolved image URL
    val creator: String,   // The final, formatted creator string
    val trackCount: Long,
    val playlistType: PlayListType
)
