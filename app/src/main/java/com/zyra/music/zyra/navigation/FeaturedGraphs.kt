package com.zyra.music.zyra.navigation

import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.newPlayer.PlayerScreenN
import com.zyra.music.zyra.presentation.playerScreen.PlayerScreen

@OptIn(UnstableApi::class)
fun NavGraphBuilder.featureGraphs(
    navController : NavHostController,
    mainMusicViewModel: MainMusicViewModel
){
    composable<Route.PlayerScreen> {
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
}