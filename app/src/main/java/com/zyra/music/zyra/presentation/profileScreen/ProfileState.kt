package com.zyra.music.zyra.presentation.profileScreen

data class ProfileState(
    val isLoading : Boolean = false,
    val avatarUrl : String? = null,
    val name : String = "",
    val email : String = "",
    val error : String? = null,
    val isNameChangeDialogVisible : Boolean = false
)
