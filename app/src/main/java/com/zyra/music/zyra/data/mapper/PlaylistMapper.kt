package com.zyra.music.zyra.data.mapper

import com.zyra.music.zyra.data.remote.dto.PrePlaylistDto
import com.zyra.music.zyra.data.remote.dto.TrackDto
import com.zyra.music.zyra.domain.model.PlayList
import com.zyra.music.zyra.domain.model.TrackFullOne

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
fun PrePlaylistDto.toPlaylist() =PlayList(
    id = this.id,
    title = this.title,
    thumbnail = this.thumbnail,
    subtitle = this.subtitle,
    songs = this.tracks.map { it.toTrack() },
    genre = this.genre
)