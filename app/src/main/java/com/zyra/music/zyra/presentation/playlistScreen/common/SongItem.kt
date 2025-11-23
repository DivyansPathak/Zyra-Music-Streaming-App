package com.zyra.music.zyra.presentation.playlistScreen.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zyra.music.zyra.R
import com.zyra.music.zyra.data.utils.ERROR_IMAGE_URL_ONE
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.presentation.utils.formatDurationLong

@Composable
fun SongListItem(
    track: TrackFullOne,
    isPlaying: Boolean,
    trailingContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor =
        if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
    val image = ImageRequest.Builder(LocalContext.current)
        .data(track.thumbnail)
        .crossfade(true)
        .build()

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
//            AsyncImage(
//                model = image,
//                placeholder = painterResource(R.drawable.error_image),
//                error =painterResource( R.drawable.error_image),
//                contentDescription = track.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(MaterialTheme.shapes.small)
//            )
            SubcomposeAsyncImage(
                model = image,
                contentDescription = "playlist thumbnail",
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            ) {
                val state by painter.state.collectAsState()
                if (state is AsyncImagePainter.State.Error) {
                    AsyncImage(
                        model = ERROR_IMAGE_URL_ONE,
                        contentDescription = "error image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    SubcomposeAsyncImageContent()
                }
            }
        },
        trailingContent = {
            trailingContent()
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}