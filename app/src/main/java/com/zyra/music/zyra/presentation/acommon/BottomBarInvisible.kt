package com.zyra.music.zyra.presentation.acommon

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.R
import com.zyra.music.zyra.navigation.HomeScreen
import com.zyra.music.zyra.navigation.LibraryScreen
import com.zyra.music.zyra.navigation.MainScreens
import com.zyra.music.zyra.navigation.ProfileScreen
import com.zyra.music.zyra.navigation.SearchScreen

@Composable
fun BottomBarInvisible(
    modifier: Modifier = Modifier,
    currentScreen : MainScreens?,
    onTabSelected : (MainScreens) -> Unit) {

    NavigationBar(
        modifier = modifier.fillMaxWidth()
            .height(60.dp),
        containerColor = Color.Transparent.copy(alpha = 0.7f),
        tonalElevation = 0.dp,
    ) {
        BottomNavItem(
            modifier = Modifier.weight(1f),
            icon = if (currentScreen is HomeScreen) R.drawable.home_filled else R.drawable.home_outlined,
            selected = currentScreen is HomeScreen,
            onClick = {onTabSelected(HomeScreen)},
            contentDescription = "Home"
        )
        BottomNavItem(
            modifier = Modifier.weight(1f),
            icon = if (currentScreen is SearchScreen) R.drawable.search_filled else R.drawable.search_outlined,
            selected = currentScreen is SearchScreen,
            onClick = {onTabSelected(SearchScreen)},
            contentDescription = "Search"
        )
        BottomNavItem(
            modifier = Modifier.weight(1f),
            icon = if (currentScreen is LibraryScreen) R.drawable.library_filled else R.drawable.library_outlined,
            selected = currentScreen is LibraryScreen,
            onClick = {onTabSelected(LibraryScreen)},
            contentDescription = "Library"
        )
        BottomNavItem(
            modifier = Modifier.weight(1f),
            icon = if (currentScreen is ProfileScreen) R.drawable.user_filled else R.drawable.user_outlined,
            selected = currentScreen is ProfileScreen,
            onClick = {onTabSelected(ProfileScreen)},
            contentDescription = "Library"
        )
    }
}

@Composable
private fun RowScope.BottomNavItem(
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
            .fillMaxHeight()
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
                .size(28.dp)
                .scale(scale)
        )
    }
}