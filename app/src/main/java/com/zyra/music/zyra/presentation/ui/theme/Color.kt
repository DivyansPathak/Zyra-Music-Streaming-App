package com.zyra.music.zyra.presentation.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


val NeonGreen = Color(0xFFC5F64D) // main accent
val NeonPink = Color(0xFFFF4DF3) // vibrant highlight
val NeonBlue = Color(0xFF4D9BFF) // cool tones for variety
val NeonPurple = Color(0xFFB84DFF) // moody, synthwave style
val DeepBlack = Color(0xFF0D0D0D) // main background
val OffBlack = Color(0xFF1A1A1A) // subtle surface contrast
val PureWhite = Color(0xFFFFFFFF) // primary text
val LightGray = Color(0xFFA0A0A0) // secondary text

val PureBlack = Color.Black
val MidGray = Color.Gray
val DarkGray = Color.DarkGray
val RedError = Color(0xFFB00020)

val ProgressGradient = Brush.linearGradient(
    colors = listOf(NeonGreen, NeonBlue)
)

val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(DeepBlack, OffBlack, Color(0xFF111122))
)

val PlayButtonGradient = Brush.linearGradient(
    colors = listOf(NeonPink, NeonPurple)
)

val BottomBarGradient = Brush.horizontalGradient(
    colors = listOf(NeonGreen, NeonBlue, NeonPink)
)