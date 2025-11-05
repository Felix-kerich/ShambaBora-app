package com.app.shamba_bora.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.Post
import com.app.shamba_bora.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onNavigateBack: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedPostType by remember { mutableStateOf("GENERAL") }
    var expandedPostType by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val postTypes = listOf(
        "GENERAL" to "General",
        "QUESTION" to "Question",
        "ADVICE" to "Advice",
        "SHARE_EXPERIENCE" to "Share Experience",
        "MARKET_UPDATE" to "Market Update",
        "WEATHER_ALERT" to "Weather Alert",
        "ANNOUNCEMENT" to "Announcement"
    )
    
    fun validateAndPost() {
        when {
            content.isBlank() -> {
                errorMessage = "Post content cannot be empty"
                showError = true
            }
            content.length < 10 -> {
                errorMessage = "Post content must be at least 10 characters"
                showError = true
            }
            else -> {
                val post = Post(
                    content = content,
                    imageUrl = imageUrl.ifBlank { null },
                    postType = selectedPostType
                )
                viewModel.createPost(post)
                onNavigateBack()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { validateAndPost() },
                        enabled = content.isNotBlank()
                    ) {
                        Text(
                            "Post",
                            fontWeight = FontWeight.Bold,
                            color = if (content.isNotBlank()) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (showError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Post Type Selector
            ExposedDropdownMenuBox(
                expanded = expandedPostType,
                onExpandedChange = { expandedPostType = it }
            ) {
                OutlinedTextField(
                    value = postTypes.find { it.first == selectedPostType }?.second ?: "General",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Post Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPostType) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    leadingIcon = {
                        Icon(
                            imageVector = when (selectedPostType) {
                                "QUESTION" -> Icons.Default.Build
                                "ADVICE" -> Icons.Default.Build
                                "SHARE_EXPERIENCE" -> Icons.Default.Share
                                "MARKET_UPDATE" -> Icons.Default.Build
                                "WEATHER_ALERT" -> Icons.Default.Build
                                "ANNOUNCEMENT" -> Icons.Default.MailOutline
                                else -> Icons.Default.MailOutline
                            },
                            contentDescription = null
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = expandedPostType,
                    onDismissRequest = { expandedPostType = false }
                ) {
                    postTypes.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                selectedPostType = value
                                expandedPostType = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (value) {
                                        "QUESTION" -> Icons.Default.Build
                                        "ADVICE" -> Icons.Default.Build
                                        "SHARE_EXPERIENCE" -> Icons.Default.Share
                                        "MARKET_UPDATE" -> Icons.Default.Build
                                        "WEATHER_ALERT" -> Icons.Default.Build
                                        "ANNOUNCEMENT" -> Icons.Default.Build
                                        else -> Icons.Default.Build
                                    },
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content Input
            OutlinedTextField(
                value = content,
                onValueChange = { 
                    content = it
                    showError = false
                },
                label = { Text("What's on your mind?") },
                placeholder = { Text("Share your farming experience, ask questions, or provide advice...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                minLines = 8,
                maxLines = 15
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Image URL Input
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL (Optional)") },
                placeholder = { Text("https://example.com/image.jpg") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Image, contentDescription = null)
                },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Post Guidelines Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Community Guidelines",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Be respectful and helpful\n" +
                                "• Share accurate information\n" +
                                "• No spam or promotional content\n" +
                                "• Keep discussions relevant to farming",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
