package com.zyra.music.zyra.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@Composable
fun PlaylistItemCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    subtitle: String,
    onItemClick: () -> Unit
) {

    Column(modifier = modifier.clickable { onItemClick() }) {
        PlaylistItemCardImage(
            imageUrl = imageUrl,
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = title, style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,

            )
    }

}

@Composable
fun PlaylistItemCardImage(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    val context = LocalContext.current
    val imageRequest = ImageRequest
        .Builder(context)
        .data(imageUrl)
        .crossfade(true)
        .build()

    AsyncImage(
        model = imageRequest,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.preview_pager),
        error = painterResource(id = R.drawable.preview_pager)
    )
}

@Preview
@Composable
private fun PlaylistItemCardPreview() {
    ZyraTheme {

    PlaylistItemCard(
        imageUrl = "https://example.com/image.jpg",
        title = "My Awesome Playlist",
        subtitle = "A collection of great songs",
        onItemClick = {}
    )
    }
}

