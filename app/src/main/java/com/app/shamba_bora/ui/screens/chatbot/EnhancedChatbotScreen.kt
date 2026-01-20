package com.app.shamba_bora.ui.screens.chatbot

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.app.shamba_bora.ui.utils.renderMarkdown
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.ChatbotConversation
import com.app.shamba_bora.data.model.ChatbotConversationSummary
import com.app.shamba_bora.data.model.ChatbotMessage
import com.app.shamba_bora.data.model.FarmAdviceResponse
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.ChatbotViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedChatbotScreen(
    viewModel: ChatbotViewModel = hiltViewModel()
) {
    var showHistory by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var conversationToDelete by remember { mutableStateOf<String?>(null) }
    var isLoadingConversation by remember { mutableStateOf(false) }
    var lastPendingMessage by remember { mutableStateOf<String?>(null) }
    var showMessageError by remember { mutableStateOf(false) }
    var showFarmAdviceNotification by remember { mutableStateOf(false) }
    var showFarmAdviceReadyDialog by remember { mutableStateOf(false) }
    var farmAdviceReady by remember { mutableStateOf<FarmAdviceResponse?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveAdviceTitle by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    val conversations by viewModel.conversations.collectAsState()
    val currentConversation by viewModel.currentConversation.collectAsState()
    val currentConversationIdFlow by viewModel.currentConversationId.collectAsState()
    val queryResponse by viewModel.queryResponse.collectAsState()
    val farmAdvice by viewModel.farmAdvice.collectAsState()
    val farmAdviceBackgroundLoading by viewModel.farmAdviceBackgroundLoading.collectAsState()
    val showFarmAdviceNotificationFlow by viewModel.showFarmAdviceNotification.collectAsState()
    val pendingMessage by viewModel.pendingMessage.collectAsState()
    val useFarmContext by viewModel.useFarmContext.collectAsState()
    val farmContext by viewModel.farmContext.collectAsState()
    val farmContextLoading by viewModel.farmContextLoading.collectAsState()
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Track if message failed
    LaunchedEffect(queryResponse) {
        if (queryResponse is Resource.Error && pendingMessage != null) {
            lastPendingMessage = pendingMessage
            showMessageError = true
        } else if (queryResponse is Resource.Success) {
            showMessageError = false
            lastPendingMessage = null
            // Message succeeded - sidebar stays open for viewing conversation
        }
    }
    
    // Track farm advice notification
    LaunchedEffect(showFarmAdviceNotificationFlow) {
        if (showFarmAdviceNotificationFlow && farmAdvice is Resource.Success) {
            showFarmAdviceNotification = true
            farmAdviceReady = (farmAdvice as Resource.Success<FarmAdviceResponse>).data
        }
    }

    // Ensure that whenever farmAdvice becomes Success we open the ready dialog
    // This makes sure the response modal shows even if the loading dialog was open
    LaunchedEffect(farmAdvice) {
        if (farmAdvice is Resource.Success) {
            farmAdviceReady = (farmAdvice as Resource.Success<FarmAdviceResponse>).data
            // Show the full advice dialog immediately
            showFarmAdviceReadyDialog = true
            // hide the transient snackbar flag - dialog will present the content
            showFarmAdviceNotification = false
        }
    }
    
    // Track conversation loading state - keep sidebar open for viewing conversation
    LaunchedEffect(currentConversation) {
        if (isLoadingConversation && currentConversation is Resource.Success) {
            // Conversation loaded successfully - sidebar stays open for viewing
            isLoadingConversation = false
            Log.d("EnhancedChatbotScreen", "Conversation loaded - sidebar stays open")
        } else if (isLoadingConversation && currentConversation is Resource.Error) {
            // Error loading conversation, keep sidebar open
            isLoadingConversation = false
        }
    }
    
    // Auto-scroll when new messages arrive or pending message changes
    LaunchedEffect(currentConversation, pendingMessage) {
        if (currentConversation is Resource.Success) {
            val messages = (currentConversation as Resource.Success<ChatbotConversation>).data?.messages
            val totalItems = (messages?.size ?: 0) + if (pendingMessage != null) 1 else 0
            if (totalItems > 0) {
                listState.animateScrollToItem(totalItems - 1)
            }
        } else if (pendingMessage != null) {
            // If we have a pending message but no conversation yet, scroll to it
            listState.animateScrollToItem(0)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Main Chat Area
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            ChatHeader(
                title = (currentConversation as? Resource.Success)?.data?.title ?: "AI Assistant",
                onMenuClick = { showHistory = !showHistory },
                onNewChat = {
                    viewModel.startNewConversation()
                },
                onGetAdvice = { viewModel.getFarmAdvice() },
                useFarmContext = useFarmContext,
                onFarmContextToggle = { enabled ->
                    viewModel.toggleFarmContext(enabled)
                },
                farmContextLoading = farmContextLoading,
                farmContextReady = farmContext != null
            )
            
            // Messages
            Box(modifier = Modifier.weight(1f)) {
                when (val conv = currentConversation) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is Resource.Success -> {
                        // Show message list if there are messages OR if we have a pending message
                        if (!conv.data?.messages.isNullOrEmpty() || pendingMessage != null) {
                            MessageList(
                                messages = conv.data?.messages ?: emptyList(),
                                listState = listState,
                                pendingMessage = pendingMessage,
                                isLoading = queryResponse is Resource.Loading,
                                hasError = showMessageError,
                                onRetry = {
                                    if (lastPendingMessage != null) {
                                        showMessageError = false
                                        viewModel.askQuestion(lastPendingMessage!!)
                                    }
                                }
                            )
                        } else {
                            // Show welcome screen only if no messages and no pending message
                            WelcomeScreen(
                                onQuestionClick = { question ->
                                    viewModel.askQuestion(question)
                                }
                            )
                        }
                    }
                    is Resource.Error -> {
                        ErrorMessage(
                            message = conv.message ?: "Failed to load conversation",
                            onRetry = {
                                currentConversationIdFlow?.let { viewModel.loadConversation(it) }
                            }
                        )
                    }
                    null -> {
                        WelcomeScreen(
                            onQuestionClick = { question ->
                                viewModel.askQuestion(question)
                            }
                        )
                    }
                }
            }
            
            // Input Section
            ChatInput(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank()) {
                        viewModel.askQuestion(messageText)
                        messageText = ""
                    }
                },
                isLoading = queryResponse is Resource.Loading
            )
        }
        
        // Floating Sidebar - Conversation History
        AnimatedVisibility(
            visible = showHistory,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
        ) {
            Box(modifier = Modifier.fillMaxHeight()) {
                ConversationHistorySidebar(
                    conversations = conversations,
                    currentConversationId = currentConversationIdFlow,
                    isLoadingConversation = isLoadingConversation,
                    onConversationClick = { conversationId ->
                        isLoadingConversation = true
                        viewModel.loadConversation(conversationId)
                    },
                    onNewConversation = {
                        viewModel.startNewConversation()
                        showHistory = false
                    },
                    onDeleteConversation = { conversationId ->
                        conversationToDelete = conversationId
                        showDeleteDialog = true
                    },
                    onRefresh = { viewModel.loadConversations() }
                )
                
                // Close button for sidebar
                IconButton(
                    onClick = { showHistory = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close sidebar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Scrim when sidebar is visible - NO LONGER CLOSES SIDEBAR
        if (showHistory) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.32f))
                    // Removed: .clickable(enabled = showHistory) { showHistory = false }
                    // Now only the close button closes the sidebar
            )
        }
        
        // Snackbar for save success/error messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog && conversationToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Conversation") },
            text = { Text("Are you sure you want to delete this conversation? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteConversation(conversationToDelete!!)
                        showDeleteDialog = false
                        conversationToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Farm Advice Ready Dialog
    if (showFarmAdviceReadyDialog && farmAdviceReady != null) {
        EnhancedFarmAdviceDialog(
            advice = farmAdviceReady!!,
            onDismiss = {
                showFarmAdviceReadyDialog = false
                viewModel.clearFarmAdvice()
            },
            onSave = {
                showSaveDialog = true
            }
        )
    }
    
    // Save Advice Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSaveDialog = false
                saveAdviceTitle = ""
            },
            title = { Text("Save Farm Advice") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Give this advice a title so you can find it later:")
                    OutlinedTextField(
                        value = saveAdviceTitle,
                        onValueChange = { saveAdviceTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g., Spring 2024 Advice") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (saveAdviceTitle.isNotBlank()) {
                            viewModel.saveCurrentAdvice(
                                title = saveAdviceTitle,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Advice saved successfully!",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                    showSaveDialog = false
                                    saveAdviceTitle = ""
                                },
                                onError = { error ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = error,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                    showSaveDialog = false
                                    saveAdviceTitle = ""
                                }
                            )
                        }
                    },
                    enabled = saveAdviceTitle.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showSaveDialog = false
                    saveAdviceTitle = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Farm Advice Loading Dialog - with "Please Wait" message
    farmAdvice?.let { advice ->
            if (advice is Resource.Loading) {
            AlertDialog(
                onDismissRequest = {
                    // Allow user to close dialog and continue processing in background
                    viewModel.startFarmAdviceBackgroundPolling()
                    showFarmAdviceNotification = true
                    // keep farmAdvice state so background polling continues in ViewModel
                },
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Getting Farm Advice")
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        advice.message?.let { msg ->
                            Text(
                                msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } ?: run {
                            Text(
                                "Analyzing your farm data...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        // Show what we're doing
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            ProgressStep(text = "Retrieving farm data", isActive = true)
                            ProgressStep(text = "Analyzing conditions", isActive = true)
                            ProgressStep(text = "Generating recommendations", isActive = true)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Start background polling, clear the loading dialog, and show notification
                            viewModel.startFarmAdviceBackgroundPolling()
                            viewModel.clearFarmAdvice() // This closes the dialog
                            showFarmAdviceNotification = true
                        }
                    ) {
                        Text("Process in background")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { /* Keep processing */ }) {
                        Text("Keep waiting")
                    }
                }
            )
        }
    }
    
    // Farm Advice Error Dialog - with timeout message
    farmAdvice?.let { advice ->
        if (advice is Resource.Error && !farmAdviceBackgroundLoading) {
            AlertDialog(
                onDismissRequest = { viewModel.clearFarmAdvice() },
                title = { Text("Farm Advice") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            advice.message ?: "Failed to get farm advice",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        // Show helpful suggestions for common errors
                        when {
                            advice.message?.contains("Access denied", ignoreCase = true) == true || 
                            advice.message?.contains("Authentication", ignoreCase = true) == true -> {
                                Text(
                                    "Tip: Make sure you're logged in with a valid account. You may need to log out and log back in to refresh your session.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            advice.message?.contains("Farm analytics data not found", ignoreCase = true) == true -> {
                                Text(
                                    "Tip: Complete your farm profile by adding farm details, soil information, and crop data. This helps the AI provide better advice.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            advice.message?.contains("Server error", ignoreCase = true) == true -> {
                                Text(
                                    "Tip: The server is experiencing issues. Please try again in a few moments.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            advice.message?.contains("taking longer", ignoreCase = true) == true -> {
                                Text(
                                    "Your farm advice is being processed in the background. You'll be notified when it's ready!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearFarmAdvice() }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        viewModel.clearFarmAdvice()
                        viewModel.getFarmAdvice() // Retry
                    }) {
                        Text("Retry")
                    }
                }
            )
        }
    }
    
    // Farm Advice Ready Notification
    if (showFarmAdviceNotification && farmAdviceReady != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(
                    onClick = {
                        showFarmAdviceReadyDialog = true
                        showFarmAdviceNotification = false
                    }
                ) {
                    Text("View", color = MaterialTheme.colorScheme.primary)
                }
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text("Your farm advice is ready!")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(
    title: String,
    onMenuClick: () -> Unit,
    onNewChat: () -> Unit,
    onGetAdvice: () -> Unit,
    useFarmContext: Boolean = false,
    onFarmContextToggle: (Boolean) -> Unit = {},
    farmContextLoading: Boolean = false,
    farmContextReady: Boolean = false
) {
    var showContextInfo by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Main header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "AI",
                        modifier = Modifier.padding(10.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Online • Powered by RAG",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
                
                Button(
                    onClick = onGetAdvice,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Get Advice")
                }
                
                IconButton(onClick = onNewChat) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            // Farm Context Toggle Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📊 Share Farm Data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (farmContextLoading) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else if (useFarmContext && farmContextReady) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF4CAF50).copy(alpha = 0.3f))
                                .padding(4.dp),
                            color = Color.Transparent
                        ) {
                            Text(
                                text = "✓ Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = { showContextInfo = !showContextInfo },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                Checkbox(
                    checked = useFarmContext,
                    onCheckedChange = onFarmContextToggle,
                    modifier = Modifier.scale(0.8f),
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF4CAF50),
                        uncheckedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                )
            }
            
            // Context info expansion
            AnimatedVisibility(visible = showContextInfo) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    color = Color.Transparent
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "When enabled, the AI will have access to:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Your farm analytics and performance\n• All patches and their details\n• Recent activities and expenses\n• Yield records and revenue\n• Farmer profile and location",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationHistorySidebar(
    conversations: Resource<List<ChatbotConversationSummary>>,
    currentConversationId: String?,
    isLoadingConversation: Boolean = false,
    onConversationClick: (String) -> Unit,
    onNewConversation: () -> Unit,
    onDeleteConversation: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Conversations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onRefresh) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            // New Conversation Button
            Button(
                onClick = onNewConversation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Conversation")
            }
            
            HorizontalDivider()
            
            // Conversations List
            when (conversations) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    if (conversations.data.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No conversations yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(conversations.data) { conversation ->
                                ConversationItem(
                                    conversation = conversation,
                                    isSelected = conversation.conversationId == currentConversationId,
                                    isLoading = isLoadingConversation && conversation.conversationId == currentConversationId,
                                    onClick = { onConversationClick(conversation.conversationId) },
                                    onDelete = { onDeleteConversation(conversation.conversationId) }
                                )
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                conversations.message ?: "Error loading",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = onRefresh) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationItem(
    conversation: ChatbotConversationSummary,
    isSelected: Boolean,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick, enabled = !isLoading),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.title ?: "Untitled",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Show loading indicator when this conversation is being loaded
                    if (isLoading) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = if (isSelected) 
                                MaterialTheme.colorScheme.onPrimaryContainer 
                            else 
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = conversation.lastMessage ?: "No messages",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${conversation.messageCount} messages • ${formatDate(conversation.updatedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            
            Box {
                IconButton(onClick = { showMenu = true }, enabled = !isLoading) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MessageList(
    messages: List<ChatbotMessage>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    pendingMessage: String? = null,
    isLoading: Boolean = false,
    hasError: Boolean = false,
    onRetry: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        state = listState
    ) {
        items(messages) { message ->
            MessageBubble(message = message)
        }
        
        // Show pending user message if exists
        pendingMessage?.let { pending ->
            item {
                PendingMessageBubble(
                    message = pending,
                    hasError = hasError,
                    onRetry = onRetry
                )
            }
        }
        
        // Show AI loading indicator when waiting for response (but only if not showing error)
        if (isLoading && pendingMessage != null && !hasError) {
            item {
                LoadingResponseBubble()
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatbotMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.role == "user") Arrangement.End else Arrangement.Start
    ) {
        if (message.role == "assistant") {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "AI",
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (message.role == "user") 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.role == "user") 16.dp else 4.dp,
                    bottomEnd = if (message.role == "user") 4.dp else 16.dp
                )
            ) {
                Text(
                    text = renderMarkdown(message.content),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.role == "user")
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = formatTimestamp(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        
        if (message.role == "user") {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun PendingMessageBubble(message: String, hasError: Boolean = false, onRetry: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (hasError)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 4.dp
                )
            ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = renderMarkdown(message),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (hasError)
                                        MaterialTheme.colorScheme.onErrorContainer
                                    else
                                        MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (hasError) {
                            // Show retry icon
                            IconButton(
                                onClick = onRetry,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retry",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    
                    // Show error message if failed
                    if (hasError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Failed to send. Tap retry to resend.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = if (hasError) "Failed" else "Sending...",
                style = MaterialTheme.typography.labelSmall,
                color = if (hasError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User",
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun LoadingResponseBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "AI",
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 16.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "AI is thinking...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Generating response",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun WelcomeScreen(onQuestionClick: (String) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "AI",
                modifier = Modifier.padding(20.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Welcome to ShambaBora AI",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Ask me anything about maize farming!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Suggested Questions
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SuggestedQuestion(
                question = "What is the best soil pH for maize?",
                onClick = { onQuestionClick("What is the best soil pH for maize?") }
            )
            SuggestedQuestion(
                question = "When should I plant maize?",
                onClick = { onQuestionClick("When should I plant maize?") }
            )
            SuggestedQuestion(
                question = "How do I control pests in maize?",
                onClick = { onQuestionClick("How do I control pests in maize?") }
            )
        }
    }
}

@Composable
fun SuggestedQuestion(
    question: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = question,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChatInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about maize farming...") },
                trailingIcon = {
                    if (messageText.isNotEmpty()) {
                        IconButton(onClick = { onMessageChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            FloatingActionButton(
                onClick = onSend,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

// Utility functions
private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Recently"
    }
}

private fun formatTimestamp(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timestamp)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        ""
    }
}

@Composable
fun FarmAdviceDialog(
    advice: FarmAdviceResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Farm Advice",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Advice
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "General Advice",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (advice.advice != null) {
                                Text(
                                    text = advice.advice!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else {
                                Text(
                                    text = "No general advice available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
                
                // Fertilizer Recommendations
                if (advice.fertilizerRecommendations.isNotEmpty()) {
                    item {
                        AdviceSection(
                            title = "Fertilizer Recommendations",
                            items = advice.fertilizerRecommendations,
                            icon = Icons.Default.Build
                        )
                    }
                }
                
                // Seed Recommendations
                if (advice.seedRecommendations.isNotEmpty()) {
                    item {
                        AdviceSection(
                            title = "Seed Recommendations",
                            items = advice.seedRecommendations,
                            icon = Icons.Default.Info
                        )
                    }
                }
                
                // Prioritized Actions
                if (advice.prioritizedActions.isNotEmpty()) {
                    item {
                        AdviceSection(
                            title = "Priority Actions",
                            items = advice.prioritizedActions,
                            icon = Icons.Default.CheckCircle
                        )
                    }
                }
                
                // Risk Warnings
                if (advice.riskWarnings.isNotEmpty()) {
                    item {
                        AdviceSection(
                            title = "Risk Warnings",
                            items = advice.riskWarnings,
                            icon = Icons.Default.Warning,
                            isWarning = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AdviceSection(
    title: String,
    items: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isWarning: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isWarning) 
                MaterialTheme.colorScheme.errorContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isWarning) 
                        MaterialTheme.colorScheme.onErrorContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isWarning) 
                        MaterialTheme.colorScheme.onErrorContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        "• ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isWarning) 
                            MaterialTheme.colorScheme.onErrorContainer 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isWarning) 
                            MaterialTheme.colorScheme.onErrorContainer 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressStep(text: String, isActive: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isActive) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 1.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}
