package com.zyra.music.zyra.presentation.searchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.common.commonThingForWholeApp.MenuItems
import com.zyra.music.zyra.presentation.newPlayer.NewPlayerState
import com.zyra.music.zyra.presentation.searchScreen.component.PlaylistCardHorizontal
import com.zyra.music.zyra.presentation.searchScreen.component.SearchTopBar
import com.zyra.music.zyra.presentation.searchScreen.component.ShimmerEffectSearch
import com.zyra.music.zyra.presentation.searchScreen.component.SongCardHorizontal
import com.zyra.music.zyra.presentation.searchScreen.component.SongListItemForSearch
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchScreenN(
    state: SearchState,
    mainState: NewPlayerState,
    onAction: (SearchAction) -> Unit,
    onBackClick: () -> Unit,
    onSongClick: (TrackFullOne) -> Unit,
    onNextPlayClick: (TrackFullOne) -> Unit,
    addToQueueClick: (TrackFullOne) -> Unit,
    addToPlaylistClick: (TrackFullOne) -> Unit,
    addToFavoriteClick: (TrackFullOne) -> Unit,
    onPlaylistClick : (String) -> Unit,
    eventFlow: Flow<SearchEvent>,
    contentPadding: Dp = 0.dp
) {

    val controller = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is SearchEvent.HideKeyboard -> {
                    controller?.hide()
                }

                is SearchEvent.NavigateToBack -> {
                    onBackClick()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchTopBar(
            query = state.query,
            onQueryChange = { newQuery -> onAction(SearchAction.OnQueryChange(newQuery)) },
            onTrailingIconClick = { onAction(SearchAction.OnClearQuery); controller?.show() },
            onBackClick = { onBackClick() },
            onImeSearchClick = { newQuery -> onAction(SearchAction.OnImeSearchClick(newQuery)) }
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(24.dp))
        when {

            state.isLoading -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(count = 12) {
                        ShimmerEffectSearch(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }


            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                }
            }


            state.searchSuggestions.isNotEmpty() -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.searchSuggestions) { suggestion ->
                        SuggestionRow(
                            suggestion = suggestion,
                            onSuggestionClick = {
                                onAction(SearchAction.OnSuggestionClick(suggestion))
                            }
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = contentPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val primaryList = state.searchResultsFromYT
                    val firstTwoResult = primaryList.take(2)
                    val remainingResult = primaryList.drop(2)
                    items(firstTwoResult) { song ->
                        val isTrackFavorite = mainState.favoriteIds.contains(song.videoId)
                        SongListItemForSearch(
                            track = song,
                            modifier = Modifier.clickable {
                                onSongClick(song)
                            },
                            isPlaying = false,
                            trailingContent = {
                                MenuItems(
                                    isFavorite = isTrackFavorite,
                                    onPlayAsRadioClick = { onSongClick(song) },
                                    onAddToNextPlay = { onNextPlayClick(song) },
                                    onAddToQueue = { addToQueueClick(song) },
                                    onAddToPlaylist = { addToPlaylistClick(song) },
                                    onToggleFavorite = { addToFavoriteClick(song) }
                                )
                            }
                        )
                    }

                    if (state.searchResultsFromYoutube.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                Text(
                                    text = "More Results", // or "From YouTube"
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )

                                // Horizontal Scroll Container
                                LazyRow(
                                    contentPadding = PaddingValues(start = 16.dp, end = 56.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(state.searchResultsFromYoutube) { song ->
                                        SongCardHorizontal(
                                            track = song,
                                            onClick = { onSongClick(song) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    items(remainingResult) { song ->
                        val isTrackFavorite = mainState.favoriteIds.contains(song.videoId)
                        SongListItemForSearch(
                            track = song,
                            modifier = Modifier.clickable { onSongClick(song) },
                            isPlaying = false,
                            trailingContent = {
                                MenuItems(
                                    isFavorite = isTrackFavorite,
                                    onPlayAsRadioClick = { onSongClick(song) },
                                    onAddToNextPlay = { onNextPlayClick(song) },
                                    onAddToQueue = { addToQueueClick(song) },
                                    onAddToPlaylist = { addToPlaylistClick(song) },
                                    onToggleFavorite = { addToFavoriteClick(song) }
                                )
                            }
                        )
                    }
                    if (state.playlistFromYoutube.isNotEmpty()){
                        item {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                Text(
                                    text = "Playlists",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )

                                // Horizontal Scroll Container
                                LazyRow(
                                    contentPadding = PaddingValues(start = 16.dp, end = 56.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(state.playlistFromYoutube) { playlist ->
                                        PlaylistCardHorizontal(
                                            playlistYt = playlist,
                                            onClick = {playlistId ->
                                                    onPlaylistClick(playlistId)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }


}


@Composable
fun SuggestionRow(
    modifier: Modifier = Modifier,
    suggestion: String,
    onSuggestionClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSuggestionClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = suggestion,
            style = MaterialTheme.typography.bodyLarge
        )
    }

}


