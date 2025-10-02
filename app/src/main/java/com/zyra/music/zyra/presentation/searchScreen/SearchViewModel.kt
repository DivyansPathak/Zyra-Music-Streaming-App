package com.zyra.music.zyra.presentation.searchScreen

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.domain.utils.getErrorMessage
import com.zyra.music.zyra.domain.utils.onFailure
import com.zyra.music.zyra.domain.utils.onSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "SearchViewModel"

class SearchViewModel(
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState = _uiState.asStateFlow()

//    private val _navigationEvent = Channel<Route>()
//    val navigationEvent = _navigationEvent.receiveAsFlow()

    var searchJob: Job? = null

    var suggestionJob: Job? = null

    fun onQueryChange(query: TextFieldValue) {

        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()
        val cleanQuery = query.text.trim()
        if (cleanQuery.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), error = null) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(1000L)
            executeSearch(cleanQuery)
//            val result = songRepository.searchSong("despacito")
//            Log.d("TEST", result.toString())
        }
    }

    private fun fetchSuggestions(query: String) {
        suggestionJob?.cancel()
        val cleanQuery = query.trim()

        if (cleanQuery.isBlank()) {
            _uiState.update {
                it.copy(
                    searchSuggestions = emptyList(),
                    searchResultsFromYT = emptyList(),
                    error = null
                )
            }
            return
        }

        suggestionJob = viewModelScope.launch {
            delay(100L)
            songRepository.getSearchSuggestions(query = cleanQuery)
                .onSuccess { suggestions ->
                    _uiState.update { it.copy(searchSuggestions = suggestions, error = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.getErrorMessage()) }
                }
        }
    }

    fun onAction(action: SearchAction) {
        Log.d(TAG, "Action received: $action")
        when (action) {
            is SearchAction.OnQueryChange -> {
//                onQueryChange(query = action.query)
                _uiState.update { it.copy(query = action.query) }
                fetchSuggestions(query = action.query.text)
            }

            is SearchAction.OnSuggestionClick -> {
                val suggestion = action.suggestion
                _uiState.update {
                    it.copy(
                        query = TextFieldValue(
                            text = suggestion,
                            selection = TextRange(suggestion.length)
                        ),
                        searchSuggestions = emptyList()
                    )
                }
                executeSearch(action.suggestion)
            }

            is SearchAction.OnTrackClick -> {
                // Handle track click
                Log.i(TAG, "Track clicked: ${action.track.title}")

            }

            is SearchAction.OnImeSearchClick -> {
                val cleanQuery = action.query.trim()
                if (cleanQuery.isNotBlank()) {
                    Log.d(TAG, "Ime search clicked for query: $cleanQuery")
                    _uiState.update {
                        it.copy(
                            query = TextFieldValue(
                                text = cleanQuery,
                                selection = TextRange(cleanQuery.length)
                            ), searchSuggestions = emptyList()
                        )
                    }
                    executeSearch(cleanQuery)
                }
            }

            is SearchAction.OnClearQuery -> {
                onQueryChange(query = TextFieldValue(""))
            }
        }
    }

    private fun executeSearch(query: String) {
        viewModelScope.launch {
            Log.d(TAG, "1. Executing search for query: '$query'")
            _uiState.update {
                it.copy(
                    isLoading = true,
                    searchSuggestions = emptyList(),
                    searchResultsFromYT = emptyList(), error = null
                )
            }
            songRepository.searchSongFromYt(query = query)
                .onSuccess { songs ->
                    Log.i(TAG, "5. SUCCESS: Found ${songs.size} songs.")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            searchResultsFromYT = songs,
                            error = null,
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "5. FAILURE: Search failed. Error: $error")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.getErrorMessage()
                        )
                    }
                }

        }

    }
}