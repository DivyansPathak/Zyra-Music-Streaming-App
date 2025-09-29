package com.zyra.music.zyra.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zyra.music.zyra.R
import com.zyra.music.zyra.data.remote.SupabaseClient
import com.zyra.music.zyra.presentation.playerScreen.UpNext
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay

@Composable
fun LoginCheckScreen(
    navController: NavHostController
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.hand_loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        restartOnPlay = false,
        iterations = 1,
    )


    LaunchedEffect(Unit) {
        delay(1500)
        SupabaseClient.supabase.auth.sessionStatus.collect { status ->
            when(status){
                is SessionStatus.Authenticated -> {
                    Log.d("LoginCheck", "User is Authenticated. Navigating to SearchScreen.")
                    navController.navigate(Route.HomeScreen){
                        popUpTo(Route.LoginCheckScreen){inclusive = true}
                    }
                }
                is SessionStatus.NotAuthenticated ->{
                    navController.navigate(Route.LoginScreen){
                        popUpTo(Route.LoginCheckScreen){inclusive = true}
                    }
                }
                else -> Unit
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}