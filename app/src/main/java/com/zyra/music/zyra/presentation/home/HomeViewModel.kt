package com.zyra.music.zyra.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.domain.model.HomeSection
import com.zyra.music.zyra.domain.repository.PlaylistRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
        observeCachedPlaylists()
        viewModelScope.launch {
            refreshInBackBackground()
        }
    }

    fun refresh() {
        Log.d(TAG, "Manual refresh triggered")
        viewModelScope.launch {
            refreshInBackBackground()
        }
    }

    private fun observeCachedPlaylists() {
        viewModelScope.launch {
            combine(
                repository.observePlaylists("Bollywood"),
                repository.observePlaylists("Bollywood Romance"),
                repository.observePlaylists("Bollywood Dance"),
                repository.observePlaylists("Apne Bandon ka")
            ) { featured, romance, dance, apne ->
                val sections = mutableListOf<HomeSection>()
                if (featured.isNotEmpty()) {
                    sections.add(
                        HomeSection(
                            id = "Feature",
                            title = "Feature for you",
                            playLists = featured
                        )
                    )
                }
                if (romance.isNotEmpty()) {
                    sections.add(
                        HomeSection(
                            id = "Romance",
                            title = "Bollywood Romance",
                            playLists = romance
                        )
                    )
                }
                if (dance.isNotEmpty()) {
                    sections.add(
                        HomeSection(
                            id = "Dance",
                            title = "Bollywood Dance",
                            playLists = dance
                        )
                    )
                }
                if (apne.isNotEmpty()) {
                    sections.add(
                        HomeSection(
                            id = "Apne Bando Ka",
                            title = "Apne Bando Ka",
                            playLists = apne
                        )
                    )
                }

                HomeScreenState(
                    sections = sections,
                    isLoading = false
                )

            }.collect { newState ->
                _uiState.value = newState
                Log.d(TAG, "UI updated from local cache : ${newState.sections.size} sections")
            }
        }
    }

    private suspend fun refreshInBackBackground() {
        viewModelScope.launch {
            if (_uiState.value.sections.isNotEmpty()){
                _uiState.update { it.copy(isLoading = true) }
            }
        }
        try{
            val genre = listOf(
                "Bollywood",
                "Bollywood Romance",
                "Bollywood Dance",
                "Apne Bandon ka"
            )
            coroutineScope {
                genre.map { genre ->
                    async { repository.refreshPlaylists(genre) }
                }.awaitAll()
            }
        } finally {
            _uiState.update { it.copy(isLoading = false) }
            Log.d(TAG,"Background refresh completed")
        }
    }

}