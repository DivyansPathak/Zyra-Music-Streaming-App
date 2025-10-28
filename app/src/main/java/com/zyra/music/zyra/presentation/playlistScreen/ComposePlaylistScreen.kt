package com.zyra.music.zyra.presentation.playlistScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.media3.common.util.UnstableApi
import coil3.compose.rememberAsyncImagePainter
import com.zyra.music.zyra.R
import com.zyra.music.zyra.domain.model.PlaylistDetails
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.common.commonThingForWholeApp.MenuItems
import com.zyra.music.zyra.presentation.common.GradientScreenContainer
import com.zyra.music.zyra.presentation.common.rememberDominantColorState
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.newPlayer.NewPlayerAction
import com.zyra.music.zyra.presentation.newPlayer.NewPlayerState
import com.zyra.music.zyra.presentation.playlistScreen.common.SongListItem
import kotlin.math.pow

private val headerHeight = 420.dp
private val toolbarHeight = 64.dp

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun ComposePlaylistScreen(
    mainMusicViewModel: MainMusicViewModel,
    state: PlaylistState,
    mainState: NewPlayerState,
    contentPadding: Dp = 0.dp,
    onBack: () -> Unit,
    addSongToPlaylist: (TrackFullOne) -> Unit,
    onRemoveSongFromPlaylist : ((TrackFullOne) -> Unit)? = null

) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            state.error != null -> Text(
                text = "Failed to load playlist: \n${state.error}",
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )

            state.playlistDetails != null -> {
                val lazyListState = rememberLazyListState()
                val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
                val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }
                val toolbarTransition by remember {
                    derivedStateOf {
                        if (lazyListState.firstVisibleItemIndex == 0) {
                            (lazyListState.firstVisibleItemScrollOffset / (headerHeightPx - toolbarHeightPx)).coerceIn(
                                0f,
                                1f
                            )
                        } else {
                            1f
                        }

                    }
                }

                GradientScreenContainer(imagerUrl = state.playlistDetails.coverImageUrl ?: "") {
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(top = headerHeight, bottom = contentPadding)
                    ) {
                        itemsIndexed(items = state.playlistDetails.tracks, key = { _, track -> track.videoId }) {  index, track ->
                            val isTrackFavorite = mainState.favoriteIds.contains(track.videoId)
                            SongListItem(
                                track = track,
                                isPlaying = mainState.isPlaying,
                                modifier = Modifier.clickable {
                                    mainMusicViewModel.playPlayList(
                                        tracks = state.playlistDetails.tracks,
                                        shuffle = false,
                                        startIndex = index
                                    )
                                },
                                trailingContent = {
                                    MenuItems(
                                        isFavorite = isTrackFavorite,
                                        onAddToNextPlay = {
                                            mainMusicViewModel.addSongToPlayNext(
                                                track
                                            )
                                        },
                                        onAddToQueue = { mainMusicViewModel.addSongToQueue(track) },
                                        onAddToPlaylist = { addSongToPlaylist(track) },
                                        onPlayAsRadioClick = {
                                            mainMusicViewModel.playRadioForSong(
                                                track
                                            )
                                        },
                                        onToggleFavorite = {
                                            mainMusicViewModel.toggleFavoriteById(
                                                track.videoId
                                            )
                                        },
                                        onRemoveFromPlaylist =  onRemoveSongFromPlaylist?.let { onRemove -> { onRemove(track)} }


                                    )
                                }
                            )
                        }
                    }
                    DynamicHeader(
                        playlist = state.playlistDetails,
                        transitionProgress = toolbarTransition,
                        mainState = mainState,
                        onPlayAllClick = {
                            mainMusicViewModel.playPlayList(
                                tracks = state.playlistDetails.tracks,
                                shuffle = false
                            )
                        },
                        onShuffleClick = {
                            mainMusicViewModel.onAction(NewPlayerAction.ToggleShuffle)
                            mainMusicViewModel.playPlayList(
                                tracks = state.playlistDetails.tracks,
                                shuffle = mainState.shuffleModeEnabled
                            )
                        }
                    )

                    CollapsingToolbar(
                        playlist = state.playlistDetails,
                        transitionProgress = toolbarTransition,
                        onBack = onBack
                    )
                    AnimatedThumbnail(
                        playlist = state.playlistDetails,
                        transitionProgress = toolbarTransition
                    )
                    AnimatedVisibility(
                        visible = toolbarTransition > 0.9f,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = contentPadding + 16.dp, end = 16.dp),
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut() + slideOutVertically { it / 2 }
                    ) {
                        FloatingActionButton(
                            onClick = {
                                mainMusicViewModel.playPlayList(
                                    tracks = state.playlistDetails.tracks,
                                    shuffle = mainState.shuffleModeEnabled
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.play),
                                contentDescription = "play",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicHeader(
    playlist: PlaylistDetails,
    transitionProgress: Float,
    mainState: NewPlayerState,
    onPlayAllClick: () -> Unit,
    onShuffleClick: () -> Unit
) {
    val imageSize = lerp(250.dp, 40.dp, transitionProgress)
    val paddingTop = lerp(20.dp, 12.dp, transitionProgress)
    val dynamicSpacerHeight = paddingTop + imageSize + 16.dp
    val shuffleEnableColor by animateColorAsState(
        targetValue = if (mainState.shuffleModeEnabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "shuffle"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .graphicsLayer {
                translationY = -transitionProgress * (headerHeight.toPx() / 2f)
                val fadeProgress = ((transitionProgress - 0.6f) / 0.4f).coerceIn(0f, 1f)
                alpha = 1f - transitionProgress.pow(1.2f)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dynamicSpacerHeight))
        Text(
            text = playlist.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = playlist.description ?: "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "download",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_share),
                    contentDescription = "share",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = { onPlayAllClick() },
                modifier = Modifier
                    .size(64.dp)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "play",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = { onShuffleClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.shuffle),
                    contentDescription = "shuffle",
                    tint = shuffleEnableColor
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = "more option",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun AnimatedThumbnail(
    playlist: PlaylistDetails,
    transitionProgress: Float
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val imageSize = lerp(250.dp, 40.dp, transitionProgress)
    val paddingTop = lerp(20.dp, 12.dp, transitionProgress)
    val startPaddingStart = (screenWidth - 230.dp) / 2
    val endPaddingStart = 72.dp
    val paddingStart = lerp(startPaddingStart, endPaddingStart, transitionProgress)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingTop, start = paddingStart)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = playlist.coverImageUrl),
            contentDescription = "Playlist thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(imageSize)

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollapsingToolbar(
    playlist: PlaylistDetails,
    transitionProgress: Float,
    onBack: () -> Unit
) {
    val toolbarColor = rememberDominantColorState(
        playlist.coverImageUrl ?: "",
        defaultColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val animateColor by animateColorAsState(
        targetValue = toolbarColor.value,
        label = "toolbarColor"
    )
    TopAppBar(
        modifier = Modifier.graphicsLayer {
            alpha = transitionProgress
        },
        title = {
            Text(
                text = playlist.title,
                modifier = Modifier
                    .padding(start = 64.dp) // Space for the animated thumbnail to settle
                    .graphicsLayer {
                        val textAlpha = ((transitionProgress - 0.5f) * 2).coerceIn(0f, 1f)
                        alpha = textAlpha
                    }
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = animateColor.copy(alpha = 0.87f),
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

