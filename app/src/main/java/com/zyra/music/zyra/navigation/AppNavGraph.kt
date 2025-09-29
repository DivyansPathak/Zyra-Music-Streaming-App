package com.zyra.music.zyra.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel

@UnstableApi
@Composable
fun AppNavGraph(
    modifier : Modifier = Modifier,
    navController : NavHostController,
    startDestination : Route,
    onSongClick : (TrackFullOne) -> Unit,
    mainMusicViewModel: MainMusicViewModel
) {
   NavHost(
       navController = navController,
       startDestination = startDestination,
       enterTransition = {fadeIn()},
       exitTransition = {fadeOut()}
   ) {
       authGraph(navController)
       mainGraph(navController,onSongClick,mainMusicViewModel)
       featureGraphs(navController,mainMusicViewModel)
   }
}