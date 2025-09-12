package com.zyra.music.zyra.presentation.utils

fun formatDurationLong(duration: Int): String {
    val minutes = duration / 60
    val seconds = duration % 60
    return String.format("%d:%02d", minutes, seconds)
}

fun formatDurationLong(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}