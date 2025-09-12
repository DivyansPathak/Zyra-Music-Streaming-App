package com.zyra.music.zyra.presentation.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController


@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController) {

    val items = listOf(
        BottomNavItems.Home,
        BottomNavItems.Search
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = true,
                onClick = {navController.navigate(item.route)},
                icon = {
                    Icon(painter = painterResource(id = item.icon),
                        contentDescription = item.label)
                },
                label = {Text(text = item.label)}
            )
        }
    }
}

@Preview
@Composable
private fun  Preview() {

}