package com.zyra.music.zyra.presentation.playerScreen.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.booleanResource

@Composable
fun SeekBarShimmer(
    modifier: Modifier = Modifier,
    shimmerColor : Color = MaterialTheme.colorScheme.surface
) {

    val shimmerColor = listOf<Color>(
        shimmerColor.copy(alpha = 0.6f),
        shimmerColor.copy(alpha = 0.2f),
        shimmerColor.copy(alpha = 0.6f),
    )
    val transition = rememberInfiniteTransition()
    val transitionAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "seekShimmer"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColor,
        start = Offset.Zero,
        end = Offset(x = transitionAnimation.value,y = transitionAnimation.value)
    )

    Box(modifier = modifier){
        Box(modifier = Modifier
            .matchParentSize()
            .background(brush))
    }
}