package com.zyra.music.zyra.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zyra.music.zyra.presentation.home.HomeScreenState
import com.zyra.music.zyra.presentation.home.component.HomeTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    state: HomeScreenState,
    onPlaylistClick : (playlistId : String) -> Unit,
    ) {

    var lazyListState = rememberLazyListState()

    val headerAlpha by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex >0 ){
                0f
            }else{
                (1f - lazyListState.firstVisibleItemScrollOffset /200f).coerceIn(0f,1f)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn (
            state = lazyListState,
            contentPadding = PaddingValues(top = 112.dp)
        ){

        }
    }


}