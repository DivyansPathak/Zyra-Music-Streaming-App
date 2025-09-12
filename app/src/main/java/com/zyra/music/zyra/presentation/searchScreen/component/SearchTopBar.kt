package com.zyra.music.zyra.presentation.searchScreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.R
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@Composable
fun SearchTopBar(
    modifier: Modifier = Modifier,
//    query: String,
//    onQueryChange: (String) -> Unit,
    query : TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onTrailingIconClick: () -> Unit,
    onBackClick: () -> Unit,
    onImeSearchClick : (String) -> Unit,

) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        CustomSearchBar(
            modifier = Modifier.weight(1f),
            query = query,
            onQueryChange = onQueryChange,
            onTrailingIconClick = onTrailingIconClick,
            onSearch = {newQuery -> onImeSearchClick(newQuery)}
        )

        IconButton(onClick = {}) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.Default.Notifications, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = {  }) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.filter_icon),
                contentDescription = "filter Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }

    }
}

//@Preview
//@Composable
//private fun PreviewCustomTopBar() {
//    ZyraTheme {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(color = MaterialTheme.colorScheme.background)
//        ) {
//            SearchTopBar(
//                query = "",
//                onQueryChange = {},
//                onTrailingIconClick = {},
//                onBackClick = {},
//
//            )
//
//        }
//    }
//}
