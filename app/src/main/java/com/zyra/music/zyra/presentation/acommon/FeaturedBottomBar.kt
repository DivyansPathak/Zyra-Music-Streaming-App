package com.zyra.music.zyra.presentation.acommon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun FeaturedBottomBar(
    modifier: Modifier = Modifier,
    currentScreen : MainScreens?,
    onScreenSelected : (MainScreens) -> Unit
) {

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.Black)
            .height(90.dp)
            .padding(4.dp),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp

    ){
        NavigationBarItem(
            selected = currentScreen is HomeScreen,
            onClick = {onScreenSelected(HomeScreen)},
            icon = {
                val iconRes = if (currentScreen is HomeScreen){
                    R.drawable.home_filled
                } else{
                    R.drawable.home_outlined
                }
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Home Icon"
                )
            }
        )

        NavigationBarItem(
            selected = currentScreen is LibraryScreen,
            onClick = {onScreenSelected(LibraryScreen)},
            icon = {
                val iconRes = if (currentScreen is LibraryScreen){
                    R.drawable.library_filled
                } else{
                    R.drawable.library_outlined
                }
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Home Icon"
                )
            }
        )

        NavigationBarItem(
            selected = currentScreen is SearchScreen,
            onClick = {onScreenSelected(SearchScreen)},
            icon = {
                val iconRes = if (currentScreen is SearchScreen){
                    R.drawable.search_filled
                } else{
                    R.drawable.search_outlined
                }
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Home Icon"
                )
            }
        )

    }


}

@Preview
@Composable
private fun PreviewFeaturedBottomBar() {
    ZyraTheme {
        FeaturedBottomBar(
            currentScreen = null,
            onScreenSelected = {}
        )
    }
}