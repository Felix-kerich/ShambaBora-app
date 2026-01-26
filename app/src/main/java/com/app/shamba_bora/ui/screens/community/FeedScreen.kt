package com.app.shamba_bora.ui.screens.community

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.Post
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.ui.components.CreatePostModal
import com.app.shamba_bora.ui.components.community.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onNavigateToPostDetail: (Long) -> Unit,
    onNavigateToMessages: (Long) -> Unit,
    onNavigateToProfile: (Long) -> Unit,
    onNavigateToConversation: (Long, String) -> Unit = { _, _ -> },
    showFAB: Boolean = true,
    showTopBar: Boolean = true,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()
    val commentsState by viewModel.commentsState.collectAsState()
    val context = LocalContext.current
    
    var showCreatePost by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    var showUserProfile by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf(0L) }
    var selectedUserName by remember { mutableStateOf("") }
    var showCommentSheet by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    // Show snackbar messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Community Feed",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadFeed() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (showFAB) {
                ExtendedFloatingActionButton(
                    onClick = { showCreatePost = true },
                    icon = { Icon(Icons.Default.Create, contentDescription = null) },
                    text = { Text("New Post") },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = feedState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }
            is Resource.Error -> {
                ErrorView(
                    message = state.message ?: "Failed to load feed",
                    onRetry = { viewModel.loadFeed() }
                )
            }
            is Resource.Success -> {
                val posts = state.data?.content ?: emptyList()
                
                if (posts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No posts yet",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Be the first to create a post!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = { showCreatePost = true }) {
                                Icon(Icons.Default.Create, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create Post")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 0.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(posts) { post ->
                            EnhancedPostCard(
                                post = post,
                                onLike = { viewModel.likePost(post.id ?: 0L) },
                                onUnlike = { viewModel.unlikePost(post.id ?: 0L) },
                                onComment = {
                                    selectedPost = post
                                    viewModel.loadComments(post.id ?: 0L)
                                    showCommentSheet = true
                                },
                                onShare = {
                                    selectedPost = post
                                    showShareSheet = true
                                },
                                onUserClick = { userId, userName ->
                                    selectedUserId = userId
                                    selectedUserName = userName
                                    showUserProfile = true
                                },
                                onPostClick = {
                                    onNavigateToPostDetail(post.id ?: 0L)
                                },
                                onMessageUser = { userId, userName ->
                                    // Navigate to conversation with this user
                                    onNavigateToConversation(userId, userName)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Create Post Modal
    if (showCreatePost) {
        CreatePostModal(
            onDismiss = { showCreatePost = false },
            onCreatePost = { post ->
                viewModel.createPost(post)
                showCreatePost = false
                snackbarMessage = "Post created successfully!"
            }
        )
    }

    // User Profile Modal
    if (showUserProfile) {
        UserProfileModal(
            userId = selectedUserId,
            userName = selectedUserName,
            userBio = "Passionate farmer from Kenya", // TODO: Get from API
            userLocation = "Kiambu, Kenya", // TODO: Get from API
            postCount = 15, // TODO: Get from API
            followerCount = 42, // TODO: Get from API
            isFollowing = false, // TODO: Get from API
            onDismiss = { showUserProfile = false },
            onSendMessage = { userId ->
                onNavigateToMessages(userId)
            },
            onViewProfile = { userId ->
                onNavigateToProfile(userId)
            },
            onFollow = { userId ->
                // TODO: Implement follow
                snackbarMessage = "Following $selectedUserName"
            },
            onUnfollow = { userId ->
                // TODO: Implement unfollow
                snackbarMessage = "Unfollowed $selectedUserName"
            }
        )
    }

    // Comment Sheet
    if (showCommentSheet && selectedPost != null) {
        val comments = when (val state = commentsState) {
            is Resource.Success -> state.data?.content ?: emptyList()
            else -> emptyList()
        }
        
        EnhancedCommentSheet(
            postId = selectedPost!!.id ?: 0L,
            comments = comments,
            isLoading = commentsState is Resource.Loading,
            onDismiss = { showCommentSheet = false },
            onAddComment = { content ->
                val comment = com.app.shamba_bora.data.model.PostComment(
                    postId = selectedPost!!.id ?: 0L,
                    content = content
                )
                viewModel.addComment(selectedPost!!.id ?: 0L, comment)
                snackbarMessage = "Comment added!"
            },
            onLikeComment = { commentId ->
                // TODO: Implement comment like
                snackbarMessage = "Comment liked!"
            },
            onReplyComment = { commentId, content ->
                // TODO: Implement comment reply
                snackbarMessage = "Reply added!"
            },
            onUserClick = { userId, userName ->
                selectedUserId = userId
                selectedUserName = userName
                showCommentSheet = false
                showUserProfile = true
            }
        )
    }

    // Share Sheet
    if (showShareSheet && selectedPost != null) {
        SharePostSheet(
            postId = selectedPost!!.id ?: 0L,
            postContent = selectedPost!!.content,
            onDismiss = { showShareSheet = false },
            onShareToGroup = { groupId ->
                // TODO: Implement share to group
                snackbarMessage = "Shared to group!"
            },
            onShareExternal = {
                sharePostExternal(context, selectedPost!!.content)
                snackbarMessage = "Opening share options..."
            },
            onCopyLink = {
                copyToClipboard(context, "https://shambabora.app/post/${selectedPost!!.id}")
            }
        )
    }
}

private fun sharePostExternal(context: Context, content: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, content)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, "Share post via"))
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Post Link", text)
    clipboard.setPrimaryClip(clip)
}