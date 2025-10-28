package com.zyra.music.zyra.presentation.newPlayer.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme
import kotlinx.coroutines.launch

@Composable
fun YoutubeStyleSeekBar(
    modifier: Modifier = Modifier,
    progress: Float,
    seekTo: (Float) -> Unit,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
//    val isDragging by interactionSource.collectIsDraggedAsState()

    var isDragging by remember { mutableStateOf(false) }
    val displayProgress = remember { Animatable(progress) }

    LaunchedEffect(progress) {
       if(!isDragging){
           displayProgress.animateTo(
               targetValue = progress,
               animationSpec = spring(stiffness = Spring.StiffnessLow)
           )
       }
    }



    val thumbSize by animateDpAsState(
        targetValue = if (isDragging) 14.dp else 6.dp,
        label = "thumbSize"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val newProgress = (tapOffset.x / size.width).coerceIn(0f, 1f)
                    seekTo(newProgress)
                    scope.launch {
                        displayProgress.stop()
                        displayProgress.snapTo(newProgress)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
//                        seekTo(newProgress)
                        isDragging = true
                        scope.launch {
                            displayProgress.stop()
                        }
                        scope.launch {
                            displayProgress.snapTo(newProgress)
                        }
                    },
                    onDrag = { change, _ ->
                        val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
//                        seekTo(newProgress)
//                        change.consume()
                        scope.launch {
                            displayProgress.snapTo(newProgress)
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        val finalProgress = displayProgress.value
                        seekTo(finalProgress)
                        isDragging = false
                    },
                    onDragCancel = {
                        isDragging = false
                    }
                )
            }

    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val trackHeight = 4.dp.toPx()
            val trackY = center.y

            val currentProgress = displayProgress.value

            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(0f,size.height/2),
                end = Offset(size.width, size.height/2),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.Red,
                start = Offset(0f,size.height/2),
                end = Offset(currentProgress * size.width , trackY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color.White,
                radius = (thumbSize / 2).toPx().coerceAtLeast(6.dp.toPx()),
                center = Offset(currentProgress * size.width, trackY)
            )
        }

    }
}
