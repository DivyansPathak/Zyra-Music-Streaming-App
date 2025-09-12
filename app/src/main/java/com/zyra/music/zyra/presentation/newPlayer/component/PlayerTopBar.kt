package com.zyra.music.zyra.presentation.newPlayer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.R
import com.zyra.music.zyra.presentation.utils.formatDurationLong

@Composable
fun PlayerTopBar(modifier: Modifier = Modifier,
                 onBackClick: () -> Unit,
                 timerText : Long?) {
    val formatedTimer = formatDurationLong(timerText?.toLong() ?: 0L)
    Row(modifier = modifier.fillMaxWidth().background(color = Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        ) {
        IconButton(onClick = onBackClick) {
            Icon(
                modifier = Modifier.size(36.dp),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (timerText != null) {
            Row(modifier = Modifier.fillMaxWidth().padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.timer_show),
                    contentDescription = "Sleep timer active",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatedTimer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

    }
}