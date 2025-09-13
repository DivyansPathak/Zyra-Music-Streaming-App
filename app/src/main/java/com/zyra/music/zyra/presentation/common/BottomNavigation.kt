package com.zyra.music.zyra.presentation.common

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentDestination : String?
    ) {

    val items = listOf(
        BottomNavItems.Home,
        BottomNavItems.Search
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val current = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->

            val routeString = item.route::class.qualifiedName
            val isSelected = currentDestination == item.route
            NavigationBarItem(
//                selected = current?.hierarchy?.any { it.route == item.route } == true,
                selected = isSelected,
                onClick = {
                    Log.d("BottomNavigationBar", "Current: $currentDestination, Target: $routeString, Selected: $isSelected")

                    // Only navigate if we're not already on the target screen
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            launchSingleTop = true

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(painter = painterResource(id = item.icon),
                        contentDescription = item.label)
                },
                label = {Text(text = item.label)}
            )
        }
    }
}
