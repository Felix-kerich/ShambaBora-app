package com.app.shamba_bora.ui.components.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.shamba_bora.data.model.Post

/**
 * Share Modal for sharing posts to groups, users, or external apps
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePostModal(
    post: Post,
    onDismiss: () -> Unit,
    onShareToGroup: (Long) -> Unit = {},
    onShareToUser: (Long) -> Unit = {},
    onCopyLink: () -> Unit = {},
    onShareExternal: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Groups", "Users", "More")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Title
            Text(
                text = "Share Post",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Preview of post being shared
            PostSharePreview(post = post)

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (selectedTab) {
                0 -> ShareToGroupsTab(onShareToGroup = onShareToGroup)
                1 -> ShareToUsersTab(onShareToUser = onShareToUser)
                2 -> ShareMoreOptions(
                    onCopyLink = {
                        onCopyLink()
                        onDismiss()
                    },
                    onShareExternal = {
                        onShareExternal()
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PostSharePreview(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Article,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.authorName ?: "Unknown User",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = post.content.take(60) + if (post.content.length > 60) "..." else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ShareToGroupsTab(
    onShareToGroup: (Long) -> Unit
) {
    // Mock groups - Replace with actual data from viewModel
    val groups = remember {
        listOf(
            ShareTarget(1L, "Maize Farmers Kenya", "245 members", Icons.Default.Group),
            ShareTarget(2L, "Organic Farming Tips", "189 members", Icons.Default.Group),
            ShareTarget(3L, "Farm Equipment Sales", "456 members", Icons.Default.Group),
            ShareTarget(4L, "Agricultural Advice", "678 members", Icons.Default.Group)
        )
    }

    if (groups.isEmpty()) {
        EmptyShareState(
            message = "You're not a member of any groups yet",
            icon = Icons.Default.Group
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(groups) { group ->
                ShareTargetItem(
                    target = group,
                    onClick = { onShareToGroup(group.id) }
                )
            }
        }
    }
}

@Composable
private fun ShareToUsersTab(
    onShareToUser: (Long) -> Unit
) {
    // Mock users - Replace with actual data from viewModel
    val users = remember {
        listOf(
            ShareTarget(1L, "John Kamau", "Online now", Icons.Default.Person),
            ShareTarget(2L, "Mary Wanjiru", "Active 2h ago", Icons.Default.Person),
            ShareTarget(3L, "Peter Odhiambo", "Active 5h ago", Icons.Default.Person),
            ShareTarget(4L, "Grace Akinyi", "Active yesterday", Icons.Default.Person)
        )
    }

    if (users.isEmpty()) {
        EmptyShareState(
            message = "No connections yet. Connect with farmers to share posts!",
            icon = Icons.Default.Person
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                ShareTargetItem(
                    target = user,
                    onClick = { onShareToUser(user.id) }
                )
            }
        }
    }
}

@Composable
private fun ShareMoreOptions(
    onCopyLink: () -> Unit,
    onShareExternal: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShareOptionCard(
            icon = Icons.Default.Link,
            title = "Copy Link",
            description = "Copy link to clipboard",
            onClick = onCopyLink
        )
        ShareOptionCard(
            icon = Icons.Default.Share,
            title = "Share via...",
            description = "Share using other apps",
            onClick = onShareExternal
        )
        ShareOptionCard(
            icon = Icons.Default.Email,
            title = "Send via Email",
            description = "Share through email",
            onClick = { /* TODO: Email share */ }
        )
    }
}

@Composable
private fun ShareTargetItem(
    target: ShareTarget,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = target.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = target.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = target.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ShareOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyShareState(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Data class for share targets
data class ShareTarget(
    val id: Long,
    val name: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
