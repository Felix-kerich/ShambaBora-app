package com.app.shamba_bora.ui.screens.community

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.Group
import com.app.shamba_bora.data.model.Message
import com.app.shamba_bora.data.model.Post
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.ui.components.CreatePostModal
import com.app.shamba_bora.utils.PreferenceManager
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.CommunityViewModel
import com.app.shamba_bora.viewmodel.MessagingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToPostDetails: (Long) -> Unit,
    communityViewModel: CommunityViewModel = hiltViewModel(),
    messagingViewModel: MessagingViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    var messageText by remember { mutableStateOf("") }
    var showCreatePostModal by remember { mutableStateOf(false) }
    val currentUserId = PreferenceManager.getUserId()
    
    // Load group data
    val groupsState by communityViewModel.groupsState.collectAsState()
    val groupPostsState by communityViewModel.feedState.collectAsState()
    val groupMessagesState by messagingViewModel.groupMessagesState.collectAsState()
    
    // Find the specific group
    val group = when (val state = groupsState) {
        is Resource.Success -> state.data?.find { it.id == groupId }
        else -> null
    }
    
    LaunchedEffect(groupId) {
        communityViewModel.loadMyGroups()
        messagingViewModel.loadGroupMessages(groupId)
        // In real app: communityViewModel.loadGroupPosts(groupId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(group?.name ?: "Group")
                        Text(
                            text = "${group?.memberCount ?: 0} members",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Group settings */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showCreatePostModal = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Post")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Posts") },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Chat") },
                    icon = { Icon(Icons.Default.MailOutline, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Members") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
            
            // Content
            when (selectedTab) {
                0 -> GroupPostsTab(
                    groupId = groupId,
                    onNavigateToPostDetails = onNavigateToPostDetails,
                    viewModel = communityViewModel
                )
                1 -> GroupChatTab(
                    groupId = groupId,
                    messageText = messageText,
                    onMessageTextChange = { messageText = it },
                    currentUserId = currentUserId,
                    viewModel = messagingViewModel
                )
                2 -> GroupMembersTab(
                    group = group
                )
            }
        }
        
        // Create Post Modal for Group
        if (showCreatePostModal) {
            CreatePostModal(
                onDismiss = { showCreatePostModal = false },
                onCreatePost = { post ->
                    communityViewModel.createPost(post)
                },
                groupId = groupId
            )
        }
    }
}

@Composable
fun GroupPostsTab(
    groupId: Long,
    onNavigateToPostDetails: (Long) -> Unit,
    viewModel: CommunityViewModel
) {
    val feedState by viewModel.feedState.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Group Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Share updates, ask questions, and collaborate with group members",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        // Posts
        when (val state = feedState) {
            is Resource.Loading -> {
                item {
                    LoadingIndicator()
                }
            }
            is Resource.Error -> {
                item {
                    ErrorView(
                        message = state.message ?: "Failed to load posts",
                        onRetry = { viewModel.loadFeed() }
                    )
                }
            }
            is Resource.Success -> {
                val posts = state.data?.content?.filter { it.groupId == groupId } ?: emptyList()
                if (posts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MailOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No posts yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Be the first to post in this group!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(posts) { post ->
                        GroupPostCard(
                            post = post,
                            onClick = { onNavigateToPostDetails(post.id ?: 0L) },
                            onLike = { viewModel.likePost(post.id ?: 0L) },
                            onUnlike = { viewModel.unlikePost(post.id ?: 0L) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupChatTab(
    groupId: Long,
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    currentUserId: Long,
    viewModel: MessagingViewModel
) {
    val messagesState by viewModel.groupMessagesState.collectAsState()
    val sendMessageState by viewModel.sendGroupMessageState.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = messagesState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorView(
                        message = state.message ?: "Failed to load messages",
                        onRetry = { viewModel.refreshGroupMessages() }
                    )
                }
            }
            is Resource.Success -> {
                val messages = state.data?.content?.reversed() ?: emptyList()
                
                if (messages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No messages yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Start the conversation!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true
                    ) {
                        items(messages) { message ->
                            GroupMessageCard(
                                message = message,
                                isSentByMe = message.senderId == currentUserId
                            )
                        }
                    }
                }
                
                // Message Input
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = onMessageTextChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        trailingIcon = {
                            if (messageText.isNotBlank()) {
                                IconButton(onClick = {
                                    val message = Message(
                                        groupId = groupId,
                                        senderId = currentUserId,
                                        content = messageText
                                    )
                                    viewModel.sendGroupMessage(message)
                                    onMessageTextChange("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        maxLines = 3
                    )
                }
                
                if (sendMessageState is Resource.Error) {
                    Text(
                        text = sendMessageState.message ?: "Failed to send message",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GroupMembersTab(
    group: Group?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Members (${group?.memberCount ?: 0})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Sample members - in real app, load from API
        items(5) { index ->
            MemberCard(
                name = "Member ${index + 1}",
                role = if (index == 0) "Admin" else "Member"
            )
        }
    }
}

@Composable
fun GroupPostCard(
    post: Post,
    onClick: () -> Unit,
    onLike: () -> Unit,
    onUnlike: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Author
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = post.authorName ?: "Unknown",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = post.createdAt ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "Likes",
                        modifier = Modifier.size(16.dp),
                        tint = if (post.likedByCurrentUser == true) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.likeCount ?: 0}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MailOutline,
                        contentDescription = "Comments",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.commentCount ?: 0}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun GroupMessageCard(
    message: Message,
    isSentByMe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isSentByMe) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.sentAt ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MemberCard(
    name: String,
    role: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (role == "Admin") {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "ADMIN",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
