package com.app.shamba_bora.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.data.network.AuthInterceptor
import com.app.shamba_bora.data.network.LocalDateAdapter
import com.app.shamba_bora.data.network.LocalDateTimeAdapter
import com.app.shamba_bora.utils.Constants
import com.app.shamba_bora.utils.PreferenceManager
import com.app.shamba_bora.utils.Resource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor() : ViewModel() {
    
    private val _conversations = MutableStateFlow<Resource<List<ChatbotConversationSummary>>>(Resource.Loading())
    val conversations: StateFlow<Resource<List<ChatbotConversationSummary>>> = _conversations.asStateFlow()
    
    private val _currentConversation = MutableStateFlow<Resource<ChatbotConversation>?>(null)
    val currentConversation: StateFlow<Resource<ChatbotConversation>?> = _currentConversation.asStateFlow()
    
    private val _currentConversationId = MutableStateFlow<String?>(null)
    val currentConversationId: StateFlow<String?> = _currentConversationId.asStateFlow()
    
    private val _queryResponse = MutableStateFlow<Resource<ChatbotQueryResponse>?>(null)
    val queryResponse: StateFlow<Resource<ChatbotQueryResponse>?> = _queryResponse.asStateFlow()
    
    private val _farmAdvice = MutableStateFlow<Resource<FarmAdviceResponse>?>(null)
    val farmAdvice: StateFlow<Resource<FarmAdviceResponse>?> = _farmAdvice.asStateFlow()
    
    private val _chatHistory = MutableStateFlow<Resource<List<ChatHistory>>>(Resource.Loading())
    val chatHistory: StateFlow<Resource<List<ChatHistory>>> = _chatHistory.asStateFlow()
    
    private val _farmAdviceBackgroundLoading = MutableStateFlow(false)
    val farmAdviceBackgroundLoading: StateFlow<Boolean> = _farmAdviceBackgroundLoading.asStateFlow()
    
    private val _showFarmAdviceNotification = MutableStateFlow(false)
    val showFarmAdviceNotification: StateFlow<Boolean> = _showFarmAdviceNotification.asStateFlow()
    
    // Background polling job for long-running farm advice requests
    private var _farmAdviceBackgroundJob: Job? = null
    private val _farmAdviceBackgroundJobActive = MutableStateFlow(false)
    val farmAdviceBackgroundJobActive: StateFlow<Boolean> = _farmAdviceBackgroundJobActive.asStateFlow()
    
    private val _pendingMessage = MutableStateFlow<String?>(null)
    val pendingMessage: StateFlow<String?> = _pendingMessage.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var currentSessionId: String = ""
    private var currentFarmerId: Long = 0
    
    private val chatbotApi: ApiService
    private val mainApi: ApiService
    
    init {
        // Create Gson instance with date adapters for proper date serialization
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()
        
        // Create dedicated Retrofit instance for chatbot service
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val chatbotOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .build()
        
        // Chatbot API (RAG service)
        val chatbotRetrofit = Retrofit.Builder()
            .baseUrl(Constants.CHATBOT_BASE_URL)
            .client(chatbotOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        
        chatbotApi = chatbotRetrofit.create(ApiService::class.java)
        
        // Main API (Spring Boot backend) - with AuthInterceptor for token
        val authInterceptor = AuthInterceptor()
        val mainOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .build()
        
        val mainRetrofit = Retrofit.Builder()
            .baseUrl("${Constants.BASE_URL}${Constants.API_PREFIX}/")
            .client(mainOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        
        mainApi = mainRetrofit.create(ApiService::class.java)
        
        // Initialize session
        initializeSession()
        
        // Load conversations on init
        loadConversations()
    }
    
    private fun initializeSession() {
        currentFarmerId = PreferenceManager.getUserId().toLong()
        currentSessionId = "session-${System.currentTimeMillis()}-${(0..9999).random()}"
    }
    
    // ============= CONVERSATION MANAGEMENT =============
    
    /**
     * Load all conversations for the current farmer
     */
    fun loadConversations() {
        viewModelScope.launch {
            _conversations.value = Resource.Loading()
            try {
                val farmerId = PreferenceManager.getUserId().toLong()
                val response = chatbotApi.getFarmerConversations(farmerId, limit = 50, offset = 0)
                
                if (response.isSuccessful && response.body() != null) {
                    val wrapper = response.body()!!
                    val convList = wrapper.getConversations()
                    _conversations.value = Resource.Success(convList)
                    Log.d("ChatbotViewModel", "Loaded ${convList.size} conversations")
                } else {
                    _conversations.value = Resource.Error("Failed to load conversations: ${response.message()}")
                }
            } catch (e: Exception) {
                _conversations.value = Resource.Error("Error: ${e.message ?: "Unknown error"}")
                Log.e("ChatbotViewModel", "Exception loading conversations", e)
            }
        }
    }
    
    /**
     * Create a new conversation
     */
    fun createNewConversation(title: String? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val farmerId = PreferenceManager.getUserId().toLong()
                
                // Create conversation on backend
                val response = chatbotApi.createConversation(
                    CreateConversationRequest(
                        title = title ?: "Conversation ${System.currentTimeMillis()}",
                        description = null,
                        farmerId = farmerId
                    )
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val conversation = response.body()!!
                    _currentConversationId.value = conversation.conversationId
                    _currentConversation.value = Resource.Success(conversation)
                    
                    // Refresh conversation list
                    loadConversations()
                    
                    Log.d("ChatbotViewModel", "Created conversation: ${conversation.conversationId}")
                } else {
                    _currentConversation.value = Resource.Error("Failed to create conversation")
                    Log.e("ChatbotViewModel", "Failed to create: ${response.message()}")
                }
            } catch (e: Exception) {
                _currentConversation.value = Resource.Error("Error: ${e.message}")
                Log.e("ChatbotViewModel", "Exception creating conversation", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load a specific conversation with all its messages
     */
    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            _currentConversation.value = Resource.Loading()
            _currentConversationId.value = conversationId
            try {
                val response = chatbotApi.getConversation(conversationId)
                
                if (response.isSuccessful && response.body() != null) {
                    val detailsResponse = response.body()!!
                    
                    // Convert backend response to ChatbotConversation format
                    if (detailsResponse.data != null && detailsResponse.data.session != null) {
                        val session = detailsResponse.data.session
                        val messages = detailsResponse.data.messages?.map { msg ->
                            // Convert ConversationMessage to ChatbotMessage format
                            ChatbotMessage(
                                role = "user",
                                content = msg.userMessage,
                                timestamp = msg.createdAt ?: "",
                                contexts = null
                            )
                        }?.toMutableList() ?: mutableListOf()
                        
                        // Also add AI responses as separate messages
                        detailsResponse.data.messages?.forEach { msg ->
                            messages.add(
                                ChatbotMessage(
                                    role = "assistant",
                                    content = msg.aiResponse,
                                    timestamp = msg.createdAt ?: "",
                                    contexts = null
                                )
                            )
                        }
                        
                        val conversation = ChatbotConversation(
                            conversationId = session.conversationId,
                            userId = session.farmerId?.toString() ?: "",
                            title = session.title,
                            messages = messages,
                            createdAt = session.createdAt,
                            updatedAt = session.updatedAt,
                            metadata = null
                        )
                        
                        _currentConversation.value = Resource.Success(conversation)
                        val messageCount = messages.size
                        Log.d("ChatbotViewModel", "Loaded conversation with $messageCount messages")
                    } else {
                        _currentConversation.value = Resource.Error("No conversation data received")
                        Log.e("ChatbotViewModel", "Empty response data")
                    }
                } else {
                    _currentConversation.value = Resource.Error("Failed to load conversation: ${response.message()}")
                    Log.e("ChatbotViewModel", "Failed to load: ${response.message()}")
                }
            } catch (e: Exception) {
                _currentConversation.value = Resource.Error("Error: ${e.message}")
                Log.e("ChatbotViewModel", "Exception loading conversation", e)
            }
        }
    }
    
    /**
     * Start a new conversation (clears current state)
     */
    fun startNewConversation() {
        Log.d("ChatbotViewModel", "startNewConversation() called - clearing previous conversation")
        _currentConversationId.value = null
        _currentConversation.value = null
        _queryResponse.value = null
        _pendingMessage.value = null
        createNewConversation()
    }
    
    // ============= QUERY & MESSAGING =============
    
    /**
     * Ask a question in the current or new conversation
     */
    fun askQuestion(question: String) {
        viewModelScope.launch {
            _pendingMessage.value = question
            _queryResponse.value = Resource.Loading()
            
            try {
                val farmerId = PreferenceManager.getUserId().toLong()
                val currentConvId = _currentConversationId.value
                
                Log.d("ChatbotViewModel", "askQuestion() - Current conversation ID: $currentConvId")
                
                // If no conversation, create one first
                val conversationId = if (currentConvId == null) {
                    Log.d("ChatbotViewModel", "No active conversation - creating new one")
                    val farmerId = PreferenceManager.getUserId().toLong()
                    val createResponse = chatbotApi.createConversation(
                        CreateConversationRequest(
                            title = "Chat ${System.currentTimeMillis()}",
                            description = null,
                            farmerId = farmerId
                        )
                    )
                    
                    if (createResponse.isSuccessful && createResponse.body() != null) {
                        val newConv = createResponse.body()!!
                        _currentConversationId.value = newConv.conversationId
                        _currentConversation.value = Resource.Success(newConv)
                        Log.d("ChatbotViewModel", "New conversation created: ${newConv.conversationId}")
                        newConv.conversationId
                    } else {
                        val errorBody = createResponse.errorBody()?.string() ?: "Empty response"
                        Log.e("ChatbotViewModel", "Create conversation failed: ${createResponse.code()} - ${createResponse.message()}")
                        Log.e("ChatbotViewModel", "Error body: $errorBody")
                        throw Exception("Failed to create conversation: ${createResponse.code()} - ${createResponse.message()}")
                    }
                } else {
                    Log.d("ChatbotViewModel", "Reusing existing conversation: $currentConvId")
                    currentConvId
                }
                
                // Send query with conversation ID
                val request = ChatbotQueryRequest(
                    question = question,
                    k = 4,
                    conversationId = conversationId,
                    userId = PreferenceManager.getUserId().toString(),
                    farmerId = farmerId,
                    includeFarmerData = true,
                    sessionId = currentSessionId
                )
                
                Log.d("ChatbotViewModel", "Sending question with conversation ID: $conversationId")
                val response = chatbotApi.queryFarmingQuestion(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _queryResponse.value = Resource.Success(result)
                    _pendingMessage.value = null
                    
                    // Ensure conversation ID is persisted from response if different
                    if (result.conversationId.isNotEmpty()) {
                        _currentConversationId.value = result.conversationId
                        Log.d("ChatbotViewModel", "Updated conversation ID from response: ${result.conversationId}")
                    }
                    
                    // Reload conversation to get the new message
                    loadConversation(conversationId)
                    loadConversations() // Refresh list
                    
                    Log.d("ChatbotViewModel", "Question answered, conversation: $conversationId")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message() ?: "Unknown error"
                    _queryResponse.value = Resource.Error("Failed: ${response.code()} - ${response.message()}")
                    _pendingMessage.value = null
                    Log.e("ChatbotViewModel", "Query failed: $errorMsg")
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                    e is java.io.IOException && e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Connection timeout. Processing in background..."
                    else -> e.message ?: "Unknown error"
                }
                _queryResponse.value = Resource.Error(errorMsg)
                _pendingMessage.value = null
                Log.e("ChatbotViewModel", "Exception asking question", e)
            }
        }
    }
    
    fun updateConversationTitle(conversationId: String, newTitle: String) {
        viewModelScope.launch {
            try {
                val request = UpdateConversationRequest(title = newTitle)
                val response = chatbotApi.updateConversation(conversationId, request)
                
                if (response.isSuccessful) {
                    loadConversations() // Refresh list
                    loadConversation(conversationId) // Refresh current
                }
            } catch (e: Exception) {
                // Handle error silently or show toast
            }
        }
    }
    
    fun deleteConversation(conversationId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val userId = PreferenceManager.getUserId().toString()
                val response = chatbotApi.deleteConversation(conversationId, userId)
                
                if (response.isSuccessful) {
                    loadConversations() // Refresh list
                    _currentConversation.value = null
                    onSuccess()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun getFarmAdvice() {
        viewModelScope.launch {
            _farmAdvice.value = Resource.Loading()
            _farmAdviceBackgroundLoading.value = false
            var startTime = System.currentTimeMillis()
            
            try {
                val token = PreferenceManager.getToken()
                
                // Log token availability (don't log the actual token for security)
                Log.d("ChatbotViewModel", "Token available: ${token.isNotEmpty()}")
                
                if (token.isEmpty()) {
                    _farmAdvice.value = Resource.Error("Authentication required. Please log in again.")
                    return@launch
                }
                
                val response = mainApi.getFarmAdvice()
                val elapsedTime = System.currentTimeMillis() - startTime

                if (response.isSuccessful && response.body() != null) {
                    _farmAdvice.value = Resource.Success(response.body()!!)
                    _showFarmAdviceNotification.value = false
                    Log.d("ChatbotViewModel", "Farm advice retrieved in ${elapsedTime}ms")
                } else {
                    val errorMsg = when (response.code()) {
                        403 -> "Access denied. Please ensure you're logged in with proper permissions."
                        401 -> "Your session has expired. Please log in again."
                        404 -> "Farm analytics data not found. Please ensure your farm profile is complete."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to get farm advice: ${response.message()}"
                    }
                    Log.e("ChatbotViewModel", "Error getting farm advice: ${response.code()} - ${response.message()}")
                    _farmAdvice.value = Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unable to connect"
                Log.e("ChatbotViewModel", "Exception getting farm advice", e)

                // Check if it's a timeout exception
                val isTimeout = e is java.net.SocketTimeoutException ||
                        (e is java.io.IOException && errorMsg.contains("timeout", ignoreCase = true))

                if (isTimeout) {
                    // Move processing to background and start polling until success
                    _farmAdviceBackgroundLoading.value = true
                    _farmAdvice.value = Resource.Loading(message = "Still processing... Please wait a little more")
                    startFarmAdviceBackgroundPolling()
                } else {
                    _farmAdvice.value = Resource.Error("Error: $errorMsg")
                }
            }
        }
    }

    /**
     * Start a background polling job that retries fetching farm advice until success.
     * Uses exponential backoff with a cap on total duration to avoid infinite loops.
     */
    fun startFarmAdviceBackgroundPolling(
        initialDelayMs: Long = 5000L,
        maxTotalDurationMs: Long = 30 * 60 * 1000L // 30 minutes
    ) {
        // If a background job is already running, don't start another
        if (_farmAdviceBackgroundJobActive.value) return

        _farmAdviceBackgroundJob = viewModelScope.launch {
            _farmAdviceBackgroundJobActive.value = true
            var attempt = 0
            var delayMs = initialDelayMs
            val start = System.currentTimeMillis()

            try {
                while (true) {
                    // Wait before retrying
                    kotlinx.coroutines.delay(delayMs)

                    try {
                        val retryResponse = mainApi.getFarmAdvice()
                        if (retryResponse.isSuccessful && retryResponse.body() != null) {
                            _farmAdvice.value = Resource.Success(retryResponse.body()!!)
                            _showFarmAdviceNotification.value = true
                            break
                        } else {
                            Log.d("ChatbotViewModel", "Background retry failed: ${retryResponse.code()} - ${retryResponse.message()}")
                        }
                    } catch (inner: Exception) {
                        Log.d("ChatbotViewModel", "Background retry exception", inner)
                    }

                    attempt++

                    // Exponential backoff with cap
                    delayMs = (delayMs * 2).coerceAtMost(60_000L) // cap to 60s

                    // Stop if we've been running too long
                    if (System.currentTimeMillis() - start > maxTotalDurationMs) {
                        _farmAdvice.value = Resource.Error("Farm advice is taking longer than expected. You can continue using the app and we'll notify you when it's ready.")
                        break
                    }
                }
            } finally {
                _farmAdviceBackgroundLoading.value = false
                _farmAdviceBackgroundJobActive.value = false
                _farmAdviceBackgroundJob = null
            }
        }
    }
    
    fun clearQueryResponse() {
        _queryResponse.value = null
    }
    
    fun clearFarmAdvice() {
        _farmAdvice.value = null
    }
    
    // Diagnostic function - check authentication status
    fun verifyAuthenticationStatus() {
        val token = PreferenceManager.getToken()
        Log.d("ChatbotViewModel", "=== AUTHENTICATION STATUS ===")
        Log.d("ChatbotViewModel", "Token exists: ${token.isNotEmpty()}")
        Log.d("ChatbotViewModel", "Token length: ${token.length}")
        Log.d("ChatbotViewModel", "Token is blank: ${token.isBlank()}")
        if (token.isNotEmpty()) {
            Log.d("ChatbotViewModel", "Token preview: ${token.take(10)}...${token.takeLast(10)}")
        }
    }

    
    fun clearPendingMessage() {
        _pendingMessage.value = null
    }
}
