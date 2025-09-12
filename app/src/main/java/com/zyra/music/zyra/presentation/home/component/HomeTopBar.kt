package com.zyra.music.zyra.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.R
import com.zyra.music.zyra.presentation.common.ProfileButton
import com.zyra.music.zyra.presentation.ui.theme.ZyraTheme

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onNotificationClick : () -> Unit = {},
    onSearchClick : () -> Unit = {},
    onProfileClick : () -> Unit = {}
    ) {


    Row(
        modifier = modifier.background(color = Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
    ) {

       Row(modifier = Modifier.weight(1f),
           verticalAlignment = Alignment.CenterVertically) {
           Image(
               painter = painterResource(id = R.drawable.icon_logo_top),
               contentDescription = null,
               modifier = Modifier
                   .padding(start = 12.dp)
                   .size(64.dp)
           )

           Text(text = "Music", style = MaterialTheme.typography.headlineLarge,
               color = MaterialTheme.colorScheme.primary,
               fontWeight = FontWeight.ExtraBold,
               modifier = Modifier.offset(x = (-8).dp)
           )
       }

        Row(modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,){
            IconButton(onClick = onNotificationClick,
                modifier = Modifier) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(id = R.drawable.search_outlined),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            ProfileButton(
                modifier = Modifier
                    .padding(8.dp)
                    .size(40.dp)
                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    .padding(4.dp),
                onClick = onProfileClick
            )

        }

    }

}

@Preview(showBackground = true)
@Composable
private fun PreviewTop() {
    ZyraTheme {
        HomeTopBar(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        )
    }
}