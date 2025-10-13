package com.zyra.music.zyra.presentation.playlistScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PlaylistScreen(
    viewModel : PlaylistViewModel,
    onBack : () -> Unit
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when{
            state.isLoading -> CircularProgressIndicator()
            state.playlistDetails != null -> Text(text = "Loaded ${state.playlistDetails?.title}")
            else -> Text("No Playlist found ${state.error}")
        }
    }

}