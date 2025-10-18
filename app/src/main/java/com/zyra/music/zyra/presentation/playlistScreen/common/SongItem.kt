package com.zyra.music.zyra.presentation.playlistScreen.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.utils.formatDurationLong

@Composable
fun SongListItem(
    track: TrackFullOne,
    isPlaying : Boolean,
    trailingContent : @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = track.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )
        },
        supportingContent = {
            Text(
                text = formatDurationLong(track.duration),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Image(
                painter = rememberAsyncImagePainter(model = track.thumbnail),
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        },
        trailingContent = {
            trailingContent()
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}