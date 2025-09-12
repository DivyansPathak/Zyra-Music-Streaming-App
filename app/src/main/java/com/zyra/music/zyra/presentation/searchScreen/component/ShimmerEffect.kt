package com.zyra.music.zyra.presentation.searchScreen.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerEffectSearch(
    modifier: Modifier = Modifier,
    shimmerColor: Color = MaterialTheme.colorScheme.surface
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
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "Transition Animation"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColor,
        start = Offset.Zero,
        end = Offset(x = transitionAnimation.value, y = transitionAnimation.value)
    )

    Row(modifier = modifier
        .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .size(80.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .background(brush),
            )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Box(modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(20.dp)
                .clip(shape = MaterialTheme.shapes.medium)
                .background(brush))

            Box(modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(16.dp)
                .clip(shape = MaterialTheme.shapes.medium)
                .background(brush))

        }
        Box(modifier = Modifier
            .size(24.dp)
            .clip(shape = MaterialTheme.shapes.small)
            .background(brush))
    }
}

@Preview
@Composable
private fun Preview() {
    ShimmerEffectSearch(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
    )

}