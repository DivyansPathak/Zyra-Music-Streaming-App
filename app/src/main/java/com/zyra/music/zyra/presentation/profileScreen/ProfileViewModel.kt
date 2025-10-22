package com.zyra.music.zyra.presentation.profileScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.schabi.newpipe.extractor.timeago.patterns.fa

private const val TAG = "ProfileViewModel"

class ProfileViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ProfileEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        Log.d(TAG,"ProfileViewModel initiated")
        onAction(ProfileAction.LoadProfile)
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.LoadProfile -> loadUserProfile()
            is ProfileAction.OnNameChanged -> saveChange(action.updateName)
            is ProfileAction.ShowNameChangeDialog -> {
                _uiState.update { it.copy(isNameChangeDialogVisible = true) }
            }
            is ProfileAction.HideNameChangeDialog ->{
                _uiState.update { it.copy(isNameChangeDialogVisible = false) }
            }
            is ProfileAction.LogOut -> logOut()
        }
    }

    private fun logOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                SupabaseClient.supabase.auth.signOut()
                _uiState.update { it.copy(isLoading = false) }
                Log.d(TAG,"signed out successfully")
                _uiEvent.send(ProfileEvent.ShowMessage(message = "Signed out successfully"))
            }catch (e: Exception){
                Log.e(TAG,"LogOut failed : ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(ProfileEvent.ShowMessage(message = "Error signing out"))
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = SupabaseClient.supabase.auth.currentUserOrNull()
            if (user != null) {
                val metadata = user.userMetadata
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        avatarUrl = (metadata?.get("avatar_url")
                            ?: metadata?.get("picture"))?.jsonPrimitive?.content,
                        name = metadata?.get("name")?.jsonPrimitive?.content ?: "",
                        email = user.email ?: "",
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "User not Found") }
            }
        }
    }

    private fun saveChange(updateName : String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {

                SupabaseClient.supabase.auth.updateUser {
                    data = buildJsonObject {
                        put("name", updateName)
                    }
                    Log.d(TAG, "name changed successfully in Supabase")
                }
                _uiState.update { it.copy(isLoading = false, name = updateName, isNameChangeDialogVisible = false) }
                _uiEvent.send(ProfileEvent.ShowMessage("Profile updated successfully"))
            } catch (e: Exception) {
                Log.e(TAG, "error in saving change : ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(ProfileEvent.ShowMessage(message = "Error : ${e.message}"))
            }
        }
    }

}