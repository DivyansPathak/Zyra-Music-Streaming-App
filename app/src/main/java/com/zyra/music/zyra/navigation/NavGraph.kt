package com.zyra.music.zyra.navigation
//
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.navigation
//import com.zyra.music.zyra.domain.model.TrackFullOne
//import com.zyra.music.zyra.presentation.home.HomeScreen
//import com.zyra.music.zyra.presentation.home.HomeViewModel
//import com.zyra.music.zyra.presentation.login.LoginScreen
//import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
//import com.zyra.music.zyra.presentation.newPlayer.PlayerScreenN
//import com.zyra.music.zyra.presentation.playlistDetail.PlaylistDetailViewModel
//import com.zyra.music.zyra.presentation.searchScreen.SearchScreenN
//import com.zyra.music.zyra.presentation.searchScreen.SearchViewModel
//import org.koin.androidx.compose.koinViewModel
//
//@androidx.media3.common.util.UnstableApi
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NavGraph(
//    navController: NavHostController,
//    onSongClick: (TrackFullOne) -> Unit,
//    mainMusicViewModel: MainMusicViewModel
//) {
//
//    NavHost(
//        navController = navController,
//        startDestination = Route.LoginCheckScreen.title,
//        enterTransition = { fadeIn() },
//        exitTransition = { fadeOut() },
//    ) {
//        composable(Route.LoginCheckScreen.title) {
//            LoginCheckScreen(navController = navController)
//        }
//        composable(Route.LoginScreen.title) {
//            LoginScreen(navController = navController)
//        }
//
//        navigation(
//            startDestination = Route.HomeScreen.title,
//            route = Route.MainGraph.title
//        ) {
//            composable(Route.HomeScreen.title) { backStackEntry ->
//
//                val parentEntry = remember(backStackEntry) {
//                    navController.getBackStackEntry(Route.MainGraph.title)
//                }
//
//                val vieModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
//                val state by vieModel.uiState.collectAsStateWithLifecycle()
//                HomeScreen(
//                    navController = navController,
//                    state = state,
//                    onPlaylistClick = {},
//                    onRefresh = vieModel::refresh
//
//                )
//            }
//
//            composable(Route.SearchScreen.title) { backStackEntry ->
//
//                val parentEntry = remember(backStackEntry) {
//                    navController.getBackStackEntry(Route.MainGraph.title)
//                }
//
//                val viewModel: SearchViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
//                val state by viewModel.uiState.collectAsStateWithLifecycle()
//
//                SearchScreenN(
//                    state = state,
//                    onAction = viewModel::onAction,
//                    navController = navController,
//                    onSongClick = onSongClick
//                    , onNextPlayClick = { track ->
//                        mainMusicViewModel.addSongToPlayNext(track)
//                    },
//                    addToQueueClick = { track ->
//                        mainMusicViewModel.addSongToQueue(track)
//                    }
//                )
//            }
//            composable(Route.ProfileScreen.title) {
////            ProfileScreen()
//            }
//            composable<Route.PlayListScreen> {
////            PlayListScreen()
//            }
//        }
//        composable(Route.PlayerScreen.title) { backStackEntry ->
//            val state by mainMusicViewModel.uiState.collectAsStateWithLifecycle()
//            PlayerScreenN(
//                state = state,
//                onAction = mainMusicViewModel::onAction,
//                eventFlow = mainMusicViewModel.uiEvent,
//                navigateToBack = {
//                    navController.popBackStack()
//                }
//            )
//
//        }
//        composable<Route.PlaylistDetailScreen>{
//            val viewModel : PlaylistDetailViewModel = koinViewModel()
//            val state by viewModel.uiState.collectAsStateWithLifecycle(
//
//            )
//        }
//
//    }
//}