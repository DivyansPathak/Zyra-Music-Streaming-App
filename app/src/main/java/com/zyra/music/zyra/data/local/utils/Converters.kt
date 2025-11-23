package com.zyra.music.zyra.data.local.utils

import androidx.room.TypeConverter
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.playlistScreen.PlayListType
import kotlinx.serialization.json.Json

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromTrackList(tracks : List<TrackFullOne>) : String{
        return json.encodeToString(tracks)
    }

    @TypeConverter
    fun toTrackList(jsonString : String) : List<TrackFullOne>{
        return json.decodeFromString(jsonString)
    }

    @TypeConverter
    fun fromPlaylistType(type : PlayListType) : String{
        return type.name
    }

    @TypeConverter
    fun toPlaylistType(name : String) : PlayListType{
        return PlayListType.valueOf(name)
    }

}