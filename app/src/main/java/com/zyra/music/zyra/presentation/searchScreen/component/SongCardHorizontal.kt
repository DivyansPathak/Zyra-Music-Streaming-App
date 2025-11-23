package com.zyra.music.zyra.presentation.searchScreen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zyra.music.zyra.R
import com.zyra.music.zyra.domain.model.TrackFullOne

@Composable
fun SongCardHorizontal(
    track: TrackFullOne,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp) // Fixed width for horizontal items
            .clickable { onClick() }
            .padding(end = 12.dp) // Spacing between items
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(track.thumbnail)
                .crossfade(true)
                .build(),
            contentDescription = track.title,
            placeholder = painterResource(R.drawable.preview_pager),
            error = painterResource(R.drawable.error_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Title
        Text(
            text = track.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        // Artist
        Text(
            text = track.artistName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}