package com.zyra.music.zyra.presentation.libraryScreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zyra.music.zyra.navigation.PlayListType
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreenTest(
viewModel: LibraryViewModelNew = koinViewModel()
) {
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Button(
            modifier = Modifier.size(200.dp),
            onClick = {
                scope.launch {
                    Log.d("DEBUG_TEST","Button clicked, fetching playlist details")
                    val result = viewModel.loadPlaylistSong()
                    Log.d("DEBUG_TEST","Result : $result")
                }
            }) {
            Text(text = "Test Playlist details")
        }
    }

}