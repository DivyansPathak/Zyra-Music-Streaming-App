package com.zyra.music.zyra.presentation.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.zyra.music.zyra.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBarNew(
    modifier: Modifier = Modifier,
    collapseFraction: Float,
    onSearchClick: () -> Unit
) {
    // Linearly interpolate (lerp) values based on the collapse fraction

    // Title fades out completely
    val titleAlpha = lerp(1f, 0f, collapseFraction * 2) // Fades out faster

    // Padding for the title when expanded
    val titlePaddingTop = lerp(16.dp, 0.dp, collapseFraction)

    // The search bar moves up to the top
    val searchBarPaddingTop = lerp(104.dp, 16.dp, collapseFraction)

    val user = SupabaseClient.supabase.auth.currentUserOrNull()
    val userName = user?.userMetadata?.get("name")?.jsonPrimitive?.content ?: ""

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Hello,\n$userName", // You can pass this as a parameter
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface, // Adjust color as needed
            modifier = Modifier
                .padding(top = titlePaddingTop)
                .alpha(titleAlpha)
                .align(Alignment.TopStart)
        )
        MyFakeSearchBar(
            modifier = Modifier
                .padding(top = searchBarPaddingTop)
                .align(Alignment.TopCenter),
            onSearchClick = onSearchClick
        )
    }
}