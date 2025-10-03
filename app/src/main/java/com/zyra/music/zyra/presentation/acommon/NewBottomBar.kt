package com.zyra.music.zyra.presentation.acommon

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.R
import com.zyra.music.zyra.navigation.HomeScreen
import com.zyra.music.zyra.navigation.LibraryScreen
import com.zyra.music.zyra.navigation.MainScreens
import com.zyra.music.zyra.navigation.SearchScreen
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@Composable
fun FeaturedBottomBarN(
    modifier: Modifier = Modifier,
    currentScreen: MainScreens?,
    onScreenSelected: (MainScreens) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.4f)) // Semi-transparent like Spotify
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = if (currentScreen is HomeScreen) R.drawable.home_filled else R.drawable.home_outlined,
                selected = currentScreen is HomeScreen,
                onClick = { onScreenSelected(HomeScreen) },
                contentDescription = "Home"
            )

            BottomNavItem(
                icon = if (currentScreen is SearchScreen) R.drawable.search_filled else R.drawable.search_outlined,
                selected = currentScreen is SearchScreen,
                onClick = { onScreenSelected(SearchScreen) },
                contentDescription = "Search"
            )

            BottomNavItem(
                icon = if (currentScreen is LibraryScreen) R.drawable.library_filled else R.drawable.library_outlined,
                selected = currentScreen is LibraryScreen,
                onClick = { onScreenSelected(LibraryScreen) },
                contentDescription = "Library"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color.White.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 200),
        label = "iconColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.0f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = false,
                    radius = 28.dp,
                    color = Color.White
                ),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .scale(scale)
        )
    }
}

@Preview
@Composable
private fun PreviewFeaturedBottomBar() {
    ZyraTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF121212))
        ) {
            FeaturedBottomBarN(
                currentScreen = HomeScreen,
                onScreenSelected = {}
            )
        }
    }
}