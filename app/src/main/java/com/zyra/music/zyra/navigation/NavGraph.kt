package com.zyra.music.zyra.navigation


import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.home.HomeScreen
import com.zyra.music.zyra.presentation.home.HomeViewModel
import com.zyra.music.zyra.presentation.login.LoginScreen
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.newPlayer.PlayerScreenN
import com.zyra.music.zyra.presentation.playerScreen.MusicViewModel
import com.zyra.music.zyra.presentation.searchScreen.SearchScreenN
import com.zyra.music.zyra.presentation.searchScreen.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@androidx.media3.common.util.UnstableApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    musicViewModel: MusicViewModel,
    navController: NavHostController,
//    onSongClick : (SingleTrack) -> Unit,
    onSongClick : (TrackFullOne) -> Unit,
    mainMusicViewModel: MainMusicViewModel
    ) {

    NavHost(
        navController = navController,
        startDestination = Route.LoginCheckScreen,
        enterTransition = {fadeIn()},
        exitTransition = {fadeOut()},
    ) {
        composable<Route.LoginCheckScreen>{
           LoginCheckScreen(navController = navController)
        }
        composable<Route.LoginScreen>{
            LoginScreen(navController = navController)
        }
        composable<Route.HomeScreen> {
            val vieModel : HomeViewModel = koinViewModel()
            val state by vieModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                navController = navController,
                state = state,
                onPlaylistClick = {}
            )
        }
        composable<Route.PlayerScreen>{ backStackEntry->
//            val state by musicViewModel.uiState.collectAsStateWithLifecycle()
//            PlayerScreen(
//                state = state,
//                onAction = musicViewModel::onAction,
//                eventFlow = musicViewModel.uiEvent,
//                navigateToBack = {
//                    navController.popBackStack()
//                }
//            )
            val state by mainMusicViewModel.uiState.collectAsStateWithLifecycle()
            PlayerScreenN(
                state = state,
                onAction = mainMusicViewModel::onAction,
                eventFlow = mainMusicViewModel.uiEvent,
                navigateToBack = {
                    navController.popBackStack()
                }
            )

        }
        composable<Route.SearchScreen>{
            val viewModel : SearchViewModel = koinViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            SearchScreenN(
                state = state,
                onAction = viewModel::onAction,
                navController = navController,
                onSongClick = onSongClick
//                onSongClick = { clickedTrack->,
////                    musicViewModel.playSongAddCreateQueue(clickedTrack = clickedTrack)
////                    val trackJson = Json.encodeToString(SingleTrack.serializer(),clickedTrack)
////                    navController.navigate(Route.PlayerScreen(jsonTrack = trackJson))
//
//                }
                ,onNextPlayClick = {track ->
//                    mainMusicViewModel
                    mainMusicViewModel.addSongToPlayNext(track)
                                  },
                addToQueueClick = {track ->
//                    musicViewModel.addSongToEndOfQueue(track)
                    mainMusicViewModel.addSongToQueue(track)
                }
            )
        }
        composable<Route.ProfileScreen> {
//            ProfileScreen()
        }
        composable<Route.PlayListScreen> {
//            PlayListScreen()
        }

        }
    }