package com.zyra.music.zyra.presentation.acommon.addToPlaylist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.NoOpUpdate
import coil3.compose.AsyncImage
import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.presentation.addPlaylist.AddPlaylistState

@Composable
fun AddPlaylistSheet(
    state: AddPlaylistState,
    onDismiss: () -> Unit = {},
    onPlaylistClick: (Long) -> Unit = {},
    addNewPlaylistClick: () -> Unit = {},
    deletePlaylist: (LibraryPlaylist) -> Unit = {},
    contentPadding: Dp = 0.dp
) {

    AddPlaylistSheetContent(
        state = state,
        onDismiss = onDismiss,
        onPlaylistClick = onPlaylistClick,
        addNewPlaylistClick = addNewPlaylistClick,
        deletePlaylist = deletePlaylist,
        contentPadding = contentPadding
    )
}

@Composable
fun AddPlaylistSheetContent(
    modifier: Modifier = Modifier,
    state: AddPlaylistState,
    onDismiss: () -> Unit = {},
    onPlaylistClick: (Long) -> Unit = {},
    addNewPlaylistClick: () -> Unit = {},
    deletePlaylist: (LibraryPlaylist) -> Unit = {},
    contentPadding: Dp = 0.dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 12.dp)
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(16.dp)
                .clickable(false) {}
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Save song to playlist",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "All Playlists",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(bottom = contentPadding + 88.dp)
                    ) {
                        items(state.playlists) { playlist ->
                            PlaylistItem(
                                playlist = playlist,
                                onClick = {
                                    onPlaylistClick(playlist.id)
                                },
                                deletePlaylist = {
                                    deletePlaylist(playlist)
                                }
                            )
                        }
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = { addNewPlaylistClick() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = contentPadding + 16.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Playlist Icon"
                    )
                    Text(text = "New Playlist", style = MaterialTheme.typography.titleSmall)
                }

//                Button(
//                    onClick = {addNewPlaylistClick()},
//                    modifier = Modifier
//                        .align(Alignment.BottomEnd)
//                        .clip(RoundedCornerShape(24.dp)),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary,
//                        contentColor = MaterialTheme.colorScheme.onPrimary
//                    )
//                ) {
//                    Row(
//                        modifier = Modifier,
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Absolute.SpaceEvenly
//                    ) {
//                        Icon(Icons.Default.Add, contentDescription = "Add Playlist Icon")
//                        Text(text = "New Playlist")
//                    }
//                }
            }
        }
    }

}

@Composable
private fun PlaylistItem(
    playlist: LibraryPlaylist,
    onClick: () -> Unit,
    deletePlaylist: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { deletePlaylist() }
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        AsyncImage(
            model = playlist.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { deletePlaylist() }) {
            Icon(
                Icons.Default.Delete, contentDescription = "delete Playlist",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
