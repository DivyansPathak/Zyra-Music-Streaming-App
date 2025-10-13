package com.zyra.music.zyra.data.remote.dto.playlistDetails

import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.navigation.PlayListType
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDetailsDto(
    val id : String,
    val title : String,
    val description : String,
    val coverImageUrl : String?,
    val tracks : List<TrackFullOne>,
    val type : PlayListType
)
