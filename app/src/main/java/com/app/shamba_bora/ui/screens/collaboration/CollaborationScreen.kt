package com.app.shamba_bora.ui.screens.collaboration

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollaborationScreen(
    onNavigateToPostDetails: (Long) -> Unit,
    onNavigateToGroups: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToCreatePost: () -> Unit = {},
    onNavigateToUserSearch: () -> Unit = {},
    onNavigateToGroupDetail: (Long) -> Unit = {},
    viewModel: CommunityViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (selectedTab == 2) {
                        IconButton(onClick = onNavigateToUserSearch) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Search Users")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = onNavigateToCreatePost,
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
                    text = { Text("Feed") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Groups") },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Messages") },
                    icon = { Icon(Icons.Default.MailOutline, contentDescription = null) }
                )
            }
        
            // Content
            when (selectedTab) {
                0 -> FeedScreen(
                    onNavigateToPostDetails = onNavigateToPostDetails,
                    onNavigateToCreatePost = onNavigateToCreatePost,
                    viewModel = viewModel
                )
                1 -> GroupsScreen(
                    onNavigateToGroups = onNavigateToGroups,
                    onNavigateToGroupDetail = onNavigateToGroupDetail,
                    viewModel = viewModel
                )
                2 -> MessagesScreen(
                    onNavigateToMessages = onNavigateToMessages,
                    onNavigateToUserSearch = onNavigateToUserSearch,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun FeedScreen(
    onNavigateToPostDetails: (Long) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    viewModel: CommunityViewModel
) {
    val feedState by viewModel.feedState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Create Post Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToCreatePost
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar
                    Surface(
                        modifier = Modifier.size(48.dp),
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
                    Text(
                        text = "Share your farming experience...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Add Image",
                        tint = MaterialTheme.colorScheme.primary
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
                        message = state.message ?: "Failed to load feed",
                        onRetry = { viewModel.loadFeed() }
                    )
                }
            }
            is Resource.Success -> {
                val posts = state.data?.content ?: emptyList()
                if (posts.isEmpty()) {
                    item {
                        Text(
                            text = "No posts yet. Be the first to share!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(posts) { post ->
                        PostCard(
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
fun GroupsScreen(
    onNavigateToGroups: () -> Unit,
    onNavigateToGroupDetail: (Long) -> Unit,
    viewModel: CommunityViewModel
) {
    val groupsState by viewModel.groupsState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadMyGroups()
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Groups",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToGroups) {
                    Text("See All")
                }
            }
        }
        
        when (val state = groupsState) {
            is Resource.Loading -> {
                item {
                    LoadingIndicator()
                }
            }
            is Resource.Error -> {
                item {
                    ErrorView(
                        message = state.message ?: "Failed to load groups",
                        onRetry = { viewModel.loadMyGroups() }
                    )
                }
            }
            is Resource.Success -> {
                val groups = state.data ?: emptyList()
                if (groups.isEmpty()) {
                    item {
                        Text(
                            text = "You're not a member of any groups yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(groups) { group ->
                        GroupCard(
                            group = group, 
                            onClick = { onNavigateToGroupDetail(group.id ?: 0L) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessagesScreen(
    onNavigateToMessages: () -> Unit,
    onNavigateToUserSearch: () -> Unit,
    viewModel: CommunityViewModel
) {
    val conversationsState by viewModel.conversationsState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadRecentConversations()
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Conversations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToUserSearch) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Chat")
                }
            }
        }
        
        when (val state = conversationsState) {
            is Resource.Loading -> {
                item {
                    LoadingIndicator()
                }
            }
            is Resource.Error -> {
                item {
                    ErrorView(
                        message = state.message ?: "Failed to load conversations",
                        onRetry = { viewModel.loadRecentConversations() }
                    )
                }
            }
            is Resource.Success -> {
                val conversations = state.data?.content ?: emptyList()
                if (conversations.isEmpty()) {
                    item {
                        Text(
                            text = "No conversations yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(conversations) { message ->
                        ConversationCard(
                            message = message,
                            onClick = { onNavigateToMessages() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: com.app.shamba_bora.data.model.Post,
    onClick: () -> Unit,
    onLike: () -> Unit = {},
    onUnlike: () -> Unit = {}
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
            // Post Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
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
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Post Content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Post Image (if any)
            post.imageUrl?.let { imageUrl ->
                if (imageUrl.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Post Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    if (post.likedByCurrentUser == true) {
                        onUnlike()
                    } else {
                        onLike()
                    }
                }) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        tint = if (post.likedByCurrentUser == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = {}) {
                    Icon(Icons.Default.Email, contentDescription = "Comment", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.commentCount ?: 0}")
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        }
    }
}

@Composable
fun GroupCard(
    group: com.app.shamba_bora.data.model.Group,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${group.memberCount ?: 0} members",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ArrowForward, contentDescription = "Navigate")
        }
    }
}

@Composable
fun ConversationCard(
    message: com.app.shamba_bora.data.model.DirectMessage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.senderName ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = message.createdAt ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (message.status != "READ") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "1",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

