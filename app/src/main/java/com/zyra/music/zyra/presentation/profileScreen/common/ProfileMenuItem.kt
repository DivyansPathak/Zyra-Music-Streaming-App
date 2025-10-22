package com.zyra.music.zyra.presentation.profileScreen.common

data class ProfileMenuItem(
    val title : String,
    val icon : Int,
    val onClick : () -> Unit
)
