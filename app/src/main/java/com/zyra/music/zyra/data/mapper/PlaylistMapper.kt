package com.zyra.music.zyra.data.mapper

import com.zyra.music.zyra.data.local.entity.PlaylistEntity
import com.zyra.music.zyra.data.remote.dto.PrePlaylistDto
import com.zyra.music.zyra.data.remote.dto.TrackDto
import com.zyra.music.zyra.data.remote.dto.playlistFromYoutubeDto.PlaylistDto
import com.zyra.music.zyra.domain.model.PlayList
import com.zyra.music.zyra.domain.model.PlaylistDetails
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.model.playlistData.PlaylistYT
import com.zyra.music.zyra.presentation.playlistScreen.PlayListType

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

fun PlaylistDto.toPlaylistYt() = PlaylistYT(
    title = title,
    playlistId = playlistId,
    author = author,
    itemCount = itemCount ?: 0,
    thumbnail = thumbnail
)
fun List<PlaylistDto>.toPlaylistYts() = map { it.toPlaylistYt() }