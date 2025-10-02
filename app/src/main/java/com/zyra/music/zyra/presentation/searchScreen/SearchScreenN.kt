package com.zyra.music.zyra.presentation.searchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zyra.music.zyra.R
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.searchScreen.component.SearchTopBar
import com.zyra.music.zyra.presentation.searchScreen.component.ShimmerEffectSearch
import com.zyra.music.zyra.presentation.utils.formatDurationLong

@Composable
fun SearchScreenN(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
    onBackClick: () -> Unit,
    onSongClick: (TrackFullOne) -> Unit,
    onNextPlayClick: (TrackFullOne) -> Unit,
    addToQueueClick: (TrackFullOne) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchTopBar(
            query = state.query,
            onQueryChange = { newQuery -> onAction(SearchAction.OnQueryChange(newQuery)) },
            onTrailingIconClick = { onAction(SearchAction.OnClearQuery) },
            onBackClick = {onBackClick() },
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
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.searchResultsFromYT) { song ->
                        SongRow(
                            track = song,
                            onSongClick = { onSongClick(song) },
                            onNextPlayClick = { onNextPlayClick(song) },
                            addToQueueClick = { addToQueueClick(song) }
                        )
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

@Composable
fun SongRow(
    track: TrackFullOne,
    onSongClick: () -> Unit,
    onNextPlayClick: () -> Unit,
    addToQueueClick: () -> Unit
) {

    val context = LocalContext.current
    val imageRequest = ImageRequest.Builder(context)
        .data(track.thumbnail)
        .crossfade(true)
        .build()

    // 1. Add state to control if the dropdown menu is open or closed.
    var isMenuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSongClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageRequest,
                placeholder = painterResource(id = R.drawable.preview_pager),
                error = painterResource(id = R.drawable.preview_pager),
                contentDescription = "track thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(12.dp)
                    .shadow(shape = RectangleShape, elevation = 4.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(
                        iterations = Int.MAX_VALUE,
                        repeatDelayMillis = 0,
                        initialDelayMillis = 3000,
                        velocity = 10.dp,

                        )
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = track.artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = " | ${formatDurationLong(track.duration)}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                    )
                }
            }
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // 3. Add the DropdownMenu itself.
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Play next") },
                        onClick = {
                            onNextPlayClick()
                            isMenuExpanded = false // Close the menu
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to queue") },
                        onClick = {
                            addToQueueClick()
                            isMenuExpanded = false // Close the menu
                        }
                    )
                }

            }
        }

    }
}

