package com.zyra.music.zyra.data.mapper

import com.zyra.music.zyra.data.local.entity.PlaylistEntity
import com.zyra.music.zyra.data.remote.dto.PrePlaylistDto
import com.zyra.music.zyra.data.remote.dto.TrackDto
import com.zyra.music.zyra.domain.model.PlayList
import com.zyra.music.zyra.domain.model.PlaylistDetails
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.navigation.PlayListType

private fun TrackDto.toTrack() = TrackFullOne(
    title = this.title,
    artistName = this.artistName ?: "Unknown Artist",
    videoId = this.videoId,
    thumbnail = this.thumbnailUrl ?: "", // Convert from thumbnailUrl
    duration = this.duration ?: 0,
    artistId = this.artistId ?: "Unknown",
    albumName = this.albumName ?: "Unknown Album",
    albumId = this.albumId ?: "Unknown ID",
)
//fun PrePlaylistDto.toPlaylist() =PlayList(
//    id = this.id,
//    title = this.title,
//    thumbnail = this.thumbnail,
//    subtitle = this.subtitle,
//    songs = this.tracks.map { it.toTrack() },
//    genre = this.genre,
//)

fun PrePlaylistDto.toPlaylistDetail() = PlaylistDetails(
    id = this.id,
    title = this.title,
    coverImageUrl = this.thumbnail,
    description = this.subtitle,
    tracks = this.tracks.map {it.toTrack() },
    type = this.type
)

fun PrePlaylistDto.toPlaylist() : PlayList{

    return PlayList(
        id = this.id,
        title = this.title,
        thumbnail = this.thumbnail,
        subtitle = this.subtitle,
        songs = this.tracks.map { it.toTrack() },
        genre = this.genre,
        type = PlayListType.PRESET
    )
}

fun PlaylistEntity.toPlaylist() : PlayList{
    return PlayList(
        id = this.id,
        title = this.title,
        subtitle = this.subtitle,
        thumbnail = this.thumbnail,
        songs = this.songs,
        genre = this.genre,
        type = this.type
    )
}

fun PlayList.toPlaylistEntity() = PlaylistEntity(
    id = this.id,
    title = this.title,
    subtitle = this.subtitle,
    thumbnail = this.thumbnail,
    songs = this.songs,
    genre = this.genre,
    type = this.type
)

fun PrePlaylistDto.toPlaylistEntity() : PlaylistEntity{
    return this.toPlaylist().toPlaylistEntity()
}
