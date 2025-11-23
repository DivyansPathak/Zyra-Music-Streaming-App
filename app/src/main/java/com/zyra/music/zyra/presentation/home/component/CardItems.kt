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
import com.zyra.music.zyra.presentation.playlistScreen.PlayListType
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@Composable
fun CardItems(
    modifier: Modifier = Modifier,
    playlists: PlayList,
    onCardItemClick: (String, PlayListType) -> Unit
) {

    val context = LocalContext.current
    val imageRequest = ImageRequest.Builder(context)
        .data(playlists.thumbnail)
        .crossfade(true)
        .build()

    Card(
        onClick = { onCardItemClick(playlists.id,playlists.type) },
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