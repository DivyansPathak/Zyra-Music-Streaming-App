package com.zyra.music.zyra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zyra.music.zyra.navigation.Route
import com.zyra.music.zyra.presentation.common.AppScreen
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@androidx.media3.common.util.UnstableApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = Route.LoginCheckScreen
        setContent {
            ZyraTheme {
//                MainScreen()
                AppScreen(startDestination = startDestination)
            }
        }
    }
}
