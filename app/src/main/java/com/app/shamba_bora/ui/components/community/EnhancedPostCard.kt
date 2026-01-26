package com.app.shamba_bora.ui.components.community

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.shamba_bora.data.model.Post

/**
 * Enhanced Post Card with better UX and interactions
 */
@Composable
fun EnhancedPostCard(
    post: Post,
    onLike: () -> Unit,
    onUnlike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onUserClick: (Long, String) -> Unit,
    onPostClick: () -> Unit,
    onMessageUser: (Long, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var showMore by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showShareModal by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPostClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Post Header
            PostHeader(
                authorName = post.authorName ?: "Unknown User",
                authorId = post.authorId ?: 0L,
                createdAt = post.createdAt ?: "",
                postType = post.postType ?: "GENERAL",
                onUserClick = onUserClick,
                onMenuClick = { showMenu = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Post Content
            PostContent(
                content = post.content,
                imageUrl = post.imageUrl,
                showMore = showMore,
                onToggleShowMore = { showMore = !showMore }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Engagement Stats
            PostEngagementStats(
                likeCount = post.likeCount ?: 0,
                commentCount = post.commentCount ?: 0,
                shareCount = post.shareCount ?: 0
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            PostActionButtons(
                isLiked = post.likedByCurrentUser ?: false,
                onLike = { if (post.likedByCurrentUser == true) onUnlike() else onLike() },
                onComment = onComment,
                onShare = { showShareModal = true }
            )
        }
    }

    // Post Menu
    if (showMenu) {
        PostMenu(
            post = post,
            onDismiss = { showMenu = false },
            onMessageUser = {
                showMenu = false
                onMessageUser(post.authorId ?: 0L, post.authorName ?: "User")
            },
            onReport = { /* TODO: Report post */ },
            onSave = { /* TODO: Save post */ },
            onHide = { /* TODO: Hide post */ }
        )
    }

    // Share Modal
    if (showShareModal) {
        SharePostModal(
            post = post,
            onDismiss = { showShareModal = false },
            onShareToGroup = { groupId ->
                // TODO: Share to group
                showShareModal = false
                onShare()
            },
            onShareToUser = { userId ->
                // TODO: Share to user
                showShareModal = false
                onShare()
            },
            onCopyLink = {
                // TODO: Copy link to clipboard
                onShare()
            },
            onShareExternal = {
                // TODO: Share via external apps
                onShare()
            }
        )
    }
}

@Composable
private fun PostHeader(
    authorName: String,
    authorId: Long,
    createdAt: String,
    postType: String,
    onUserClick: (Long, String) -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Avatar (Clickable)
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable { onUserClick(authorId, authorName) },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // User Info (Clickable)
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onUserClick(authorId, authorName) }
        ) {
            Text(
                text = authorName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (postType != "GENERAL") {
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = getPostTypeColor(postType)
                    ) {
                        Text(
                            text = formatPostType(postType),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        // More Menu
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
    }
}

@Composable
private fun PostContent(
    content: String,
    imageUrl: String?,
    showMore: Boolean,
    onToggleShowMore: () -> Unit
) {
    val context = LocalContext.current
    val maxLines = if (showMore) Int.MAX_VALUE else 5
    val shouldShowButton = content.length > 200

    Column {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )

        if (shouldShowButton) {
            TextButton(
                onClick = onToggleShowMore,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = if (showMore) "Show less" else "Show more",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Post Image
        imageUrl?.let { url ->
            if (url.isNotBlank() && url != "null") {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(url)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Post image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 400.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun PostEngagementStats(
    likeCount: Int,
    commentCount: Int,
    shareCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (likeCount > 0) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatCount(likeCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (commentCount > 0) {
                Text(
                    text = "$commentCount ${if (commentCount == 1) "comment" else "comments"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (shareCount > 0) {
                Text(
                    text = "$shareCount ${if (shareCount == 1) "share" else "shares"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PostActionButtons(
    isLiked: Boolean,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Like Button with animation
        Box(modifier = Modifier.weight(1f)) {
            ActionButton(
                icon = if (isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUp,
                label = if (isLiked) "Liked" else "Like",
                isActive = isLiked,
                onClick = onLike
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Comment Button
        Box(modifier = Modifier.weight(1f)) {
            ActionButton(
                icon = Icons.Default.MailOutline,
                label = "Comment",
                isActive = false,
                onClick = onComment
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Share Button
        Box(modifier = Modifier.weight(1f)) {
            ActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                isActive = false,
                onClick = onShare
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (isActive) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isActive) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostMenu(
    post: Post,
    onDismiss: () -> Unit,
    onMessageUser: () -> Unit,
    onReport: () -> Unit,
    onSave: () -> Unit,
    onHide: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Post Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            MenuOption(
                icon = Icons.Default.Send,
                title = "Message ${post.authorName ?: "User"}",
                subtitle = "Start a conversation",
                onClick = {
                    onMessageUser()
                    onDismiss()
                }
            )
            MenuOption(
                icon = Icons.Default.Star,
                title = "Save Post",
                subtitle = "Save for later",
                onClick = {
                    onSave()
                    onDismiss()
                }
            )
            MenuOption(
                icon = Icons.Default.Warning,
                title = "Report Post",
                subtitle = "Report inappropriate content",
                onClick = {
                    onReport()
                    onDismiss()
                }
            )
            MenuOption(
                icon = Icons.Default.Close,
                title = "Hide Post",
                subtitle = "See fewer posts like this",
                onClick = {
                    onHide()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun MenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Helper Functions
@Composable
private fun formatPostType(type: String): String {
    return type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}

@Composable
private fun getPostTypeColor(type: String): androidx.compose.ui.graphics.Color {
    return MaterialTheme.colorScheme.secondaryContainer
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000000 -> String.format("%.1fM", count / 1000000.0)
        count >= 1000 -> String.format("%.1fK", count / 1000.0)
        else -> count.toString()
    }
}
