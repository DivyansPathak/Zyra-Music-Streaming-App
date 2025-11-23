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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.zyra.music.zyra.domain.model.playlistData.PlaylistYT
import com.zyra.music.zyra.presentation.common.rememberDominantColorState

@Composable
fun PlaylistCardHorizontal(
    playlistYt: PlaylistYT,
    onClick: (String) -> Unit
) {
    val firstImageUrl = playlistYt.thumbnail
    val image = ImageRequest.Builder(LocalContext.current)
        .data(playlistYt.thumbnail)
        .crossfade(true)
        .build()
    val dominantColorState = rememberDominantColorState(
        imageUrl = firstImageUrl
    )
    val dominantColor = dominantColorState.value
    Card(
        modifier = Modifier
            .width(350.dp)
            .height(200.dp)
            .padding(end = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
        ),
        onClick = { onClick(playlistYt.playlistId) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = firstImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .blur(24.dp)
                    .alpha(0.9f)
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                dominantColor.copy(alpha = 0.55f),
                                dominantColor.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                AsyncImage(
                    model = image,
                    contentDescription = playlistYt.title,
                    placeholder = painterResource(R.drawable.preview_pager),
                    error = painterResource(R.drawable.error_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = playlistYt.title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Artist
                    Text(
                        text = playlistYt.author ?: "Unknown",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Absolute.SpaceAround
                    ) {
                        Button(
                            onClick = { onClick(playlistYt.playlistId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = ButtonDefaults.ContentPadding
                        ) {
                            Row {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "play",
                                )
                                Text(text = "Play")
                            }
                        }

                    }
                }

            }
        }

    }
}