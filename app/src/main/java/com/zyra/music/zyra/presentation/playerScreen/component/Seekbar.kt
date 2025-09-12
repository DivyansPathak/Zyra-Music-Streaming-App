package com.zyra.music.zyra.presentation.playerScreen.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongSeekBar(
    progress: Float, // 0f..1f
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var isDragging by remember { mutableStateOf(false) }

    val color by animateColorAsState(
        targetValue = if (isDragging) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
        label = "thumbColor"
    )
    Slider(
        value = progress,
        onValueChange = {
            isDragging = true
            onValueChange(it)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        enabled = enabled,
        colors = SliderDefaults.colors(
            thumbColor = color,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
        ),
        thumb = {
            val size by animateDpAsState(
                targetValue = if (isDragging) 14.dp else 8.dp,
                label = "thumbSize"
            )
            Box(
                modifier = Modifier
                    .size(size)
                    .background(Color.Black, CircleShape)
            )
        },
        onValueChangeFinished = {
            isDragging = false
        }
    )
}
@ExperimentalMaterial3Api
@Composable
fun YouTubeStyleSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

    val thumbSize by animateDpAsState(
        targetValue = if (isDragging) 14.dp else 0.dp,
        label = "thumbSize"
    )
    val color by animateColorAsState(
        targetValue = if (isDragging) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
        label = "thumbColor"
    )

    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        onValueChangeFinished = onValueChangeFinished,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        thumb = {
            // Smaller thumb like YouTube
            Box(
                Modifier
                    .size(thumbSize)
                    .offset(y = (1).dp)// YouTube-ish size
                    .background(
                        color = color,
                        shape = CircleShape)

            )
        },
        track = { sliderState ->
            // Custom thin track
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp) // thin like LinearProgressIndicator
            ) {
                SliderDefaults.Track(
                    sliderState = sliderState,
                    enabled = enabled,
                    colors = SliderDefaults.colors(
                        activeTrackColor = color,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSeekbar() {
    ZyraTheme {
        var sliderPosition by remember { mutableStateOf(0.5f) }

        SongSeekBar(
            progress = sliderPosition,
            onValueChange = { sliderPosition = it }
        )

    }
}