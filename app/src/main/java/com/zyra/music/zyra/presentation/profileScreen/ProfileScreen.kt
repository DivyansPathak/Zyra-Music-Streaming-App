package com.zyra.music.zyra.presentation.profileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.zyra.music.zyra.R
import com.zyra.music.zyra.data.utils.APP_VERSION
import com.zyra.music.zyra.presentation.profileScreen.common.NameChangeDialog
import com.zyra.music.zyra.presentation.profileScreen.common.ProfileMenuItem
import com.zyra.music.zyra.presentation.profileScreen.common.ProfileMenuUi
import org.schabi.newpipe.extractor.timeago.patterns.id

@Composable
fun ComposeProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    if (state.isNameChangeDialogVisible) {
        NameChangeDialog(state = state, onAction = onAction)
    }

    val menuList = listOf(
        ProfileMenuItem(
            title = "Change Name",
            icon = R.drawable.icon_edit_profile,
            onClick = { onAction(ProfileAction.ShowNameChangeDialog) }
        ),
        ProfileMenuItem(
            title = "Check for New Update",
            icon = R.drawable.icon_update,
            onClick = {}
        ),
        ProfileMenuItem(
            title = "Privacy & Control",
            icon = R.drawable.ic_privacy,
            onClick = { }
        ),
        ProfileMenuItem(
            title = "About & Help",
            icon = R.drawable.ic_help,
            onClick = {  }
        ),

        )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomCenter) {
                AsyncImage(
                    model = state.avatarUrl,
                    contentDescription = "Profile Picture",
                    placeholder = painterResource(id = R.drawable.preview_pager), // Use your placeholder
                    error = painterResource(id = R.drawable.preview_pager), // Use your error drawable
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
                Icon(
                    modifier = Modifier
                        .offset(y = (20.dp))
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .padding(8.dp),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = state.name.takeIf { it.isNotEmpty() } ?: "User Name",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.email.takeIf { it.isNotEmpty() } ?: "",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                        .clip(shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    tonalElevation = 12.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp, bottom = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f)
                        ) {
                            ProfileMenu(
                                modifier = Modifier.padding(16.dp),
                                menuItems = menuList
                            )

                            Spacer(Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable {
                                        onAction(ProfileAction.LogOut)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        modifier = Modifier.padding(10.dp),
                                        painter = painterResource(id = R.drawable.ic_sign_out),
                                        contentDescription = "sign out"
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .weight(1f),
                                        text = "Log out",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.CenterHorizontally),
                            text = APP_VERSION
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Made with ❤️ by PapaJi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMenu(
    modifier: Modifier = Modifier,
    menuItems: List<ProfileMenuItem>
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        menuItems.forEach { item ->
            ProfileMenuUi(
                title = item.title,
                icon = item.icon,
                onClick = item.onClick
            )
        }
    }

}