package com.zyra.music.zyra.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.zyra.music.zyra.data.remote.SupabaseClient
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.common.MainScreenWithBottomBar
import com.zyra.music.zyra.presentation.common.commonThingForWholeApp.AddPlaylistSheet
import com.zyra.music.zyra.presentation.common.commonThingForWholeApp.CreateNewPlaylistDialog
import com.zyra.music.zyra.presentation.common.commonThingForWholeApp.DeleteAlertDialog
import com.zyra.music.zyra.presentation.addPlaylist.AddPlaylistAction
import com.zyra.music.zyra.presentation.addPlaylist.AddPlaylistEvent
import com.zyra.music.zyra.presentation.addPlaylist.AddPlaylistViewModel
import com.zyra.music.zyra.presentation.login.LoginScreen
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.newPlayer.NewPlayerEvent
import com.zyra.music.zyra.presentation.newPlayer.PlayerScreenN
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import org.koin.androidx.compose.koinViewModel

private const val TAG = "AppNavigation"
@UnstableApi
@Composable
fun AppNavigation() {

    var startScreen by remember { mutableStateOf<AppScreens?>(null) }

    LaunchedEffect(Unit) {
        SupabaseClient.supabase.auth.sessionStatus.collect { status ->
            Log.d(TAG,"Session Status: $status")
            when(status){
                is SessionStatus.Authenticated -> {
                    Log.d(TAG,"Authenticated")
                        startScreen = MainGraph

                }
                is SessionStatus.NotAuthenticated -> {
                    Log.d(TAG,"NotAuthenticated")
                        startScreen = LoginScreen

                }
                SessionStatus.Initializing -> {
                    Log.d(TAG,"Initializing")
                }
                is SessionStatus.RefreshFailure -> {
                    Log.d(TAG,"RefreshFailure")
                        startScreen = LoginScreen
                }
            }
        }
    }


    if (startScreen == null){
        return
    }

    val appTopBackStack = rememberNavBackStack(startScreen!!)


    val mainMusicViewModel : MainMusicViewModel = koinViewModel()
    val addPlaylistViewModel : AddPlaylistViewModel = koinViewModel()

    val snackBarHostState = remember { SnackbarHostState() }

    val addPlaylistState by addPlaylistViewModel.uiState.collectAsStateWithLifecycle()
    var showPlaylistSheet by remember { mutableStateOf(false) }

    val onAddToPlaylistClick : (TrackFullOne) -> Unit = { track ->
        Log.d(TAG, "Step 1: Trigger received. Calling viewModel.onAction...")
        addPlaylistViewModel.onAction(AddPlaylistAction.SetSongAndShowSheet(track))
        showPlaylistSheet = true
    }

//    val onRemoveSongFromPlaylist : (TrackFullOne) -> Unit = {track ->
//        Log.d(TAG,"Step 1: Trigger received. Calling viewModel.onAction")
//        addPlaylistViewModel.onAction(AddPlaylistAction.RemoveSongFromPlaylist())
//    }

    var controlsHeight by remember { mutableStateOf(0.dp) }


   LaunchedEffect(addPlaylistViewModel) {
        Log.d("SNACK_DEBUG", "Collector launched for AddPlaylistViewModel")
        addPlaylistViewModel.uiEvent.collect { event ->
            when (event) {
                is AddPlaylistEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        mainMusicViewModel.uiEvent.collect { event ->
            when (event) {
                is NewPlayerEvent.ShowMessage -> {
                        snackBarHostState.showSnackbar(message = event.message)
                }
                is NewPlayerEvent.NavigateToBack ->{
                    appTopBackStack.removeLastOrNull()
                }
            }
        }
    }


    NavDisplay(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        backStack = appTopBackStack,
        onBack = { appTopBackStack.removeLastOrNull() },
        entryProvider = entryProvider{

            entry <LoginScreen>{
                Log.d(TAG,"Login Screen")
                LoginScreen(
                    onLoginSuccess = {
                        appTopBackStack.clear()
                        appTopBackStack.add(
                            MainGraph)
                    }
                )
            }
            entry<MainGraph> {
                Log.d(TAG,"Main Graph initiated")
                MainScreenWithBottomBar(
                    appTopBackStack = appTopBackStack,
                    mainViewModel = mainMusicViewModel,
                    addPlaylistViewModel = addPlaylistViewModel,
                    onAddToPlaylistClick = onAddToPlaylistClick,
                    snackbarHostState = snackBarHostState
                )
            }
            entry<PlayerScreen> {
                AnimatedContent(
                    targetState = it,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) {_ ->
                    val state by mainMusicViewModel.uiState.collectAsStateWithLifecycle()
                    PlayerScreenN(
                        state = state,
                        onAction = mainMusicViewModel::onAction,
                        navigateToBack = { appTopBackStack.removeLastOrNull() },
                        onAddToPlaylistClick = {track ->
                            Log.d(TAG,"onAddToPlaylistClick")
                            onAddToPlaylistClick(track)
                        },
                        snackbarHostState = snackBarHostState
                    )
                }
            }
        }
    )

    if (addPlaylistState.isCreateDialogOpen){
        CreateNewPlaylistDialog(
            onDismiss = {
                addPlaylistViewModel.onAction(AddPlaylistAction.HideCreateDialog)
            },
            onConfirm = { title ,description ->
                addPlaylistViewModel.onAction(AddPlaylistAction.CreatePlaylistAndAddSong(title = title, description = description))
                addPlaylistViewModel.onAction(AddPlaylistAction.HideCreateDialog)
            }
        )
    }

    if (showPlaylistSheet){
        AddPlaylistSheet(
            state = addPlaylistState,
            onDismiss = {showPlaylistSheet = false},
            onPlaylistClick = {playlistId ->
                Log.d(TAG, "Step 2A: Existing playlist item clicked.")
                showPlaylistSheet = false
                addPlaylistViewModel.onAction(AddPlaylistAction.AddSongToPlaylist(playlistId))
            },
            addNewPlaylistClick = {
                Log.d(TAG, "Step 2B: 'New Playlist' button clicked.")
                addPlaylistViewModel.onAction(AddPlaylistAction.ShowCreateDialog)
            },
            deletePlaylist = {playlist ->
                Log.d(TAG, "Step 2C: Delete Playlist button clicked. ${playlist.name}")
                addPlaylistViewModel.onAction(AddPlaylistAction.ShowDeleteDialog(playlist))
            },
            contentPadding = controlsHeight,
            viewModel = addPlaylistViewModel
        )
    }
    addPlaylistState.playlistToDelete?.let { playlist ->
        DeleteAlertDialog(
            onDismiss = {
                addPlaylistViewModel.onAction(AddPlaylistAction.HideDeleteDialog)
            },
            onConfirm = {
                addPlaylistViewModel.onAction(AddPlaylistAction.ConfirmDelete)
            },
            playlist = playlist,

        )
    }
}