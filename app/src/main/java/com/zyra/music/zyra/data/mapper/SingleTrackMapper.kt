package com.zyra.music.zyra.data.mapper

import com.zyra.music.zyra.data.remote.dto.TrackDto
import com.zyra.music.zyra.data.remote.dto.TrackFullOneDto
import com.zyra.music.zyra.data.remote.dto.playlistDetails.PlaylistDetailSongs
import com.zyra.music.zyra.domain.model.TrackFullOne


private fun TrackFullOneDto.toTrackFullOne() = TrackFullOne(
    title = this.title,
    artistName = this.artistName,
    videoId = this.videoId,
    thumbnail = this.thumbnail,
    duration = this.duration,
    artistId = this.artistId.toString(),
    albumName = this.albumName.toString(),
    albumId = this.albumId.toString(),
)

fun List<TrackFullOneDto>.toTrackFullOneList() = map { it.toTrackFullOne() }


fun TrackDto.toTrackFullOneDto(): TrackFullOneDto {
    return TrackFullOneDto(
        title = this.title,
        artistName = this.artistName ?: "Unknown Artist",
        videoId = this.videoId,
        thumbnail = this.thumbnailUrl ?: "", // Convert from thumbnailUrl
        duration = this.duration ?: 0,
        artistId = null, // Set to null or get from another source
        albumName = null,
        albumId = null
    )
}

fun TrackFullOneDto.toPlaylistDetailSongs() = PlaylistDetailSongs(
    title = this.title,
    videoId = this.videoId,
    thumbnail = this.thumbnail,
    artistName =  this.artistName,
    artistId = this.artistId,
    duration = this.duration,
    albumId = this.albumId,
    albumName = this.albumName
)

fun PlaylistDetailSongs.toTrackFullDto() = TrackFullOneDto(
    title = title,
    videoId = videoId,
    thumbnail = thumbnail,
    artistName = artistName,
    artistId = artistId,
    albumId = albumId,
    albumName = albumName,
    duration = duration
)

fun PlaylistDetailSongs.toTrackFull() = TrackFullOne(
    title = title,
    videoId = videoId,
    thumbnail = thumbnail,
    artistName = artistName,
    artistId = artistId ?: "",
    albumId = albumId ?: "",
    albumName = albumName ?: "",
    duration = duration
)
