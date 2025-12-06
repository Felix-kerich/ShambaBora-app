package com.app.shamba_bora.ui.screens.chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.app.shamba_bora.data.model.ChatbotQueryRequest
import com.app.shamba_bora.data.model.ChatbotQueryResponse
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.data.network.LocalDateAdapter
import com.app.shamba_bora.data.network.LocalDateTimeAdapter
import com.app.shamba_bora.utils.Constants
import com.app.shamba_bora.utils.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Composable
fun ChatbotScreen() {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    
    // Initialize with welcome message
    LaunchedEffect(Unit) {
        messages.add(ChatMessage("bot", "Hello! I'm your ShambaBora AI assistant. How can I help you with your maize farming today?"))
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chat Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 4.dp
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
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "AI Assistant",
                        modifier = Modifier.padding(12.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI Assistant",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Online",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = listState
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
        }
        
        // Input Section
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
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type your message...") },
                    trailingIcon = {
                        if (messageText.isNotEmpty()) {
                            IconButton(onClick = { messageText = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.width(8.dp))
                val coroutineScope = rememberCoroutineScope()
                var lastSentMessage by remember { mutableStateOf<String?>(null) }
                
                // Handle bot response when lastSentMessage changes
                LaunchedEffect(lastSentMessage) {
                    lastSentMessage?.let { message ->
                        if (message.isNotBlank()) {
                            // Show typing indicator
                            messages.add(ChatMessage("bot", "Typing...", isTyping = true))
                            listState.animateScrollToItem(messages.size - 1)
                            
                            try {
                                // Create Gson instance with date adapters for proper date serialization
                                val gson = GsonBuilder()
                                    .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                                    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                                    .setLenient()
                                    .create()
                                
                                // Create chatbot API service
                                val loggingInterceptor = HttpLoggingInterceptor().apply {
                                    level = HttpLoggingInterceptor.Level.BODY
                                }
                                val okHttpClient = OkHttpClient.Builder()
                                    .addInterceptor(loggingInterceptor)
                                    .connectTimeout(30, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS)
                                    .writeTimeout(30, TimeUnit.SECONDS)
                                    .build()
                                    
                                val retrofit = Retrofit.Builder()
                                    .baseUrl(Constants.CHATBOT_BASE_URL)
                                    .client(okHttpClient)
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build()
                                    
                                val chatbotApi = retrofit.create(ApiService::class.java)
                                
                                // Get real farmer details
                                val farmerId = PreferenceManager.getUserId()
                                
                                // Call the API
                                val response = chatbotApi.queryFarmingQuestion(
                                    ChatbotQueryRequest(
                                        question = message,
                                        conversationId = "temp-${System.currentTimeMillis()}",
                                        farmerId = farmerId
                                    )
                                )
                                
                                // Remove typing indicator
                                messages.removeAt(messages.size - 1)
                                
                                if (response.isSuccessful && response.body() != null) {
                                    val answer = response.body()!!.response ?: ""
                                    messages.add(ChatMessage("bot", answer))
                                } else {
                                    messages.add(ChatMessage("bot", "Sorry, I couldn't process your question. Please try again."))
                                }
                            } catch (e: Exception) {
                                // Remove typing indicator
                                if (messages.isNotEmpty() && messages.last().isTyping) {
                                    messages.removeAt(messages.size - 1)
                                }
                                messages.add(ChatMessage("bot", "Error: ${e.message ?: "Unable to connect to the chatbot service."}"))
                            }
                            
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                }
                
                FloatingActionButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            val userMessage = messageText
                            messages.add(ChatMessage("user", userMessage))
                            lastSentMessage = userMessage
                            messageText = ""
                            // Scroll to show the user's message
                            coroutineScope.launch {
                                listState.scrollToItem(messages.size - 1)
                            }
                        }
                    },
                    modifier = Modifier.size(56.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.sender == "user") Arrangement.End else Arrangement.Start
    ) {
        if (message.sender == "bot") {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Bot",
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.sender == "user") 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.sender == "user") 16.dp else 4.dp,
                bottomEnd = if (message.sender == "user") 4.dp else 16.dp
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.sender == "user") 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (message.sender == "user") {
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

data class ChatMessage(
    val sender: String, // "user" or "bot"
    val text: String,
    val isTyping: Boolean = false
)
