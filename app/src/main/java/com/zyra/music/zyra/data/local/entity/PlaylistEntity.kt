package com.zyra.music.zyra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zyra.music.zyra.data.utils.PLAYLIST_TABLE_ENTITY
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.navigation.PlayListType


@Entity(tableName = PLAYLIST_TABLE_ENTITY)
data class PlaylistEntity(
    @PrimaryKey
    val id: String,
    val title : String,
    val subtitle : String,
    val thumbnail : String,
    val songs : List<TrackFullOne>,
    val genre : String,
    val type : PlayListType
)
