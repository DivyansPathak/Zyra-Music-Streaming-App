package com.zyra.music.zyra.presentation.searchScreen


import androidx.compose.ui.text.input.TextFieldValue
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.model.playlistData.PlaylistYT

data class SearchState(
    val query : TextFieldValue = TextFieldValue(""),
    val searchResultsFromYT : List<TrackFullOne> = emptyList(),
    val searchResultsFromYoutube : List<TrackFullOne> = emptyList(),
    val playlistFromYoutube : List<PlaylistYT> = emptyList(),
    val searchSuggestions : List<String> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null
)