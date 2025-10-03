package com.zyra.music.zyra.presentation.acommon

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.zyra.music.zyra.navigation.HomeScreen
import com.zyra.music.zyra.navigation.LibraryScreen
import com.zyra.music.zyra.navigation.MainScreens
import com.zyra.music.zyra.navigation.PlayerScreen
import com.zyra.music.zyra.navigation.SearchScreen
import com.zyra.music.zyra.presentation.acommon.miniPlayer.MiniPlayer
import com.zyra.music.zyra.presentation.home.HomeScreen
import com.zyra.music.zyra.presentation.home.HomeViewModel
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.newPlayer.NewPlayerAction
import com.zyra.music.zyra.presentation.searchScreen.SearchScreenN
import com.zyra.music.zyra.presentation.searchScreen.SearchViewModel
import com.zyra.music.zyra.presentation.utils.formatDurationLong
import org.koin.androidx.compose.koinViewModel

@UnstableApi
@Composable
fun MainScreenWithBottomBar(
    appTopBackStack: NavBackStack<NavKey>,
    mainViewModel: MainMusicViewModel
) {

    val mainBackStack = rememberNavBackStack<MainScreens>(HomeScreen)
    val searchViewModel: SearchViewModel = koinViewModel()
    val homeViewModel: HomeViewModel = koinViewModel()
    val mainState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val isMiniPlayerVisible = mainState.currentTrack != null

    Scaffold(
        contentColor = MaterialTheme.colorScheme.primary,
        bottomBar = {
            FeaturedBottomBarN(
                currentScreen = mainBackStack.lastOrNull() as? MainScreens,
                onScreenSelected = {
                    mainBackStack.clear()
                    mainBackStack.add(it)
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = mainBackStack,
                onBack = { mainBackStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<HomeScreen> {
                        val state by homeViewModel.uiState.collectAsStateWithLifecycle()
                        HomeScreen(
                            state = state,
                            onRefresh = homeViewModel::refresh,
                            onPlaylistClick = {},
                        )
                    }

                    entry<SearchScreen> {
                        val state by searchViewModel.uiState.collectAsStateWithLifecycle()
                        SearchScreenN(
                            state = state,
                            onAction = searchViewModel::onAction,
                            onSongClick = { track ->
                                mainViewModel.playRadioForSong(track)
                                appTopBackStack.add(PlayerScreen)
                            },
                            onNextPlayClick = { track -> mainViewModel.addSongToPlayNext(track) },
                            addToQueueClick = { track -> mainViewModel.addSongToQueue(track) },
                            onBackClick = { mainBackStack.removeLastOrNull() }
                        )
                    }
                    entry<LibraryScreen> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Library")
                        }
                    }
                }
            )

            AnimatedVisibility(
                visible = isMiniPlayerVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {

                mainState.currentTrack?.let { trackFullOne ->
                    MiniPlayer(
                        imageUrl = trackFullOne.thumbnail,
                        title = trackFullOne.title,
                        isPlaying = mainState.isPlaying,
                        isFavorite = mainState.isFavorite,
                        progress = if (mainState.totalDuration > 0) {
                            mainState.currentPosition.toFloat() / mainState.totalDuration.toFloat()
                        } else 0f,
                        onPlayPauseClick = { mainViewModel.onAction(NewPlayerAction.PlayPause) },
                        favoriteIconClick = { mainViewModel.onAction(NewPlayerAction.ToggleFavorite) },
                        onClick = { appTopBackStack.add(PlayerScreen) },
                        duration = formatDurationLong(mainState.totalDuration),
                        currentDuration = formatDurationLong(mainState.currentPosition)
                    )
                }

            }
        }
    }
}
