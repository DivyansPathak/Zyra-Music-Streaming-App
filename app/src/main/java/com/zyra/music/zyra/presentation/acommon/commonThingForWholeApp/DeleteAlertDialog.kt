package com.zyra.music.zyra.presentation.acommon.commonThingForWholeApp

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.zyra.music.zyra.domain.model.LibraryPlaylist

@Composable
fun DeleteAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    playlist: LibraryPlaylist,
    modifier: Modifier = Modifier,
) {

    val title = playlist.name
    val highlightedTextColor = MaterialTheme.colorScheme.error

    val annotatedText = buildAnnotatedString {
        append("Are you sure you want to delete ")
        withStyle(
            style = SpanStyle(
                color = highlightedTextColor,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
        ){
            append(title)
        }
        append(" playlist? This action cannot be undone")
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) { Text(text = "Delete", color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))}
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Delete Playlist", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = modifier
    )
}