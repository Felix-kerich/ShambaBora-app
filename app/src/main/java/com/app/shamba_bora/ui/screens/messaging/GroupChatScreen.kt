package com.app.shamba_bora.ui.screens.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.Message
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.PreferenceManager
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.MessagingViewModel
import kotlinx.coroutines.launch

@Composable
fun GroupChatScreen(
    groupId: Long,
    groupName: String,
    onNavigateBack: () -> Unit,
    viewModel: MessagingViewModel = hiltViewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val messagesState by viewModel.groupMessagesState.collectAsState()
    val sendMessageState by viewModel.sendGroupMessageState.collectAsState()
    val currentUserId = PreferenceManager.getUserId()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(groupId) {
        viewModel.loadGroupMessages(groupId)
    }
    
    LaunchedEffect(messagesState) {
        if (messagesState is Resource.Success) {
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(groupName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = messagesState) {
                is Resource.Loading -> {
                    LoadingIndicator()
                }
                is Resource.Error -> {
                    ErrorView(
                        message = state.message ?: "Failed to load messages",
                        onRetry = { viewModel.refreshGroupMessages() }
                    )
                }
                is Resource.Success -> {
                    val messages = state.data?.content?.reversed() ?: emptyList()
                    
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true
                    ) {
                        items(messages) { message ->
                            GroupMessageBubble(
                                message = message,
                                isSentByMe = message.senderId == currentUserId
                            )
                        }
                    }
                    
                    // Message Input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
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
                                        messageText = ""
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = "Send",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            singleLine = false,
                            maxLines = 4
                        )
                    }
                    
                    // Show error if sending failed
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
}

@Composable
fun GroupMessageBubble(
    message: Message,
    isSentByMe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isSentByMe) {
            Spacer(modifier = Modifier.width(48.dp))
        }
        
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSentByMe) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(12.dp),
            horizontalAlignment = if (isSentByMe) Alignment.End else Alignment.Start
        ) {
            if (!isSentByMe) {
                Text(
                    text = "User ${message.senderId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSentByMe) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            message.createdAt?.let {
                Text(
                    text = formatMessageTime(it),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSentByMe) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        
        if (isSentByMe) {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

