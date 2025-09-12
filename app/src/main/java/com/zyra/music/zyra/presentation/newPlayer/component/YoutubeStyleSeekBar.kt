package com.zyra.music.zyra.presentation.newPlayer.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@Composable
fun YoutubeStyleSeekBar(
    modifier: Modifier = Modifier,
    progress: Float,
    seekTo: (Float) -> Unit,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

    val thumbSize by animateDpAsState(
        targetValue = if (isDragging) 14.dp else 6.dp,
        label = "thumbSize"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
//            .pointerInput(Unit) {
//                detectDragGestures { change, dragAmount ->
//                    val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
//                    seekTo(newProgress)
//                }
//            }
            // Handle taps anywhere on the bar
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val newProgress = (tapOffset.x / size.width).coerceIn(0f, 1f)
                    seekTo(newProgress)
                }
            }
//            .pointerInput(Unit) {
//                // ✅ CORRECTED SPELLING HERE
//                detectDragGestures(
//                    onDragStart = { offset ->
//                        // This captures the initial tap position
//                        val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
//                        seekTo(newProgress)
//                    },
//                    onDrag = { change, _ ->
//                        // This handles the continuous drag movement
//                        val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
//                        seekTo(newProgress)
//                    }
//                )
//            }
            // Handle dragging the thumb
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        seekTo(newProgress)
                    },
                    onDrag = { change, _ ->
                        val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                        seekTo(newProgress)
                        change.consume()
                    }
                )
            }

    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            //background line
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(0f,size.height/2),
                end = Offset(size.width, size.height/2),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Progress Line
            drawLine(
                color = Color.Red,
                start = Offset(0f,size.height/2),
                end = Offset(progress*size.width, size.height/2),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Tiny thumb
            drawCircle(
                color = Color.White,
                radius = (thumbSize / 2).toPx().coerceAtLeast(6.dp.toPx()),
                center = Offset(progress * size.width, size.height / 2)
            )
        }

    }
}

@Preview
@Composable
private fun PreviewSeekBar() {

    ZyraTheme {
        YoutubeStyleSeekBar(
            progress = 0.5f,
            seekTo = {}
        )
    }

}