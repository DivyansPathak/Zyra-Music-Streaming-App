package com.zyra.music.zyra.presentation.common

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.navigation.HomeScreen
import com.zyra.music.zyra.navigation.LibraryScreen
import com.zyra.music.zyra.navigation.LoginScreen
import com.zyra.music.zyra.navigation.MainScreens
import com.zyra.music.zyra.navigation.PlayListType
import com.zyra.music.zyra.navigation.PlayerScreen
import com.zyra.music.zyra.navigation.PlaylistScreen
import com.zyra.music.zyra.navigation.ProfileScreen
import com.zyra.music.zyra.navigation.SearchScreen
import com.zyra.music.zyra.presentation.common.miniPlayer.MiniPlayer
import com.zyra.music.zyra.presentation.addPlaylist.AddPlaylistViewModel
import com.zyra.music.zyra.presentation.home.HomeScreen
import com.zyra.music.zyra.presentation.home.HomeViewModel
import com.zyra.music.zyra.presentation.libraryScreen.LibraryScreen
import com.zyra.music.zyra.presentation.libraryScreen.LibraryViewModelNew
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.newPlayer.NewPlayerAction
import com.zyra.music.zyra.presentation.playlistScreen.ComposePlaylistScreen
import com.zyra.music.zyra.presentation.playlistScreen.PlaylistEvent
import com.zyra.music.zyra.presentation.playlistScreen.PlaylistViewModel
import com.zyra.music.zyra.presentation.profileScreen.ComposeProfileScreen
import com.zyra.music.zyra.presentation.profileScreen.ProfileEvent
import com.zyra.music.zyra.presentation.profileScreen.ProfileViewModel
import com.zyra.music.zyra.presentation.searchScreen.SearchScreenN
import com.zyra.music.zyra.presentation.searchScreen.SearchViewModel
import com.zyra.music.zyra.presentation.utils.formatDurationLong
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "BACK_PRESS_DEBUG"

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MainScreenWithBottomBar(
    appTopBackStack: NavBackStack<NavKey>,
    mainViewModel: MainMusicViewModel,
    addPlaylistViewModel: AddPlaylistViewModel,
    onAddToPlaylistClick: (TrackFullOne) -> Unit,
    snackbarHostState: SnackbarHostState
) {

    val mainBackStack = rememberNavBackStack<MainScreens>(HomeScreen)
    val searchViewModel: SearchViewModel = koinViewModel()
    val homeViewModel: HomeViewModel = koinViewModel()
    val libraryViewModel: LibraryViewModelNew = koinViewModel()
    val profileViewModel: ProfileViewModel = koinViewModel()
    val mainState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val isMiniPlayerVisible = mainState.currentTrack != null

    var controlsHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val context = LocalContext.current
    val activity = context as? Activity
    var backPressedOnce by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val backToHome: () -> Unit = {
        if (mainBackStack.size > 1) {
            mainBackStack.removeLastOrNull()
        } else {
            mainBackStack.clear()
            mainBackStack.add(HomeScreen)
        }
    }

    BackHandler {
        Log.d(TAG, "Back pressed!")

        val currentScreen = mainBackStack.lastOrNull()

        if (currentScreen != HomeScreen) {
            mainBackStack.clear()
            mainBackStack.add(HomeScreen)
            backPressedOnce = false
        } else {
            if (backPressedOnce) {
                activity?.moveTaskToBack(true)
            } else {
                backPressedOnce = true
                Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                coroutineScope.launch {
                    delay(2000)
                    backPressedOnce = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        profileViewModel.uiEvent.collect { event ->
            when (event) {
                is ProfileEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is ProfileEvent.NavigateToLoginScreen -> {
                    appTopBackStack.clear()
                    appTopBackStack.add(LoginScreen)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        contentColor = MaterialTheme.colorScheme.primary,

        ) { innerPadding ->
        Box(
            modifier = Modifier
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
                            contentPadding = controlsHeight,
                            onRefresh = homeViewModel::refresh,
                            onPlaylistClick = { playlistId, playlistType ->
                                mainBackStack.add(
                                    PlaylistScreen(
                                        id = playlistId,
                                        type = playlistType
                                    )
                                )
                            },
                            onSearchClick = {
                                mainBackStack.add(SearchScreen)
                            }
                        )
                    }

                    entry<SearchScreen> {
                        val state by searchViewModel.uiState.collectAsStateWithLifecycle()
                        SearchScreenN(
                            state = state,
                            contentPadding = controlsHeight,
                            onAction = searchViewModel::onAction,
                            eventFlow = searchViewModel.uiEvent,
                            mainState = mainState,
                            onSongClick = { track ->
                                mainViewModel.playRadioForSong(track)
                                appTopBackStack.add(PlayerScreen)
                            },
                            onNextPlayClick = { track -> mainViewModel.addSongToPlayNext(track) },
                            addToQueueClick = { track -> mainViewModel.addSongToQueue(track) },
                            addToPlaylistClick = { track -> onAddToPlaylistClick(track) },
                            onBackClick = {
                                backToHome()
                            },
                            addToFavoriteClick = { track -> mainViewModel.toggleFavoriteById(track.videoId) }
                        )
                    }
                    entry<LibraryScreen> {
                        val state by libraryViewModel.uiState.collectAsStateWithLifecycle()
                        LibraryScreen(
                            onPlaylistClick = { playlistId, playlistType ->
                                mainBackStack.add(
                                    PlaylistScreen(
                                        id = playlistId,
                                        type = playlistType
                                    )
                                )
                            },
                            state = state,
                            onScreenTypeSelected = {}
                        )


                    }
                    entry<ProfileScreen> {
                        val state by profileViewModel.uiState.collectAsStateWithLifecycle()
                        ComposeProfileScreen(
                            state = state,
                            onAction = profileViewModel::onAction,
                        )
//                        LibraryScreenTest()
                    }
                    entry<PlaylistScreen> { screen ->
                        val playlistViewModel: PlaylistViewModel = koinViewModel(
                            parameters = { parametersOf(screen.id, screen.type) }
                        )
                        val state by playlistViewModel.uiState.collectAsStateWithLifecycle()
                        LaunchedEffect(Unit) {
                            playlistViewModel.getPlaylistSongsDetails(screen.id, screen.type)
                        }
                        val removeFun: ((TrackFullOne) -> Unit)? =
                            if (state.playlistDetails?.type == PlayListType.USER_CREATED) {
                                { trackToRemove ->
                                    playlistViewModel.removeSongFromPlaylist(track = trackToRemove)
                                }
                            } else {
                                null
                            }
                        LaunchedEffect(playlistViewModel) {
                            playlistViewModel.uiEvent.collect { event ->
                                when (event) {
                                    is PlaylistEvent.ShowMessage -> {
                                        snackbarHostState.showSnackbar(event.message)
                                    }
                                }
                            }
                        }
                        ComposePlaylistScreen(
                            mainMusicViewModel = mainViewModel,
                            mainState = mainState,
                            state = state,
                            contentPadding = controlsHeight,
                            onBack = {
                                mainBackStack.removeLastOrNull()
                            },
                            addSongToPlaylist = { trackFullOne ->
                                Log.d(TAG, "onAddPlaylistClicked with song $trackFullOne")
                                onAddToPlaylistClick(trackFullOne)
                            },
                            onRemoveSongFromPlaylist = removeFun
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged { size ->
                        controlsHeight = with(density) { size.height.toDp() }
                    }) {
                AnimatedVisibility(
                    visible = isMiniPlayerVisible,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
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
                val currentScreen = mainBackStack.lastOrNull() as? MainScreens
                val isBottomBarVisible = currentScreen !is ProfileScreen

                AnimatedVisibility(
                    visible = isBottomBarVisible,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    BottomBarInvisible(
                        modifier = Modifier.fillMaxWidth(),
                        currentScreen = mainBackStack.lastOrNull() as? MainScreens,
                        onTabSelected = {
                            mainBackStack.clear()
                            mainBackStack.add(it)
                        }
                    )
                }
            }
        }
    }
}
