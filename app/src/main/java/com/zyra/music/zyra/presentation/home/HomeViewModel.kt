package com.zyra.music.zyra.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.domain.model.HomeSection
import com.zyra.music.zyra.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
        refreshInBackBackground()
    }

    fun refresh() {
        Log.d(TAG,"Manual refresh triggered")
        refreshInBackBackground()
    }

    private fun observeCachedPlaylists(){
        viewModelScope.launch {
            combine(
                repository.observePlaylists("Bollywood"),
                repository.observePlaylists("Bollywood Romance"),
                repository.observePlaylists("Bollywood Dance"),
                repository.observePlaylists("Apne Bandon ka")
            ){ featured,romance,dance,apne ->
                val sections = mutableListOf<HomeSection>()
                if (featured.isNotEmpty()){
                    sections.add(HomeSection(id = "Romance", title = "Bollywood Romance", playLists = romance))
                }
                if (romance.isNotEmpty()){
                    sections.add(HomeSection(id = "Romance", title = "Bollywood Romance", playLists = romance))
                }
                if (dance.isNotEmpty()){
                    sections.add(HomeSection(id = "Dance", title = "Bollywood Dance", playLists = dance))
                }
                if (apne.isNotEmpty()){
                    sections.add(HomeSection(id = "Apne Bando Ka", title = "Apne Bando Ka", playLists = apne))
                }

                HomeScreenState(
                    sections = sections,
                    isLoading = false
                )

            }.collect{newState ->
                _uiState.value = newState
                Log.d(TAG,"UI updated from local cache : ${newState.sections.size} sections")
            }
        }
    }

    private fun refreshInBackBackground(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val genres = listOf(
                "Bollywood",
                "Bollywood Romance",
                "Bollywood Dance",
                "Apne Bandon ka"
            )
            genres.forEach { genre ->
                repository.refreshPlaylists(genre)
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
            Log.d(TAG,"Background refresh completed")
        }
    }

//    private fun loadHomeScreenContent(forceRefresh : Boolean) {
//        viewModelScope.launch {
//            Log.d(TAG, "Loading home screen content... ${uiState.value.isLoading} force refresh : $forceRefresh")
//            _uiState.update { it.copy(isLoading = true) }
//
//            delay(1000)
//            Log.d(TAG, "Fetching featured playlists...")
//            val featuredPlaylists = repository.getAllPlaylist("Bollywood",forceRefresh)
//            val romancePlaylists = repository.getAllPlaylist("Bollywood Romance",forceRefresh)
//            val dancePlaylist = repository.getAllPlaylist("Bollywood Dance",forceRefresh)
//            val apneBandoKa = repository.getAllPlaylist("Apne Bandon ka",forceRefresh)
//
//            val section = mutableListOf<HomeSection>()
//            if (featuredPlaylists.isNotEmpty()) {
//                Log.d(TAG, "Adding featured playlists section...")
//                section.add(
//                    HomeSection(
//                        id = "Feature",
//                        title = "Featured Playlists",
//                        playLists = featuredPlaylists
//                    )
//                )
//            }
//            if (romancePlaylists.isNotEmpty()) {
//                Log.d(TAG, "Adding romance playlists section...")
//                section.add(
//                    HomeSection(
//                        id = "Romance",
//                        title = "Bollywood Romance",
//                        playLists = romancePlaylists
//                    )
//                )
//            }
//            if (dancePlaylist.isNotEmpty()){
//                Log.d(TAG,"Adding dance playlists section...")
//                section.add(
//                    HomeSection(
//                        id = "Dance",
//                        title = "Bollywood Dance",
//                        playLists = dancePlaylist
//                    )
//                )
//            }
//            if (apneBandoKa.isNotEmpty()){
//                Log.d(TAG,"Adding apne bandon ka playlist section...")
//                section.add(
//                    HomeSection(
//                        id = "Apne Bando Ka",
//                        title = "Apne Bando Ka",
//                        playLists = apneBandoKa
//                    )
//                )
//            }
//            _uiState.update {
//                it.copy(
//                    sections = section,
//                    isLoading = false
//                )
//            }
//            Log.d(TAG, "Home screen content loaded successfully. ${uiState.value.isLoading}")
//        }
//    }
}