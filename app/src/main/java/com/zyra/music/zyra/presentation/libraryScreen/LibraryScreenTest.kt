package com.zyra.music.zyra.presentation.libraryScreen

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zyra.music.zyra.navigation.PlayListType
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreenTest(
viewModel: LibraryViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Button(
        modifier = Modifier.size(200.dp),
        onClick = {
        scope.launch {
            Log.d("DEBUG_TEST","Button clicked, fetching playlist details")

            val testPlaylistId = "93aad41d-a2bb-4ebb-8470-7354391965c1"
            val testPlaylistType = PlayListType.PRESET

            val result = viewModel.getPlaylistDetails(testPlaylistId,testPlaylistType)

            Log.d("DEBUG_TEST","Result : $result")
        }
    }) {
        Text(text = "Test Playlist details")
    }

}