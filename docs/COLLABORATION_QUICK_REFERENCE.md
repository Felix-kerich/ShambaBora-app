# 🚀 Quick Reference: Enhanced Collaboration Features

## New Components at a Glance

### 1. UserProfileModal
```kotlin
UserProfileModal(
    userId = userId,
    userName = "John Farmer",
    userBio = "Coffee farmer from Kiambu",
    userLocation = "Kiambu, Kenya",
    postCount = 25,
    followerCount = 150,
    isFollowing = false,
    onDismiss = { },
    onSendMessage = { userId -> navigateToChat(userId) },
    onViewProfile = { userId -> navigateToProfile(userId) },
    onFollow = { userId -> followUser(userId) },
    onUnfollow = { userId -> unfollowUser(userId) }
)
```

### 2. EnhancedPostCard
```kotlin
EnhancedPostCard(
    post = post,
    onLike = { likePost(post.id) },
    onUnlike = { unlikePost(post.id) },
    onComment = { openComments(post.id) },
    onShare = { openShareSheet(post.id) },
    onUserClick = { userId, userName -> showProfile(userId, userName) },
    onPostClick = { navigateToDetail(post.id) }
)
```

### 3. EnhancedCommentSheet
```kotlin
EnhancedCommentSheet(
    postId = postId,
    comments = commentsList,
    isLoading = false,
    onDismiss = { },
    onAddComment = { content -> addComment(content) },
    onLikeComment = { commentId -> likeComment(commentId) },
    onReplyComment = { commentId, content -> replyComment(commentId, content) },
    onUserClick = { userId, userName -> showProfile(userId, userName) }
)
```

### 4. SharePostSheet
```kotlin
SharePostSheet(
    postId = postId,
    postContent = post.content,
    onDismiss = { },
    onShareToGroup = { groupId -> shareToGroup(postId, groupId) },
    onShareExternal = { shareExternal(post.content) },
    onCopyLink = { copyToClipboard(postUrl) }
)
```

---

## Common Patterns

### Show User Profile Modal
```kotlin
// 1. Declare state
var showUserProfile by remember { mutableStateOf(false) }
var selectedUserId by remember { mutableStateOf(0L) }
var selectedUserName by remember { mutableStateOf("") }

// 2. In your UI (on avatar/name click)
onUserClick = { userId, userName ->
    selectedUserId = userId
    selectedUserName = userName
    showUserProfile = true
}

// 3. Show modal
if (showUserProfile) {
    UserProfileModal(
        userId = selectedUserId,
        userName = selectedUserName,
        // ... other props
        onDismiss = { showUserProfile = false },
        onSendMessage = { userId ->
            onNavigateToMessages(userId)
            showUserProfile = false
        }
    )
}
```

### Open Comment Sheet
```kotlin
// 1. Declare state
var showComments by remember { mutableStateOf(false) }
var selectedPost by remember { mutableStateOf<Post?>(null) }

// 2. On comment button click
onComment = {
    selectedPost = post
    viewModel.loadComments(post.id)
    showComments = true
}

// 3. Show sheet
if (showComments && selectedPost != null) {
    EnhancedCommentSheet(
        postId = selectedPost!!.id,
        comments = commentsList,
        // ... other props
        onDismiss = { showComments = false }
    )
}
```

### Share Post
```kotlin
// 1. Declare state
var showShare by remember { mutableStateOf(false) }

// 2. On share button
onShare = {
    selectedPost = post
    showShare = true
}

// 3. Show sheet
if (showShare && selectedPost != null) {
    SharePostSheet(
        postId = selectedPost!!.id,
        postContent = selectedPost!!.content,
        onDismiss = { showShare = false },
        onShareExternal = {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, selectedPost!!.content)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(intent, "Share"))
        },
        onCopyLink = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Link", "https://app/post/${selectedPost!!.id}")
            clipboard.setPrimaryClip(clip)
        }
    )
}
```

---

## API Integration

### Required ViewModel Methods

```kotlin
class CommunityViewModel : ViewModel() {
    
    // Posts
    fun loadFeed()
    fun createPost(post: Post)
    fun likePost(postId: Long)
    fun unlikePost(postId: Long)
    
    // Comments
    fun loadComments(postId: Long)
    fun addComment(postId: Long, comment: PostComment)
    fun likeComment(commentId: Long)
    fun replyComment(commentId: Long, content: String)
    
    // Users (TODO: Implement)
    fun followUser(userId: Long)
    fun unfollowUser(userId: Long)
    fun getUserProfile(userId: Long)
    
    // Share (TODO: Implement)
    fun shareToGroup(postId: Long, groupId: Long)
}
```

---

## State Management

### Feed Screen State
```kotlin
@Composable
fun FeedScreen(viewModel: CommunityViewModel) {
    // Post states
    val feedState by viewModel.feedState.collectAsState()
    val commentsState by viewModel.commentsState.collectAsState()
    
    // UI states
    var showCreatePost by remember { mutableStateOf(false) }
    var showUserProfile by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }
    var showShare by remember { mutableStateOf(false) }
    
    // Selection states
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    var selectedUserId by remember { mutableStateOf(0L) }
    var selectedUserName by remember { mutableStateOf("") }
    
    // Feedback
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
}
```

---

## Navigation Setup

### Required Navigation Routes

```kotlin
// In your navigation graph
composable("feed") {
    FeedScreen(
        onNavigateToPostDetail = { postId ->
            navController.navigate("post/$postId")
        },
        onNavigateToMessages = { userId ->
            navController.navigate("messages/$userId")
        },
        onNavigateToProfile = { userId ->
            navController.navigate("profile/$userId")
        }
    )
}

composable("post/{postId}") {
    val postId = it.arguments?.getString("postId")?.toLong() ?: 0L
    PostDetailScreen(postId = postId, ...)
}

composable("messages/{userId}") {
    val userId = it.arguments?.getString("userId")?.toLong() ?: 0L
    MessagesScreen(userId = userId, ...)
}

composable("profile/{userId}") {
    val userId = it.arguments?.getString("userId")?.toLong() ?: 0L
    ProfileScreen(userId = userId, ...)
}
```

---

## Styling Tips

### Material 3 Colors
```kotlin
// Primary actions (Like, Follow, Send)
MaterialTheme.colorScheme.primary

// Secondary info (badges, labels)
MaterialTheme.colorScheme.secondaryContainer

// Surfaces (cards, sheets)
MaterialTheme.colorScheme.surface

// Containers (grouped content)
MaterialTheme.colorScheme.surfaceVariant
```

### Common Modifiers
```kotlin
// Card elevation
CardDefaults.cardElevation(defaultElevation = 2.dp)

// Rounded corners
shape = MaterialTheme.shapes.large // or medium, small

// Padding
Modifier.padding(16.dp) // Standard
Modifier.padding(horizontal = 24.dp, vertical = 12.dp) // Custom

// Clickable with ripple
Modifier.clickable { /* action */ }

// Fill width with max
Modifier.fillMaxWidth(0.95f)
```

---

## Error Handling

### With Snackbar
```kotlin
var snackbarMessage by remember { mutableStateOf<String?>(null) }
val snackbarHostState = remember { SnackbarHostState() }

LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let {
        snackbarHostState.showSnackbar(it)
        snackbarMessage = null
    }
}

// Usage
onLike = {
    try {
        viewModel.likePost(postId)
        snackbarMessage = "Post liked!"
    } catch (e: Exception) {
        snackbarMessage = "Error: ${e.message}"
    }
}
```

### With Resource State
```kotlin
when (val state = feedState) {
    is Resource.Loading -> LoadingIndicator()
    is Resource.Error -> ErrorView(
        message = state.message ?: "Error",
        onRetry = { viewModel.loadFeed() }
    )
    is Resource.Success -> {
        val posts = state.data?.content ?: emptyList()
        // Show content
    }
}
```

---

## Testing

### Component Testing
```kotlin
@Test
fun userProfileModal_showsCorrectInfo() {
    composeTestRule.setContent {
        UserProfileModal(
            userId = 1,
            userName = "Test User",
            userLocation = "Test Location",
            postCount = 10,
            followerCount = 50,
            // ...
        )
    }
    
    composeTestRule
        .onNodeWithText("Test User")
        .assertExists()
        
    composeTestRule
        .onNodeWithText("Test Location")
        .assertExists()
}
```

---

## Performance Tips

1. **Use remember for stable references**
```kotlin
val modalState = remember { mutableStateOf(false) }
```

2. **Lazy load comments**
```kotlin
LaunchedEffect(postId) {
    viewModel.loadComments(postId)
}
```

3. **Use keys in LazyColumn**
```kotlin
items(posts, key = { it.id }) { post ->
    EnhancedPostCard(post = post, ...)
}
```

4. **Optimize images**
```kotlin
AsyncImage(
    model = ImageRequest.Builder(context)
        .data(imageUrl)
        .crossfade(true)
        .build(),
    // ...
)
```

---

## Checklist for Implementation

- [ ] Add new component files
- [ ] Update Feed Screen
- [ ] Add navigation routes
- [ ] Implement ViewModel methods
- [ ] Add API endpoints
- [ ] Test all interactions
- [ ] Add error handling
- [ ] Test on different devices
- [ ] Check accessibility
- [ ] Performance testing

---

## Quick Wins

**5-Minute Additions:**
- Add snackbar feedback
- Enable ripple effects
- Add loading states

**30-Minute Additions:**
- Implement user profile modal
- Add share sheet
- Enhanced post cards

**2-Hour Additions:**
- Full comment system
- Reply functionality
- Complete navigation

---

**Happy Coding! 🎉**
