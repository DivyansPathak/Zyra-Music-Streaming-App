package com.zyra.music.zyra.presentation.common


import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zyra.music.zyra.R
import com.zyra.music.zyra.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth

@Composable
fun ProfileButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {


    val context = LocalContext.current

    val user = SupabaseClient.supabase.auth.currentUserOrNull()
    val metadata = user?.userMetadata
    Log.d("ProfileButton", "User metadata: $metadata")

    // try avatar_url first, fallback to picture
    val rawUrl = metadata?.get("avatar_url") ?: metadata?.get("picture")
    val profileUrl = rawUrl?.toString()?.removeSurrounding("\"")


    val imageRequest = ImageRequest.Builder(context)
        .data(profileUrl)
        .crossfade(enable = true)
        .listener(
            onError = { _, result -> Log.e("ProfileButton", "Image load failed", result.throwable) },
            onSuccess = { _, _ -> Log.d("ProfileButton", "Image loaded successfully!") }
        )
        .build()

    Log.d("ProfileButton", "avatar_url: $profileUrl")

    IconButton(onClick = onClick, modifier = modifier) {

            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillBounds,
                placeholder = painterResource(id = R.drawable.preview_pager),
                error = painterResource(id = R.drawable.preview_pager)
            )
    }

}