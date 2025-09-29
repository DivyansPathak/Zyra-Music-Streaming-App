package com.zyra.music.zyra.presentation.common
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.KeyboardArrowDown
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.material3.TopAppBarScrollBehavior
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import coil3.compose.AsyncImage
//import com.zyra.music.zyra.R
//import com.zyra.music.zyra.data.remote.SupabaseClient
//import com.zyra.music.zyra.navigation.Route
//import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme
//import io.github.jan.supabase.auth.auth
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CustomTopBar(
//    modifier: Modifier = Modifier,
//    scrollBehavior: TopAppBarScrollBehavior,
//    currentScreen: Route?,
//    onSearchClick: () -> Unit,
//    onProfileClick: () -> Unit,
//    onBackIconClick : () -> Unit,
//    onDropDownClick : () -> Unit,
//    onProfileClose : () -> Unit
//) {
//
//    TopAppBar(
//        title =
////            {Text(text = "Zyra")}
//            { Text(text = currentScreen.toString() ?: "") }
//        ,
//        navigationIcon = {
//            if (currentScreen is Route.HomeScreen) {
//                Image(
//                    painterResource(id = R.drawable.icon_logo_top),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.size(48.dp)
//                )
//            }
////            if (currentScreen is Route.PlayListScreen) {
////                IconButton(onClick = onBackIconClick) {
////                    Icon(
////                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
////                        contentDescription = null,
////                        tint = MaterialTheme.colorScheme.primary
////                    )
////                }
////            }
////            if (currentScreen is Route.PlayerScreen) {
////                IconButton(onClick = onDropDownClick) {
////                    Icon(
////                        imageVector = Icons.Default.ArrowDropDown, contentDescription = null,
////                        tint = MaterialTheme.colorScheme.primary
////                    )
////                }
////            }
//        },
//        actions = {
//            when (currentScreen) {
//                is Route.HomeScreen, is Route.SearchScreen, -> {
//                    IconButton(onClick = onSearchClick) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.search_outlined),
//                            contentDescription = null,
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                    ProfileButton(onClick = onProfileClick)
//
//                }
//
//                is Route.PlayerScreen -> {
//                    IconButton(onClick = {}) {
//                        Icon(
//                            imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null,
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
////                is Route.PlayListScreen -> {
////                    IconButton(onClick = onSearchClick) {
////                        Icon(
////                            painter = painterResource(id = R.drawable.search_outlined),
////                            contentDescription = null,
////                            tint = MaterialTheme.colorScheme.primary
////                        )
////                    }
////                }
//
//                is Route.ProfileScreen -> {
//                    IconButton(onClick = onProfileClose) {
//                        Icon(
//                            imageVector = Icons.Default.Close, contentDescription = null,
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
//
//                else -> {
//                    ProfileButton(onClick = onProfileClick)
//                }
//            }
//        },
//        scrollBehavior = scrollBehavior,
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = Color.Transparent,
//            scrolledContainerColor = MaterialTheme.colorScheme.background
//        )
//    )
//
//}
//
//@Composable
//fun ProfileButton(
//    onClick: () -> Unit
//) {
//    IconButton(onClick = onClick) {
//        val user = SupabaseClient.supabase.auth.currentUserOrNull()
//        val userImage = user?.userMetadata?.get("avatar_url").toString()
//        AsyncImage(
//            model = userImage,
//            contentDescription = null,
//            placeholder = painterResource(R.drawable.preview_pager),
//            error = painterResource(R.drawable.preview_pager),
//            modifier = Modifier
//                .size(32.dp)
//                .clip(CircleShape),
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
//@Composable
//private fun PreviewCustomTopBar() {
//    ZyraTheme {
//    CustomTopBar(
//        currentScreen = Route.HomeScreen,
//        onSearchClick = {},
//        onProfileClick = {},
//        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
//        onBackIconClick = {},
//        onDropDownClick = {},
//        onProfileClose = {}
//    )
//    }
//}