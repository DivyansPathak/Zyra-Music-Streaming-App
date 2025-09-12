package com.zyra.music.zyra.presentation.searchScreen

import androidx.compose.ui.text.input.TextFieldValue
import com.zyra.music.zyra.domain.model.SingleTrack

interface SearchAction {

//    data class OnQueryChange(val query : String) : SearchAction
    data class OnQueryChange(val query : TextFieldValue) : SearchAction
    data class OnTrackClick(val track : SingleTrack) : SearchAction
    data object OnClearQuery : SearchAction
    data object OnBackClick : SearchAction
    data class OnSuggestionClick(val suggestion : String) : SearchAction
    data class OnImeSearchClick(val query : String) : SearchAction
}