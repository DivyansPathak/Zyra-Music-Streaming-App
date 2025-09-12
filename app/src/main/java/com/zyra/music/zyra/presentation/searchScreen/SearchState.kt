package com.zyra.music.zyra.presentation.searchScreen


import androidx.compose.ui.text.input.TextFieldValue
import com.zyra.music.zyra.domain.model.SingleTrack
import com.zyra.music.zyra.domain.model.TrackFullOne
import com.zyra.music.zyra.domain.utils.DataError

data class SearchState(
//    val query : String = "",
    val query : TextFieldValue = TextFieldValue(""),
    val searchResults : List<SingleTrack> = emptyList(),
    val searchResultsFromYT : List<TrackFullOne> = emptyList(),
    val searchSuggestions : List<String> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null
)