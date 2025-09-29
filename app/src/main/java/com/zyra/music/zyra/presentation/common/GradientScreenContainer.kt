package com.zyra.music.zyra.presentation.common

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import androidx.core.graphics.drawable.toBitmap
import coil3.Bitmap
import coil3.asDrawable

private const val TAG = "GradientScreenContainer"

@Composable
fun GradientScreenContainer(
    modifier: Modifier = Modifier,
    imagerUrl: String?,
    alphaValue : Float = 0.6f,
    content: @Composable () -> Unit
) {

    val context = LocalContext.current
    val defaultBackgroundColor = MaterialTheme.colorScheme.background

    var dominantColor by remember { mutableStateOf<Color?>(null) }

    LaunchedEffect(imagerUrl) {
        if (imagerUrl == null) {
            dominantColor = null
            return@LaunchedEffect
        }
        val request = ImageRequest.Builder(context)
            .data(imagerUrl)
            .allowHardware(false)
            .build()

        val loader = ImageLoader.Builder(context).build()
        val result = loader.execute(request)

        if (result is SuccessResult) {
            try {
                // Preferred: convert to Drawable then use core-ktx toBitmap()
                val drawable = result.image.asDrawable(context.resources)
                val bitmap: Bitmap = (drawable as? BitmapDrawable)?.bitmap ?: drawable.toBitmap()

                // Build palette synchronously (safe inside LaunchedEffect)
                val palette = Palette.from(bitmap).generate()
                // Prefer vibrant/dominant/muted swatches as available
                val colorInt = palette.vibrantSwatch?.rgb
                    ?: palette.dominantSwatch?.rgb
                    ?: palette.mutedSwatch?.rgb

                dominantColor = colorInt?.let { Color(it) }
                Log.d(TAG, "extracted dominant color = $dominantColor")
            } catch (t: Throwable) {
                Log.w(TAG, "failed to convert Coil image to bitmap: ${t.message}")
            }


        } else {
            Log.d(TAG, "Image request not successful: $result")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        dominantColor?.copy(alpha = alphaValue) ?: Color.Transparent,
                        defaultBackgroundColor
                    )
                )
            ),

        ) {
        content()
    }

}