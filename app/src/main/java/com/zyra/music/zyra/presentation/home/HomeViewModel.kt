package com.zyra.music.zyra.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.domain.model.HomeSection
import com.zyra.music.zyra.domain.repository.PlaylistRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(
    private val repository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        Log.d(TAG, "ViewModel init")
        loadHomeScreenContent()
    }

    fun refresh() {
        loadHomeScreenContent()
    }

    private fun loadHomeScreenContent() {
        viewModelScope.launch {
            Log.d(TAG, "Loading home screen content... ${uiState.value.isLoading}")
            _uiState.update { it.copy(isLoading = true) }

            delay(1000)
            Log.d(TAG, "Fetching featured playlists...")
            val featuredPlaylists = repository.getAllPlaylist("Bollywood")
            val romancePlaylists = repository.getAllPlaylist("Bollywood Romance")
            val dancePlaylist = repository.getAllPlaylist("Bollywood Dance")

            val section = mutableListOf<HomeSection>()
            if (featuredPlaylists.isNotEmpty()) {
                Log.d(TAG, "Adding featured playlists section...")
                section.add(
                    HomeSection(
                        id = "Feature",
                        title = "Featured Playlists",
                        playLists = featuredPlaylists
                    )
                )
            }
            if (romancePlaylists.isNotEmpty()) {
                Log.d(TAG, "Adding romance playlists section...")
                section.add(
                    HomeSection(
                        id = "Romance",
                        title = "Bollywood Romance",
                        playLists = romancePlaylists
                    )
                )
            }
            if (dancePlaylist.isNotEmpty()){
                Log.d(TAG,"Adding dance playlists section...")
                section.add(
                    HomeSection(
                        id = "Dance",
                        title = "Bollywood Dance",
                        playLists = dancePlaylist
                    )
                )
            }
            _uiState.update {
                it.copy(
                    sections = section,
                    isLoading = false
                )
            }
            Log.d(TAG, "Home screen content loaded successfully. ${uiState.value.isLoading}")
        }
    }
}