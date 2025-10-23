package com.zyra.music.zyra.presentation.newPlayer

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.util.UnstableApi
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zyra.music.zyra.R
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.common.GradientScreenContainer
import com.zyra.music.zyra.presentation.newPlayer.component.PlayerTopBar
import com.zyra.music.zyra.presentation.newPlayer.component.QueueItem
import com.zyra.music.zyra.presentation.newPlayer.component.YoutubeStyleSeekBar
import com.zyra.music.zyra.presentation.playerScreen.RepeatMode
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun PlayerScreenN(
    modifier: Modifier = Modifier,
    state: NewPlayerState,
    onAction: (NewPlayerAction) -> Unit,
    eventFlow: Flow<NewPlayerEvent>,
    navigateToBack: () -> Unit,
    onAddToPlaylistClick: (TrackFullOne) -> Unit
) {

    BackHandler {
        navigateToBack()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when(event){
                is NewPlayerEvent.NavigateToBack -> {
                    navigateToBack()
                }
                is NewPlayerEvent.ShowMessage ->{
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }


    GradientScreenContainer(imagerUrl = state.currentTrack?.thumbnail) {
       Box(modifier = Modifier.fillMaxSize()){
           var showQueueSheet by remember { mutableStateOf(false) }

           if (showQueueSheet) {
               ModalBottomSheet(
                   onDismissRequest = { showQueueSheet = false },
               ) {
                   LazyColumn {
                       itemsIndexed(state.queue) { index, song ->
                           QueueItem(
                               song = song, onClick =
                                   {
                                       onAction(NewPlayerAction.PlayFromQueue(index))
                                       showQueueSheet = false
                                   },
                               isCurrentlyPlaying = (song.videoId == state.currentTrack?.videoId),
                               onRemoveClick = { onAction(NewPlayerAction.RemoveFromQueue(index)) },
                               isPlaying = state.isPlaying
                           )
                       }
                   }
               }
           }

           ConstraintLayout(
               modifier = Modifier
                   .fillMaxSize()
                   .padding(16.dp)
           ) {
               val (topBar,
                   thumbnail,
                   title,
                   controlPanel,
                   seekBar,
                   controlsButtons,
                   bottomMenu) = createRefs()

               PlayerTopBar(
                   modifier = Modifier.constrainAs(topBar) {
                       top.linkTo(parent.top)
                       start.linkTo(parent.start)
                       end.linkTo(parent.end)
                       width = Dimension.fillToConstraints
                   },
                   onBackClick = { onAction(NewPlayerAction.Back) },
                   timerText = state.sleepTimeRemaining,
               )

               ThumbnailSection(
                   modifier = Modifier.constrainAs(thumbnail) {
                       top.linkTo(topBar.bottom, margin = 16.dp)
                       start.linkTo(parent.start)
                       end.linkTo(parent.end)
                       width = Dimension.fillToConstraints
                       height = Dimension.percent(0.45f)
                   },
                   imageUrl = state.currentTrack?.thumbnail,
                   isLoading = state.isLoading
               )
               SongDetailSection(
                   modifier = Modifier.constrainAs(title) {
                       top.linkTo(thumbnail.bottom, margin = 16.dp)
                       start.linkTo(parent.start)
                       end.linkTo(parent.end)
                       width = Dimension.fillToConstraints
                   },
                   songTitle = state.currentTrack?.title,
                   songArtist = state.currentTrack?.artistName
               )
               ControlPanel(
                   modifier = Modifier.constrainAs(controlPanel) {
                       top.linkTo(title.bottom, margin = 20.dp)
                       start.linkTo(parent.start)
                       end.linkTo(parent.end)
                       bottom.linkTo(seekBar.top, margin = 16.dp)
                       width = Dimension.fillToConstraints
                   },
                   isFavorite = state.isFavorite,
                   isEndTrackTimer = state.isEndTrackTimerActive,
                   onFavoriteClick = { onAction(NewPlayerAction.ToggleFavorite) },
                   onShareClick = { onAction(NewPlayerAction.Share) },
                   onAddToPlaylistClick = {state.currentTrack?.let { trackFullOne ->
                       onAddToPlaylistClick(trackFullOne)
                   }},
                   onDownLoadClick = {},
                   setSleepTimer = {timer -> onAction(NewPlayerAction.SetSleepTimer(timer)) },
                   setSleepTimerCurrentTrack = { onAction(NewPlayerAction.SetSleepTimerToEndOfTrack) },
                   cancelTimer = { onAction(NewPlayerAction.CancelSleepTimer) }
               )
               SeekBarSection(
                   modifier = Modifier.constrainAs(seekBar) {
                       top.linkTo(controlPanel.bottom, margin = 16.dp)
                       start.linkTo(parent.start, margin = 8.dp)
                       end.linkTo(parent.end, margin = 8.dp)
                       bottom.linkTo(controlsButtons.top, margin = 16.dp)
                       width = Dimension.fillToConstraints
                   },
                   currentPosition = state.currentPosition,
                   totalDuration = state.totalDuration,
                   isLoading = state.isLoading,
                   onSeek = { fraction ->
                       val targetPosition = (fraction * state.totalDuration).toFloat()
                       onAction(NewPlayerAction.SeekTo(targetPosition))
                   }
               )
               PlayControls(
                   modifier = Modifier.constrainAs(controlsButtons) {
                       top.linkTo(controlPanel.bottom, margin = 16.dp)
                       start.linkTo(parent.start)
                       end.linkTo(parent.end)
                       bottom.linkTo(bottomMenu.top)
                       width = Dimension.fillToConstraints
                   },
                   isPlaying = state.isPlaying,
                   repeatMode = state.repeatMode,
                   shuffleModeEnabled = state.shuffleModeEnabled,
                   onPlayPauseClick = { onAction(NewPlayerAction.PlayPause) },
                   onPreviousClick = { onAction(NewPlayerAction.SkipToPrevious) },
                   onNextClick = { onAction(NewPlayerAction.SkipToNext) },
                   onShuffleClick = { onAction(NewPlayerAction.ToggleShuffle) },
                   onRepeatClick = { onAction(NewPlayerAction.CycleRepeatMode) },
               )

               BottomMenuRow(
                   modifier = Modifier.constrainAs(bottomMenu) {
                       bottom.linkTo(parent.bottom, margin = 16.dp)
                       start.linkTo(parent.start)
                       end.linkTo(parent.end)
                       width = Dimension.fillToConstraints
                   },
                   onUpNextClick = { showQueueSheet = true },
                   onLyricsClick = {},
                   onRelatedClick = {}
               )

           }
           SnackbarHost(
               hostState = snackbarHostState,
               modifier = Modifier
                   .align(Alignment.BottomCenter)
                   .padding(bottom = 50.dp, start = 16.dp, end = 16.dp)
           )
       }
    }
}

@Composable
private fun ThumbnailSection(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    isLoading: Boolean
) {
    val context = LocalContext.current
    val imageRequest = ImageRequest
        .Builder(context)
        .data(imageUrl)
        .crossfade(true)
        .build()
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.preview_pager),
            error = painterResource(id = R.drawable.preview_pager)
        )

        if (isLoading) {
            CircularProgressIndicator()
        }

    }
}


@Composable
fun ControlPanel(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onDownLoadClick: () -> Unit,
    setSleepTimer: (Long) -> Unit,
    setSleepTimerCurrentTrack: () -> Unit,
    cancelTimer: () -> Unit,
    isEndTrackTimer: Boolean
) {

    var showDropMenu by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = onFavoriteClick) {

            Crossfade(targetState = isFavorite) { favorite ->
                val tintColor = if (isFavorite) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
                Icon(
                    if (favorite) painterResource(id = R.drawable.heart_filled)
                    else painterResource(id = R.drawable.heart_outlined),
                    contentDescription = "favorite",
                    tint = tintColor
                )
            }
        }
        Box{
            IconButton(onClick = { showDropMenu = true }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.timer_show),
                    contentDescription = "set timer",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                expanded = showDropMenu,
                onDismissRequest = { showDropMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Off") },
                    onClick = {
                        cancelTimer()
                        showDropMenu = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = "Stop when current song ends") },
                    onClick = {
                        setSleepTimerCurrentTrack()
                        showDropMenu = false

                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "15 minutes") },
                    onClick = {
                        setSleepTimer(15)
                        showDropMenu = false

                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "30 minutes") },
                    onClick = {
                        setSleepTimer(30)
                        showDropMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "45 minutes") },
                    onClick = {
                        setSleepTimer(45)
                        showDropMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "60 minutes") },
                    onClick = {
                        setSleepTimer(60)
                        showDropMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "Custom") },
                    onClick = {
                        showDropMenu = false
                    }
                )
            }

        }
        IconButton(onClick = onShareClick) {
            Icon(
                painter = painterResource(id = R.drawable.icon_share),
                contentDescription = "share button",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = { onAddToPlaylistClick()
            Log.d("ButtonAddPlaylist","Button is clicked")
        },
            modifier = Modifier
        ) {
            Icon(
                painter = painterResource(id = R.drawable.playlist_icon),
                contentDescription = "add to playlist",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onDownLoadClick) {
            Icon(
                painter = painterResource(id = R.drawable.download),
                contentDescription = "Download",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SongDetailSection(
    modifier: Modifier = Modifier,
    songTitle: String?,
    songArtist: String?
) {

    Column(modifier = modifier) {
        Text(
            text = songTitle ?: "Unknown Title", style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            modifier = Modifier.basicMarquee(
                iterations = Int.MAX_VALUE,
                initialDelayMillis = 5000,
                repeatDelayMillis = 3000,
                velocity = 10.dp,
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = songArtist ?: "Unknown Artist", style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Composable
private fun PlayControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    repeatMode: RepeatMode,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    shuffleModeEnabled: Boolean
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceAround,
    ) {

        IconButton(
            onClick = onShuffleClick,
        ) {
            val tintColor =
                if (shuffleModeEnabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.shuffle),
                contentDescription = "shuffle Button",
                tint = tintColor
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = onPreviousClick) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.step_backward),
                    contentDescription = "previous click",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            PlayPauseButton(
                onPlayPauseClick = onPlayPauseClick,
                isPlaying = isPlaying
            )
            IconButton(onClick = onNextClick) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.step_forward),
                    contentDescription = "next click",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

        }

        IconButton(onClick = onRepeatClick) {
            val tintColor =
                if (repeatMode == RepeatMode.OFF) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.primary
            Icon(
                modifier = Modifier.size(20.dp),
                painter = if (repeatMode == RepeatMode.ONE) painterResource(id = R.drawable.arrows_repeat_one)
                else painterResource(id = R.drawable.arrows_repeat),
                contentDescription = "repeat click",
                tint = tintColor
            )
        }


    }

}

@Composable
private fun PlayPauseButton(
    modifier: Modifier = Modifier,
    onPlayPauseClick: () -> Unit,
    isPlaying: Boolean,
) {
    Crossfade(targetState = isPlaying) { playing ->
        Surface(
            modifier = modifier
                .size(64.dp)
                .clip(shape = CircleShape)
                .clickable { onPlayPauseClick() },
            color = MaterialTheme.colorScheme.onSurface
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(16.dp), painter = if (playing) painterResource(id = R.drawable.pause)
                else painterResource(id = R.drawable.play),
                contentDescription = "play/pause",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SeekBarSection(
    modifier: Modifier = Modifier,
    currentPosition: Long,
    totalDuration: Long,
    isLoading: Boolean,
    onSeek: (Float) -> Unit
) {

    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val remainingSeconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    Column(modifier = modifier) {
        YoutubeStyleSeekBar(
            modifier = Modifier,
            progress = if (totalDuration > 0) {
                currentPosition.toFloat() / totalDuration.toFloat()
            } else 0f,
            seekTo = onSeek
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = formatTime(totalDuration),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BottomMenuRow(
    modifier: Modifier = Modifier,
    onUpNextClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onRelatedClick: () -> Unit,
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Up Next",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable { onUpNextClick() }
        )

        Text(
            text = "Lyrics",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable { onLyricsClick() }
        )

        Text(
            text = "Related",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable { onRelatedClick() }
        )
    }


}

@UnstableApi
@Preview(
    showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewPlayerScreen() {
    ZyraTheme() {
        PlayerScreenN(
            state = NewPlayerState(),
            onAction = {},
            eventFlow = emptyFlow(),
            navigateToBack = {},
            onAddToPlaylistClick = {}
        )
    }
}
