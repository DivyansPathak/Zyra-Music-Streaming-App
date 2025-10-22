package com.zyra.music.zyra.presentation.profileScreen.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.presentation.profileScreen.ProfileAction
import com.zyra.music.zyra.presentation.profileScreen.ProfileState

@Composable
fun NameChangeDialog(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
) {
    var tempName by rememberSaveable(state.name) { mutableStateOf(state.name) }

    AlertDialog(
        onDismissRequest = {
            if (!state.isLoading) {
                onAction(ProfileAction.HideNameChangeDialog)
            }
        },
        title = { Text(text = "Change Your Name") },
        text = {
            Column {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    readOnly = state.isLoading
                )
                if (state.isLoading) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAction(ProfileAction.OnNameChanged(tempName)) },
                enabled = !state.isLoading && tempName.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(
                onClick = { onAction(ProfileAction.HideNameChangeDialog) },
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }
        }

    )

}