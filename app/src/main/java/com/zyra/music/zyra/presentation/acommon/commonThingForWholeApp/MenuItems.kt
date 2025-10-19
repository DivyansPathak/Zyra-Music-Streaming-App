package com.zyra.music.zyra.presentation.acommon.commonThingForWholeApp

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.zyra.music.zyra.R

@Composable
fun MenuItems(
    isFavorite : Boolean,
    onAddToNextPlay: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onPlayAsRadioClick: () -> Unit,
    onToggleFavorite : () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { isMenuExpanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Start Radio") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_play_as_radio),
                        contentDescription = "play as radio",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = {
                    onPlayAsRadioClick()
                    isMenuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Play next") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_add_to_next),
                        contentDescription = "add to play next",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = {
                    onAddToNextPlay()
                    isMenuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Add to queue") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_add_to_queue),
                        contentDescription = "add to queue",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = {
                    onAddToQueue()
                    isMenuExpanded = false
                }
            )
                DropdownMenuItem(
                    text = {
                        Text(if (isFavorite) "Remove from favorites" else "Add to favorites")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onToggleFavorite()
                        isMenuExpanded = false
                    }
                )
            DropdownMenuItem(
                text = { Text("Add to playlist") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_add_to_playlist),
                        contentDescription = "add to playlist",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = {
                    onAddToPlaylist()
                    isMenuExpanded = false
                }
            )
        }

    }
}