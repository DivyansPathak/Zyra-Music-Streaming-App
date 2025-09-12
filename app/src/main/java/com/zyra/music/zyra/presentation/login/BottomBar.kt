package com.zyra.music.zyra.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.R
import com.zyra.music.zyra.presentation.ui.theme.DeepBlack
import com.zyra.music.zyra.presentation.ui.theme.LightGray
import com.zyra.music.zyra.presentation.ui.theme.NeonBlue
import com.zyra.music.zyra.presentation.ui.theme.NeonGreen
import com.zyra.music.zyra.presentation.ui.theme.NeonPink


// 1. Data class to represent each navigation item
data class BottomNavItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
)

@Composable
fun ClassicFloatingBottomBar(
    items: List<BottomNavItem>,
    selectedItemIndex: Int,
    onItemClick: (Int) -> Unit
) {
    val NeonIndicatorGradient = Brush.horizontalGradient(
        colors = listOf(NeonGreen, NeonBlue, NeonPink)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        NavigationBar(
            containerColor = DeepBlack
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItemIndex == index
                NavigationBarItem(
                    selected = selectedItemIndex == index,
                    onClick = { onItemClick(index) },
                    label = { Text(text = item.title,
                        color = if (selectedItemIndex == index) NeonGreen else LightGray
                    ) },
                    icon = {
                        Icon(
                            painter = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            },
                            contentDescription = item.title
                        )
                    },
                    // Customize the colors of the icons and text
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonGreen,
                        selectedTextColor = NeonGreen,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = LightGray,
                        unselectedTextColor = LightGray
                    ),
                    alwaysShowLabel = true
                )
               if (isSelected){
                   Box(modifier = Modifier
                       .background(
                           brush = NeonIndicatorGradient,
                           shape = RoundedCornerShape(50)
                       ).alpha(0.15f)){

                   }
               }

            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewMorphinNav() {
    val items = listOf(
        BottomNavItem(
            "Home",
            painterResource(id = R.drawable.home_filled),
            painterResource(id = R.drawable.home_outlined)
        ),
        BottomNavItem(
            "Explore",
            painterResource(id = R.drawable.search_filled),
            painterResource(id = R.drawable.search_outlined)
        ),
        BottomNavItem(
            "Library",
            painterResource(id = R.drawable.library_filled),
            painterResource(id = R.drawable.library_outlined)
        )
    )
    var selectedItemIndex by remember { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            ClassicFloatingBottomBar(
                items = items,
                selectedItemIndex = selectedItemIndex,
                onItemClick = { selectedItemIndex = it }
            )
        }
    ) { paddingValues ->
        // Your screen content here
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize())
    }
}