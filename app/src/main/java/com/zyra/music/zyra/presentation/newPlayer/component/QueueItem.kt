package com.zyra.music.zyra.presentation.newPlayer.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.zyra.music.zyra.R
import com.zyra.music.zyra.domain.model.TrackFullOne

@Composable
fun QueueItem(
    song: TrackFullOne, // Use the actual SingleTrack model
    onClick: () -> Unit,
    isPlaying: Boolean,
    isCurrentlyPlaying: Boolean,
    onRemoveClick : () -> Unit,
    modifier: Modifier = Modifier
) {

    val color = if (isCurrentlyPlaying) Color.Green else MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onRemoveClick) {
            Icon(modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Clear,
                contentDescription = "Remove from queue",
                tint = MaterialTheme.colorScheme.primary)
        }
        // Thumbnail
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = song.thumbnail,
                contentDescription = "Song thumbnail for ${song.title}",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.preview_pager)
            )
        }

        // Title and Artist
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = color
            )
            Text(
                text = song.artistName.toString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isCurrentlyPlaying) {
            WaveformAnimation(
                isPlaying = isPlaying,
                modifier = Modifier.size(24.dp)
            )
        }


    }
}

@Composable
fun WaveformAnimation(
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    barColor: Color = MaterialTheme.colorScheme.primary,
    isPlaying: Boolean,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_transition")

    val animations = List(barCount) { i ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = if (isPlaying) 1f else 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 400 + i * 150),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$i"
        )
    }

    Row(
        modifier = modifier, // use the passed size
        verticalAlignment = Alignment.Bottom
    ) {
        animations.forEach { anim ->
            Box(
                modifier = Modifier
                    .weight(1f) // distribute bars evenly
                    .fillMaxHeight(anim.value)
                    .padding(horizontal = 1.dp)
                    .clip(RoundedCornerShape(50))
                    .background(barColor)
            )
        }
    }
}

