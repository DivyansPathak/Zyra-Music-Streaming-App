package com.zyra.music.zyra.presentation.libraryScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.navigation.PlayListType
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onPlaylistClick: (String, PlayListType) -> Unit,
    state: LibraryState,
    onScreenTypeSelected: (LibraryScreenType) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    viewModel.refreshLibraryContent()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
   Box(modifier = Modifier.fillMaxSize(),
       contentAlignment = Alignment.Center){

       PullToRefreshBox(
           isRefreshing = state.isLoading,
           onRefresh = {
               scope.launch {
                   viewModel.refreshLibraryContent()
               }
           }
       ) {
           Column(modifier = Modifier.fillMaxSize()) {
               Text("Library Screen")
               if (state.isLoading && state.playlists.isEmpty()){
                   Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                       CircularProgressIndicator()
                   }
               }else{
                   LibraryContent(
                       playlists = state.playlists,
                       onPlaylistClick = {  playlist ->
                           onPlaylistClick(playlist.id.toString(),playlist.playlistType)
                       }
                   )
               }
           }
       }

   }

}

@Composable
private fun LibraryContent(
    playlists: List<LibraryPlaylist>,
    onPlaylistClick: (LibraryPlaylist) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items = playlists, key = { it.id }) { playlist ->
            PlaylistItem(playlist = playlist, onClick = { onPlaylistClick(playlist) })
        }
    }
}

@Composable
private fun PlaylistItem(playlist: LibraryPlaylist, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.imageUrl,
            contentDescription = playlist.name,
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${playlist.creator} • ${playlist.trackCount} tracks",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}


@Preview
@Composable
private fun PreviewLibraryScreen() {
    ZyraTheme {
        // The preview now holds and manages the state
        var state by remember { mutableStateOf(LibraryState()) }

        LibraryScreen(
            onPlaylistClick = {playlist, playlistType  ->

            },
            state = state,
            // --- FIX 5: Uncomment this block to make the preview interactive ---
            onScreenTypeSelected = { newType ->
                // When a new type is selected, update the state for the preview
                state = state.copy(selectedScreen = newType)
            }
        )
    }
}