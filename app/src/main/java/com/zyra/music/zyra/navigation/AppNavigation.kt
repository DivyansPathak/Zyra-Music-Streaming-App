package com.zyra.music.zyra.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.zyra.music.zyra.data.remote.SupabaseClient
import com.zyra.music.zyra.presentation.acommon.MainScreenWithBottomBar
import com.zyra.music.zyra.presentation.login.LoginScreen
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.newPlayer.PlayerScreenN
import io.github.jan.supabase.auth.Auth
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
                    if (startScreen ==null){
                        startScreen = MainGraph
                    }
                }
                is SessionStatus.NotAuthenticated -> {
                    Log.d(TAG,"NotAuthenticated")
                    if (startScreen ==null){
                        startScreen = LoginScreen
                    }
                }
                SessionStatus.Initializing -> {
                    Log.d(TAG,"Initializing")
                }
                is SessionStatus.RefreshFailure -> {
                    Log.d(TAG,"RefreshFailure")
                    if (startScreen ==null){
                        startScreen = LoginScreen
                    }
                }
            }
        }
    }


    if (startScreen == null){
        return
    }

    val appTopBackStack = rememberNavBackStack(startScreen!!)


    val mainMusicViewModel : MainMusicViewModel = koinViewModel()

    NavDisplay(
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
                    mainViewModel = mainMusicViewModel
                )
            }
            entry<PlayerScreen> {
                val state by mainMusicViewModel.uiState.collectAsStateWithLifecycle()
                PlayerScreenN(
                    state = state,
                    onAction = mainMusicViewModel::onAction,
                    eventFlow = mainMusicViewModel.uiEvent,
                    navigateToBack = { appTopBackStack.removeLastOrNull() }
                )
            }
        }
    )
}