package com.zyra.music.zyra.presentation.common

import com.zyra.music.zyra.R
import com.zyra.music.zyra.navigation.Route

sealed  class BottomNavItems(val route : Route,val icon : Int, val label : String){
    object Home : BottomNavItems(Route.HomeScreen,R.drawable.home_outlined, label = "Home")
    object Search : BottomNavItems(Route.SearchScreen,R.drawable.search_outlined, label = "Search")
}
