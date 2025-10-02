package com.zyra.music.zyra.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zyra.music.zyra.presentation.common.GradientScreenContainer
import com.zyra.music.zyra.presentation.home.HomeScreenState
import com.zyra.music.zyra.presentation.home.component.CardItems
import com.zyra.music.zyra.presentation.home.component.HomeTopBar
import com.zyra.music.zyra.presentation.home.component.ShimmerEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeScreenState,
    onPlaylistClick: (playlistId: String) -> Unit,
    onRefresh: suspend () -> Unit

) {

    var lazyListState = rememberLazyListState()

    val pullToRefreshState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()

    val gradientAlpha by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0) {
                0f
            } else {
                (1f - lazyListState.firstVisibleItemScrollOffset / 400f).coerceIn(0f, 1f)
            }
        }
    }
    val firstImageUrl = state.sections.firstOrNull()?.playLists?.firstOrNull()?.thumbnail
    GradientScreenContainer(
        modifier = modifier,
        imagerUrl = firstImageUrl,
        alphaValue = gradientAlpha
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize(),
            isRefreshing = state.isLoading,
            onRefresh = {
                scope.launch {
                    onRefresh()
                }
            }
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp, top = 80.dp)
            ) {
                if (state.isLoading && state.sections.isEmpty()) {
                    items(3) {
                        ShimmeringSectionPlaceholder()
                    }
                } else {
                    items(state.sections, key = { it.id }) { section ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(section.playLists, key = { it.id }) { playlists ->
                                    CardItems(
                                        modifier = Modifier
                                            .fillParentMaxWidth(0.4f)
                                            .aspectRatio(0.75f),
                                        playlists = playlists,
                                        onCardItemClick = { onPlaylistClick(playlists.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            HomeTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )

        }
    }

}

@Composable
private fun ShimmeringSectionPlaceholder(modifier: Modifier = Modifier) {
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        ShimmerEffect(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(0.6f)
                .height(24.dp)
                .clip(RoundedCornerShape(8.dp)),
            shimmerColor = shimmerColor
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(5) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillParentMaxWidth(0.4f)
                        .aspectRatio(0.75f)
                        .clip(RoundedCornerShape(8.dp)),
                    shimmerColor = shimmerColor
                )
            }
        }
    }
}