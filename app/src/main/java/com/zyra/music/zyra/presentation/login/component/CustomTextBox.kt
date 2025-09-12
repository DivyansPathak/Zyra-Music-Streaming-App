package com.zyra.music.zyra.presentation.login.component

import android.graphics.drawable.Icon
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CustomTextBox(
    modifier: Modifier = Modifier,
    value : String,
    label : String,
    placeholder : String,
    keyboardType : KeyboardType,
    imeAction: ImeAction,
    supportingText : String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange : (String) -> Unit,
    trailingIcon : @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(text = label) },
        placeholder = {Text(text = placeholder)},
        supportingText = {Text(text = supportingText.orEmpty())},
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            unfocusedLeadingIconColor = Color.White,
            focusedLeadingIconColor = Color.White,
            unfocusedTrailingIconColor = Color.White,
            focusedTrailingIconColor = Color.White,
            focusedSupportingTextColor = Color.White,
            unfocusedSupportingTextColor = Color.White,
            focusedLabelColor = Color.White,       // ✅ label when focused
            unfocusedLabelColor = Color.White,     // ✅ label when unfocused
            focusedPlaceholderColor = Color.White, // ✅ placeholder when focused
            unfocusedPlaceholderColor = Color.White // ✅ placeholder when unfocused
        )

    )
}