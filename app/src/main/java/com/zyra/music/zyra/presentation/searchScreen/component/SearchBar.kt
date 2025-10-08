package com.zyra.music.zyra.presentation.searchScreen.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
//    query: String,
//    onQueryChange: (String) -> Unit,
    query : TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onTrailingIconClick: () -> Unit,
    onSearch : (String) -> Unit,
) {

    val controller = LocalSoftwareKeyboardController.current
    Box(modifier = modifier){
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = {
                onQueryChange(it)
            },
            placeholder = { Text(text = "आज क्या सुनना चाहते हो",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
            ) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                showKeyboardOnFocus = true,
                imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {onSearch(query.text) ; controller?.hide()}
            ),
            trailingIcon = {
                IconButton(onClick = onTrailingIconClick) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = "close",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
            ),
            singleLine = true,
        )
    }


}
