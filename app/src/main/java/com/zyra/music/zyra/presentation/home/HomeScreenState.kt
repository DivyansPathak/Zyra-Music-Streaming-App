package com.zyra.music.zyra.presentation.home

import com.zyra.music.zyra.domain.model.HomeSection

data class HomeScreenState(
    val sections: List<HomeSection> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)