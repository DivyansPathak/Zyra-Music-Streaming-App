package com.zyra.music.zyra.presentation.common

import com.zyra.music.zyra.R
import com.zyra.music.zyra.navigation.Route

sealed  class BottomNavItems(val route : String,val icon : Int, val label : String){
    object Home : BottomNavItems(Route.HomeScreen.title,R.drawable.home_outlined, label = "Home")
    object Search : BottomNavItems(Route.SearchScreen.title,R.drawable.search_outlined, label = "Search")
}
