package com.zyra.music.zyra.presentation.common

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zyra.music.zyra.domain.model.SingleTrack
import com.zyra.music.zyra.exoplayer.MusicService
import com.zyra.music.zyra.navigation.NavGraph
import com.zyra.music.zyra.navigation.Route
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.newPlayer.NewPlayerAction
import com.zyra.music.zyra.presentation.newPlayer.NewPlayerEvent
import com.zyra.music.zyra.presentation.playerScreen.PlayerAction
import com.zyra.music.zyra.presentation.playerScreen.miniPlayer.MiniPlayer
import com.zyra.music.zyra.presentation.playerScreen.MusicViewModel
import com.zyra.music.zyra.presentation.utils.formatDurationLong
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

@androidx.media3.common.util.UnstableApi
@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val musicViewModel: MusicViewModel = koinViewModel()
    val state by musicViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val mainMusicViewModel: MainMusicViewModel = koinViewModel()
    val mainState by mainMusicViewModel.uiState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route


    Log.d("MainScreen", "$currentDestination")
    val isPlayerScreen = currentDestination == Route.PlayerScreen.title
//        currentDestination?.startsWith(playerRouteString ?: "") == true

    val isBottomBarVisible = when {
//        currentDestination?.startsWith("com.zyra.music.zyra.navigation.Route.PlaylistScreen") == true -> true

        currentDestination == Route.HomeScreen.title -> true
        currentDestination == Route.SearchScreen.title -> true
        currentDestination?.startsWith("com.zyra.music.zyra.navigation.Route.PlaylistScreen") == true -> true
        currentDestination == Route.ProfileScreen.title -> true
        else -> false
    }

    val isMiniPlayerVisible = !isPlayerScreen && state.currentTrack != null

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        mainMusicViewModel.uiEvent.collect { event ->
            when (event) {
                is NewPlayerEvent.ShowMessage -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }

                is NewPlayerEvent.NavigateToBack -> {
                    navController.popBackStack()
                }

                is NewPlayerEvent.OpenShareDialog -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Listen to ${event.songTitle} on ZYRA MUSIC!"
                        )
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = {
            AnimatedVisibility(visible = isBottomBarVisible) {
                Column {
                    AnimatedVisibility(
                        visible = isMiniPlayerVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        mainState.currentTrack?.let { track ->
                            MiniPlayer(
                                imageUrl = track.thumbnail,
                                title = track.title,
                                isPlaying = mainState.isPlaying,
                                isFavorite = mainState.isFavorite,
                                progress = if (mainState.totalDuration > 0) {
                                    mainState.currentPosition.toFloat() / mainState.totalDuration.toFloat()
                                } else 0f,
                                onPlayPauseClick = {
//                                    musicViewModel.onAction(PlayerAction.PlayPause)
                                    mainMusicViewModel.onAction(NewPlayerAction.PlayPause)
                                },
                                favoriteIconClick = {
                                    mainMusicViewModel.onAction(NewPlayerAction.ToggleFavorite)
                                },
                                onClick = {
//                                    val trackJson =
//                                        Json.encodeToString(SingleTrack.serializer(), track)
//                                    navController.navigate(Route.PlayerScreen(jsonTrack = trackJson)) {
//
//                                    }
                                    navController.navigate(Route.PlayerScreen.title)
                                },
                                duration = formatDurationLong(state.totalDuration),
                                currentDuration = formatDurationLong(state.currentPosition)
                            )
                        }
                    }
                    BottomNavigationBar(
                        navController = navController,
                        currentDestination = currentDestination
                    )
                }
            }
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            NavGraph(
                navController = navController,
//                paddingValues = paddingValues,
//                musicViewModel = musicViewModel,
//                modifier = Modifier.weight(1f),
                mainMusicViewModel = mainMusicViewModel,
                onSongClick = { track ->

                    val intent = Intent(context, MusicService::class.java)
                    context.startForegroundService(intent)
//                    musicViewModel.playSongFromList(clickedTrack = track, fullList = state.queue)
//                    musicViewModel.loadSong(track = track)
//                    musicViewModel.playSongAddCreateQueue(clickedTrack = track)
//                    val trackJson = Json.encodeToString(SingleTrack.serializer(), track)
//                    navController.navigate(Route.PlayerScreen(jsonTrack = trackJson))

                    mainMusicViewModel.playRadioForSong(clickedTrack = track)
                    navController.navigate(Route.PlayerScreen.title)
                }
            )

        }
    }

}