package com.zyra.music.zyra.data.mapper

import com.zyra.music.zyra.data.remote.dto.SongResultDto
import com.zyra.music.zyra.domain.model.SongResult

fun SongResultDto.toSongResult() = SongResult(
  streamUrl = this.streamUrl
)