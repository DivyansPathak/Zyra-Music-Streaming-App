package com.zyra.music.zyra.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zyra.music.zyra.R
import com.zyra.music.zyra.domain.model.PlayList
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@Composable
fun CardItems(
    modifier: Modifier = Modifier,
    playlists: PlayList,
    onCardItemClick: () -> Unit
) {

    val context = LocalContext.current
    val imageRequest = ImageRequest.Builder(context)
        .data(playlists.thumbnail)
        .crossfade(true)
        .build()

    Card(
        onClick = onCardItemClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(color = Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                model = imageRequest,
                contentDescription = "Playlist thumbnail",
                placeholder = painterResource(id = R.drawable.preview_pager),
                error = painterResource(id = R.drawable.preview_pager),
                contentScale = ContentScale.Crop
            )

            Text(
                text = playlists.title,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = playlists.subtitle,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,

            )
        }
    }

}

@Preview
@Composable
private fun PreviewCardItems() {

    ZyraTheme {
        val playlists = PlayList(
            id = "1",
            title = "Upbeat Marathi",
            subtitle = "Sanju Rathod, Andand Shinde, Vaishali Raj, Shreya S",
            thumbnail = "",
            songs = emptyList(),
            genre = ""
        )


        // Calculate the width for each card: 1/2.5 = 40% of the screen width.
        // This ensures 2 cards are fully visible and 1/2 of the third card is visible.
        val cardWidthFraction = 1f / 2.5f

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(5) { size ->

                // This is the key: fillParentMaxWidth and aspectRatio
                CardItems(
                    modifier = Modifier
                        // 1. Set the width as a fraction of the LazyRow's container width
                        .fillParentMaxWidth(cardWidthFraction)
                        // 2. Define the portrait shape: height = 2x width (1:2 aspect ratio)
                        .aspectRatio(0.5f),
                    playlists = playlists,
                    onCardItemClick = { /* Handle click */ }
                )
            }
        }

        CardItems(
            modifier = Modifier.aspectRatio(0.5f),
            playlists = playlists,
            onCardItemClick = {})

    }

}