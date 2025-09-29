package com.zyra.music.zyra.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zyra.music.zyra.presentation.login.LoginScreen


fun NavGraphBuilder.authGraph(navController: NavHostController){

    composable<Route.LoginScreen> {
        LoginScreen(navController = navController)
    }
    composable <Route.LoginCheckScreen>{
        LoginCheckScreen(navController = navController)
    }
}