package com.zyra.music.zyra.presentation.searchScreen.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment.Companion.Rectangle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zyra.music.zyra.R
import com.zyra.music.zyra.data.utils.ERROR_IMAGE_URL_ONE
import com.zyra.music.zyra.domain.model.TrackFullOne

@Composable
fun SongCardHorizontal(
    track: TrackFullOne,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit
) {
    val image = ImageRequest.Builder(LocalContext.current)
        .data(track.thumbnail)
        .crossfade(true)
        .build()
    Column(
        modifier = Modifier
            .width(140.dp)
            .padding(end = 12.dp)
    ) {

        Box {
            SubcomposeAsyncImage(
                model = image,
                contentDescription = "playlist thumbnail",
                modifier = Modifier
                    .size(140.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onClick() },
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

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.38f))
            ) {
                trailingContent()
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = track.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = track.artistName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}