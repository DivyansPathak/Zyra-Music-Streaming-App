package com.zyra.music.zyra.navigation

import androidx.annotation.OptIn
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.home.HomeScreen
import com.zyra.music.zyra.presentation.home.HomeViewModel
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.searchScreen.SearchScreen
import com.zyra.music.zyra.presentation.searchScreen.SearchScreenN
import com.zyra.music.zyra.presentation.searchScreen.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(UnstableApi::class)
fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    onSongClick: (TrackFullOne) -> Unit,
    mainMusicViewModel: MainMusicViewModel
){

    navigation<Route.MainGraph>(
        startDestination = Route.HomeScreen
    ){
        composable<Route.HomeScreen>{backStackEntry ->
            val parentEntry = remember (backStackEntry){
                navController.getBackStackEntry<Route.MainGraph>()
            }
            val viewModel : HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                navController = navController,
                state = state,
                onPlaylistClick = {},
                onRefresh = viewModel::refresh
            )
        }
        composable<Route.SearchScreenN>{backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Route.MainGraph>() }

            val viewModel : SearchViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            SearchScreenN(
                state = state,
                onAction = viewModel::onAction,
                navController = navController,
                onSongClick = onSongClick,
                onNextPlayClick = {track -> mainMusicViewModel.addSongToPlayNext(track)},
                addToQueueClick = {track -> mainMusicViewModel.addSongToQueue(track)}
            )
        }
        composable<Route.ProfileScreen> {  }
        composable<Route.PlayListScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.PlayListScreen>()
        }
    }
}