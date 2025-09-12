package com.zyra.music.zyra.presentation.login

data class AuthState(
    val isLoading : Boolean = false,
    val error : String? = null,
    val onGoogleSignIn : () -> Unit,
    val onEmailSignIn : (String,String) -> Unit,
    val onSignOut : suspend () -> Unit,
    val getProfileImage : () -> String?
)
