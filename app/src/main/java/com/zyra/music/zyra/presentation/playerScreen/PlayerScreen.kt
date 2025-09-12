package com.zyra.music.zyra.presentation.playerScreen

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zyra.music.zyra.R
import com.zyra.music.zyra.presentation.playerScreen.component.PlayerTopBar
import com.zyra.music.zyra.presentation.playerScreen.RepeatMode
import com.zyra.music.zyra.presentation.playerScreen.component.YouTubeStyleSlider
import com.zyra.music.zyra.presentation.playerScreen.MainPlayerState
import com.zyra.music.zyra.presentation.playerScreen.component.QueueItem
import com.zyra.music.zyra.presentation.playerScreen.component.SeekBarShimmer
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@ExperimentalMaterial3Api
@Composable
fun PlayerScreen(
    state: MainPlayerState,
    onAction: (PlayerAction) -> Unit,
    eventFlow: Flow<PlayerEvent>,
    modifier: Modifier = Modifier,
    navigateToBack: () -> Unit
) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is PlayerEvent.NavigateBack -> {
                    navigateToBack()
                }

                is PlayerEvent.ShowMessage -> {

                }

                is PlayerEvent.OpenShareDialog -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Listen to ${event.songTitle} on ZYRA MUSIC!")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            }
        }
    }

    var showQueueSheet by remember { mutableStateOf(false) }

    if(showQueueSheet){
        ModalBottomSheet(onDismissRequest = {showQueueSheet = false}) {
            LazyColumn {
                itemsIndexed(state.queue) { index,song ->
//                    Text(modifier = Modifier.padding(12.dp).clickable{onAction(PlayerAction.PlayFromQueue(index))},text = song.title, style = MaterialTheme.typography.labelSmall)
                    QueueItem(song = song, onClick =
                        {onAction(PlayerAction.PlayFromQueue(index))
                            showQueueSheet = false
                        })
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        PlayerTopBar(onBackClick = { onAction(PlayerAction.Back) })
        if (state.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            ThumbnailSection(
                imageUrl = state.currentTrack?.thumbnail,
                modifier = Modifier
                    .size(450.dp)
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(1f),
                isLoading = state.isLoading,

                )
            SongDetailSection(
                songTitle = state.currentTrack?.title.toString(),
                songArtist = state.currentTrack?.artistName.toString(),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ControlPanel(
                isFavorite = state.isFavorite,
                onFavoriteClick = { onAction(PlayerAction.ToggleFavorite) },
                onShareClick = { onAction(PlayerAction.Share) },
                onAddToPlaylistClick = {},
                onDownLoadClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {

                SeekBarSection(
                    currentPosition = state.currentPosition,
                    totalDuration = state.totalDuration,
                    isLoading = state.isLoading,
                    onSeek = { newPosition -> onAction(PlayerAction.Seek(newPosition)) },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                PlayControls(
                    isPlaying = state.isPlaying,
                    repeatMode = state.repeatMode,
                    shuffleModeEnabled = state.shuffleModeEnabled,
                    onPlayPauseClick = { onAction(PlayerAction.PlayPause) },
                    onPreviousClick = { onAction(PlayerAction.SkipToPrevious) },
                    onNextClick = { onAction(PlayerAction.SkipToNext) },
                    onShuffleClick = { onAction(PlayerAction.ToggleShuffle) },
                    onRepeatClick = { onAction(PlayerAction.CycleRepeatMode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)

                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UpNext(
                        modifier = Modifier.clickable{showQueueSheet = true}
                    )
                    Lyrics()
                    Related()

                }
                Spacer(modifier = Modifier.height(16.dp))

            }

        }
    }

}

@Composable
fun UpNext(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = "Up Next",
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun Lyrics(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Lyrics",
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun Related(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Related",
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun ControlPanel(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onDownLoadClick: () -> Unit,
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = onFavoriteClick) {
            val tintColor = if (isFavorite) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary
            Crossfade(targetState = isFavorite) { favorite ->
                Icon(
                    if (favorite) painterResource(id = R.drawable.heart_filled)
                    else painterResource(id = R.drawable.heart_outlined),
                    contentDescription = "favorite",
                    tint = tintColor
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
        IconButton(onClick = onAddToPlaylistClick) {
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
            modifier = modifier,
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = R.drawable.preview_pager),
            error = painterResource(id = R.drawable.preview_pager)
        )

        if (isLoading){
            CircularProgressIndicator()
        }

    }
}

@Composable
private fun SongDetailSection(
    modifier: Modifier = Modifier,
    songTitle: String,
    songArtist: String
) {

    Column(modifier = modifier) {
        Text(
            text = songTitle, style = MaterialTheme.typography.headlineMedium,
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
            text = songArtist, style = MaterialTheme.typography.titleMedium,
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
        YouTubeStyleSlider(
            value = currentPosition.toFloat(),
            onValueChange = onSeek,
            valueRange = 0f..totalDuration.toFloat(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = formatTime(totalDuration),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    ZyraTheme {
//        SongDetailSection(
//            songTitle = "Song Title",
//            songArtist = "Song Artist"
//        )
    }

}
