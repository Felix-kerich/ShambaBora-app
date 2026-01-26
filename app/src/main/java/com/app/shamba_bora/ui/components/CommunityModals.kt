package com.app.shamba_bora.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.app.shamba_bora.data.model.Post
import com.app.shamba_bora.data.model.PostComment
import com.app.shamba_bora.utils.CloudinaryUploader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostModal(
    onDismiss: () -> Unit,
    onCreatePost: (Post) -> Unit,
    groupId: Long? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPostType by remember { mutableStateOf("GENERAL") }
    var expandedPostType by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isUploadingImage by remember { mutableStateOf(false) }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Upload to Cloudinary
            isUploadingImage = true
            scope.launch {
                val result = CloudinaryUploader.uploadImage(
                    context = context,
                    imageUri = it,
                    folder = if (groupId != null) "shamba_bora/groups" else "shamba_bora/community"
                )
                isUploadingImage = false
                result.onSuccess { url ->
                    imageUrl = url
                }.onFailure { error ->
                    errorMessage = "Failed to upload image: ${error.message}"
                    showError = true
                    selectedImageUri = null
                }
            }
        }
    }
    
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
                    postType = selectedPostType,
                    groupId = groupId
                )
                onCreatePost(post)
                onDismiss()
            }
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Create,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (groupId != null) "Create Group Post" else "Create Post",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Row {
                            TextButton(onClick = onDismiss) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { validateAndPost() },
                                enabled = content.isNotBlank() && !isUploadingImage
                            ) {
                                if (isUploadingImage) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(if (isUploadingImage) "Uploading..." else "Post")
                            }
                        }
                    }
                }
                
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                                    imageVector = Icons.Default.Info,
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
                            .height(300.dp),
                        minLines = 12,
                        maxLines = 20
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Image Selection Section
                    Text(
                        text = "Add Image",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Image Picker Button
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick Image")
                        }
                        
                        // Clear Image Button
                        if (selectedImageUri != null || imageUrl.isNotEmpty()) {
                            OutlinedButton(
                                onClick = {
                                    selectedImageUri = null
                                    imageUrl = ""
                                }
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Image URL Input (or display selected image info)
                    if (isUploadingImage) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Uploading image to cloud...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else if (selectedImageUri != null && imageUrl.isNotEmpty()) {
                        OutlinedTextField(
                            value = "Image uploaded successfully",
                            onValueChange = {},
                            label = { Text("Selected Image") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            leadingIcon = {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        )
                    } else {
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("Or paste Image URL") },
                            placeholder = { Text("https://example.com/image.jpg") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Star, contentDescription = null)
                            },
                            singleLine = true
                        )
                    }
                    
                    // Image Preview
                    if (selectedImageUri != null || imageUrl.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = selectedImageUri ?: imageUrl,
                                contentDescription = "Selected image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Guidelines
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Community Guidelines",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Be respectful and helpful\n• Share accurate information\n• No spam or promotional content",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddCommentModal(
    postId: Long,
    onDismiss: () -> Unit,
    onAddComment: (PostComment) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    
    fun validateAndComment() {
        when {
            commentText.isBlank() -> {
                showError = true
            }
            commentText.length < 3 -> {
                showError = true
            }
            else -> {
                val comment = PostComment(
                    postId = postId,
                    content = commentText
                )
                onAddComment(comment)
                onDismiss()
            }
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Comment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { 
                        commentText = it
                        showError = false
                    },
                    label = { Text("Your comment") },
                    placeholder = { Text("Share your thoughts...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    minLines = 5,
                    maxLines = 8,
                    isError = showError
                )
                
                if (showError) {
                    Text(
                        text = "Comment must be at least 3 characters",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { validateAndComment() },
                        enabled = commentText.isNotBlank()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Comment")
                    }
                }
            }
        }
    }
}
