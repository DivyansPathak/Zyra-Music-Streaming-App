package com.zyra.music.zyra.presentation.common

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberDominantColorState(
    imageUrl: String,
    defaultColor: Color = MaterialTheme.colorScheme.surface
): State<Color> {

    // 1. Remember a mutable state, initialized with the default color.
    // The key ensures that if the imageUrl or defaultColor changes, the state resets.
    val dominantColor = remember(imageUrl, defaultColor) {
        mutableStateOf(defaultColor)
    }
    val context = LocalContext.current

    // 2. Launch a side-effect that runs whenever the imageUrl changes.
    LaunchedEffect(imageUrl) {
        if (imageUrl.isBlank()) {
            dominantColor.value = defaultColor
            return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .allowHardware(false) // Required for Palette
                    .build()

                val loader = ImageLoader(context)
                val result = loader.execute(request)

                if (result is SuccessResult) {
                    val drawable = result.image.asDrawable(context.resources)
                    val bitmap: Bitmap =
                        (drawable as? BitmapDrawable)?.bitmap ?: drawable.toBitmap()

                    val palette = Palette.from(bitmap).generate()
                    val colorInt = palette.vibrantSwatch?.rgb
                        ?: palette.dominantSwatch?.rgb
                        ?: palette.mutedSwatch?.rgb

                    // 3. Update the state's value with the new color if found.
                    dominantColor.value = colorInt?.let { Color(it) } ?: defaultColor
                } else {
                    dominantColor.value = defaultColor
                }
            } catch (e: Exception) {
                dominantColor.value = defaultColor
            }
        }
    }

    // 4. Return the state object. The caller can read its .value.
    return dominantColor
}

