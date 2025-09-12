package com.zyra.music.zyra.presentation.playerScreen.miniPlayer

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun MiniPlayer(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    isPlaying: Boolean,
    isFavorite: Boolean,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    favoriteIconClick: () -> Unit,
    onClick: () -> Unit,
    duration: String,
    currentDuration : String
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
//            IconButton(onClick = onCancelIconClick) {
//                Icon(
//                    imageVector = Icons.Default.Close, contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
                MiniPlayerImage(
                    imageUrl = imageUrl,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title, style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee(
                            initialDelayMillis = 5000,
                            iterations = Int.MAX_VALUE,
                            velocity = 10.dp
                        )
                    )
                    Text(
                        text = "$currentDuration / $duration", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = favoriteIconClick) {
                    Crossfade(targetState = isFavorite, label = duration) { favorite ->

                        Icon(
                            painter = if (favorite) painterResource(id = R.drawable.heart_filled)
                            else painterResource(
                                id = R.drawable.heart_outlined
                            ),
                            contentDescription = null,
                            tint = if (favorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onPlayPauseClick) {
                    Crossfade(targetState = isPlaying, label = duration) { playing ->

                        Icon(
                            painter = if (playing) painterResource(id = R.drawable.pause)
                            else painterResource(
                                id = R.drawable.play
                            ),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }


            }

            MiniProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}

@Composable
fun MiniProgressBar(
    modifier: Modifier = Modifier,
    progress: Float
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier.height(2.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun MiniPlayerImage(
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
        modifier = modifier.clip(CircleShape),
        contentDescription = null,
        placeholder = painterResource(id = R.drawable.placeholder_miniplayer),
        error = painterResource(id = R.drawable.placeholder_miniplayer),
        contentScale = ContentScale.FillBounds


    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewMiniPlayer() {
    ZyraTheme {
        MiniPlayer(
            imageUrl = "", // you can put a test image URL here
            title = "Sample Song Title That Might Be Long",
            isPlaying = true,
            isFavorite = false,
            progress = 0.5f, // 50% progress
            onPlayPauseClick = {},
            favoriteIconClick = {},
            onClick = {},
            duration = "3:45",
            currentDuration = ""
        )
    }
}

