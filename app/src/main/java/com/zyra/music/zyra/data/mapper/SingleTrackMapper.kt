package com.zyra.music.zyra.data.mapper

import com.zyra.music.zyra.data.remote.dto.SingleTrackDto
import com.zyra.music.zyra.data.remote.dto.TrackFullOneDto
import com.zyra.music.zyra.domain.model.SingleTrack
import com.zyra.music.zyra.domain.model.TrackFullOne

private fun SingleTrackDto.toSingleTrack() = SingleTrack(
    title = this.title,
    artistName = this.artistName,
    url = this.url,
    thumbnail = this.thumbnail,
    duration = this.duration,
)
fun List<SingleTrackDto>.toSingleTrackList() = map { it.toSingleTrack() }

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