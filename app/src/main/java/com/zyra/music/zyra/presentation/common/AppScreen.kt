package com.zyra.music.zyra.presentation.common

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.zyra.music.zyra.navigation.AppNavGraph
import com.zyra.music.zyra.navigation.Route
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.playerScreen.miniPlayer.MiniPlayer
import com.zyra.music.zyra.presentation.utils.formatDurationLong
import org.koin.androidx.compose.koinViewModel

@OptIn(UnstableApi::class)
@Composable
fun AppScreen(modifier: Modifier = Modifier,
              startDestination : Route) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val mainMusicViewModel: MainMusicViewModel = koinViewModel()
    val mainState by mainMusicViewModel.uiState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.toRoute<Route>()

    val isPlayerScreenOrAuth = currentRoute is Route.PlayerScreen ||
                                        currentRoute is Route.LoginScreen ||
                                        currentRoute is Route.LoginCheckScreen

    val isBottomBarVisible = currentRoute?.isBottomBarVisible ?: true

    val isMiniPlayerVisible = !isPlayerScreenOrAuth && mainState.currentTrack != null

    Scaffold(
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = isMiniPlayerVisible,
                    enter = slideInVertically{it},
                    exit = slideOutVertically{it}
                ) {

                    mainState.currentTrack?.let { track ->
                        MiniPlayer(
                            imageUrl = track.thumbnail,
                            title = track.title,
                            isFavorite = mainState.isFavorite,
                            isPlaying = mainState.isPlaying,
                            progress = if (mainState.totalDuration > 0) {
                                mainState.currentPosition.toFloat() / mainState.totalDuration.toFloat()
                            } else 0f,
                            onClick = {},
                            onPlayPauseClick = {},
                            favoriteIconClick = {},
                            duration = formatDurationLong(mainState.totalDuration),
                            currentDuration = formatDurationLong(mainState.currentPosition)

                        )
                    }
                }
                AnimatedVisibility(visible = isBottomBarVisible) {
                    BottomNavigationBarN(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = startDestination,
            mainMusicViewModel = mainMusicViewModel,
            onSongClick = {track ->
                mainMusicViewModel.playRadioForSong(clickedTrack = track)
                navController.navigate(Route.PlayerScreen)
            }
        )
    }
}