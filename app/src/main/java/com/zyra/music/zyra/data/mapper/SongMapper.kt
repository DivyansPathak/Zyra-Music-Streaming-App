package com.zyra.music.zyra.data.mapper

import com.zyra.music.zyra.data.remote.dto.TrackFullOneDto
import com.zyra.music.zyra.data.remote.dto.playlistDetails.PlaylistDetailSongs
import com.zyra.music.zyra.domain.model.SingleTrack
import com.zyra.music.zyra.domain.model.TrackFullOne

private fun SingleTrack.toTrackFullOne() = TrackFullOne(
    title = title,
    artistName = artistName ?: "Unknown Artist",
    videoId = url.substringAfter("v="),
    duration = duration,
    thumbnail = thumbnail,
    artistId = "",
    albumName = "",
    albumId = ""
)

fun List<SingleTrack>.toListTrackFullOne() = map { it.toTrackFullOne() }

