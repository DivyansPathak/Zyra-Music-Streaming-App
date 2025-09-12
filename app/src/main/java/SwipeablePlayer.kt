//// file: com/zyra/music/zyra/presentation/newPlayer/SwipeablePlayer.kt
//
//package com.zyra.music.zyra.presentation.newPlayer
//
//import androidx.activity.compose.BackHandler
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.gestures.DraggableAnchors
//import androidx.compose.foundation.gestures.Orientation
//import androidx.compose.foundation.gestures.anchoredDraggable
//import androidx.compose.foundation.gestures.rememberAnchoredDraggableState
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxWithConstraints
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.IntOffset
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.lerp
//import com.zyra.music.zyra.presentation.playerScreen.PlayerScreen
//import com.zyra.music.zyra.presentation.playerScreen.miniPlayer.MiniPlayer
//import kotlin.math.roundToInt
//import androidx.media3.common.util.UnstableApi
//
//
//@OptIn(ExperimentalFoundationApi::class)
//@UnstableApi
//@Composable
//fun SwipeablePlayer(
//    musicViewModel: MainMusicViewModel,
//    playerState: NewPlayerState
//) {
//    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
//        val density = LocalDensity.current
//        val miniPlayerHeightPx = with(density) { 80.dp.toPx() }
//        val screenHeightPx = with(density) { maxHeight.toPx() }
//
//        // 1. ✅ CORRECT: Use the DraggableAnchors builder, not a Map.
//        val anchors = remember(screenHeightPx) {
//            DraggableAnchors {
//                PlayerDraggableState.COLLAPSED at screenHeightPx - miniPlayerHeightPx
//                PlayerDraggableState.EXPANDED at 0f
//            }
//        }
//
//        // 2. ✅ CORRECT: Use the 'rememberAnchoredDraggableState' helper.
//        // This correctly creates the state and links it to the anchors.
//        val draggableState = rememberAnchoredDraggableState(
//            initialValue = PlayerDraggableState.COLLAPSED,
//            anchors = anchors,
//            positionalThreshold = { distance -> distance * 0.5f },
//            velocityThreshold = { 100f },
//            animationSpec = tween()
//        )
//
//        // Sync ViewModel state -> UI state
//        LaunchedEffect(playerState.playerState) {
//            if (playerState.playerState != draggableState.currentValue) {
//                draggableState.animateTo(playerState.playerState)
//            }
//        }
//
//        // Sync UI state -> ViewModel state
//        LaunchedEffect(draggableState.currentValue) {
//            if (draggableState.currentValue != playerState.playerState) {
//                when (draggableState.currentValue) {
//                    PlayerDraggableState.COLLAPSED -> musicViewModel.onAction(NewPlayerAction.CollapsePlayer)
//                    PlayerDraggableState.EXPANDED -> musicViewModel.onAction(NewPlayerAction.ExpandPlayer)
//                }
//            }
//        }
//
//        // Back press handler to collapse the player
//        BackHandler(enabled = draggableState.currentValue == PlayerDraggableState.EXPANDED) {
//            musicViewModel.onAction(NewPlayerAction.CollapsePlayer)
//        }
//
//        // 3. ✅ CORRECT: 'positionOf' now works because 'anchors' is the right type.
//        val expandedOffset = anchors.positionOf(PlayerDraggableState.EXPANDED)
//        val collapsedOffset = anchors.positionOf(PlayerDraggableState.COLLAPSED)
//
//        // Calculate progress for animations (0.0 for expanded, 1.0 for collapsed)
//        val progress = ((draggableState.requireOffset() - expandedOffset) / (collapsedOffset - expandedOffset))
//            .coerceIn(0f, 1f)
//
//
//        Card(
//            modifier = Modifier
//                .offset {
//                    IntOffset(0, draggableState.offset.roundToInt())
//                }
//                .fillMaxSize()
//                .anchoredDraggable(
//                    state = draggableState,
//                    orientation = Orientation.Vertical
//                ),
//            shape = RoundedCornerShape(
//                topStart = lerp(0.dp, 12.dp, progress),
//                topEnd = lerp(0.dp, 12.dp, progress)
//            ),
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//        ) {
//            Box(modifier = Modifier.fillMaxSize()) {
//                // Full player is always visible, but fades out as you collapse
//                Box(modifier = Modifier.alpha(1f - progress)) {
//                   //TODO: PlayerScreen
//                }
//                // Mini player fades in as you collapse
//                Box(modifier = Modifier.alpha(progress)) {
//                   //TODO: MiniPlayer
//                }
//            }
//        }
//    }
//}