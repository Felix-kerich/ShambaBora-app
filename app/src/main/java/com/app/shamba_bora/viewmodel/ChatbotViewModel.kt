package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.utils.Constants
import com.app.shamba_bora.utils.PreferenceManager
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor() : ViewModel() {
    
    private val _conversations = MutableStateFlow<Resource<List<ChatbotConversationSummary>>>(Resource.Loading())
    val conversations: StateFlow<Resource<List<ChatbotConversationSummary>>> = _conversations.asStateFlow()
    
    private val _currentConversation = MutableStateFlow<Resource<ChatbotConversation>?>(null)
    val currentConversation: StateFlow<Resource<ChatbotConversation>?> = _currentConversation.asStateFlow()
    
    private val _queryResponse = MutableStateFlow<Resource<ChatbotQueryResponse>?>(null)
    val queryResponse: StateFlow<Resource<ChatbotQueryResponse>?> = _queryResponse.asStateFlow()
    
    private val _farmAdvice = MutableStateFlow<Resource<FarmAdviceResponse>?>(null)
    val farmAdvice: StateFlow<Resource<FarmAdviceResponse>?> = _farmAdvice.asStateFlow()
    
    private val _pendingMessage = MutableStateFlow<String?>(null)
    val pendingMessage: StateFlow<String?> = _pendingMessage.asStateFlow()
    
    private val chatbotApi: ApiService
    private val mainApi: ApiService
    
    init {
        // Create dedicated Retrofit instance for chatbot service
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        // Chatbot API (RAG service)
        val chatbotRetrofit = Retrofit.Builder()
            .baseUrl(Constants.CHATBOT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        chatbotApi = chatbotRetrofit.create(ApiService::class.java)
        
        // Main API (Spring Boot backend)
        val mainRetrofit = Retrofit.Builder()
            .baseUrl("${Constants.BASE_URL}${Constants.API_PREFIX}/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        mainApi = mainRetrofit.create(ApiService::class.java)
        
        // Load conversations on init
        loadConversations()
    }
    
    fun loadConversations() {
        viewModelScope.launch {
            _conversations.value = Resource.Loading()
            try {
                val userId = PreferenceManager.getUserId().toString()
                val response = chatbotApi.getUserConversations(userId, limit = 50, offset = 0)
                
                if (response.isSuccessful && response.body() != null) {
                    _conversations.value = Resource.Success(response.body()!!)
                } else {
                    _conversations.value = Resource.Error("Failed to load conversations: ${response.message()}")
                }
            } catch (e: Exception) {
                _conversations.value = Resource.Error("Error: ${e.message ?: "Unknown error"}")
            }
        }
    }
    
    fun createConversation(title: String? = null, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val userId = PreferenceManager.getUserId().toString()
                val request = CreateConversationRequest(
                    userId = userId,
                    title = title
                )
                
                val response = chatbotApi.createConversation(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val conversation = response.body()!!
                    _currentConversation.value = Resource.Success(conversation)
                    loadConversations() // Refresh list
                    onSuccess(conversation.conversationId)
                } else {
                    _currentConversation.value = Resource.Error("Failed to create conversation")
                }
            } catch (e: Exception) {
                _currentConversation.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
    
    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            _currentConversation.value = Resource.Loading()
            try {
                val response = chatbotApi.getConversation(conversationId)
                
                if (response.isSuccessful && response.body() != null) {
                    _currentConversation.value = Resource.Success(response.body()!!)
                } else {
                    _currentConversation.value = Resource.Error("Failed to load conversation")
                }
            } catch (e: Exception) {
                _currentConversation.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
    
    fun askQuestion(question: String, conversationId: String? = null) {
        viewModelScope.launch {
            // Set pending message to show immediately in UI
            _pendingMessage.value = question
            _queryResponse.value = Resource.Loading()
            
            try {
                val userId = PreferenceManager.getUserId().toString()
                val request = ChatbotQueryRequest(
                    question = question,
                    userId = userId,
                    conversationId = conversationId,
                    k = 4
                )
                
                val response = chatbotApi.queryChatbot(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _queryResponse.value = Resource.Success(result)
                    
                    // Clear pending message
                    _pendingMessage.value = null
                    
                    // Reload the conversation to get updated messages
                    loadConversation(result.conversationId)
                } else {
                    _queryResponse.value = Resource.Error("Failed to get answer: ${response.message()}")
                    _pendingMessage.value = null
                }
            } catch (e: Exception) {
                _queryResponse.value = Resource.Error("Error: ${e.message ?: "Unable to connect"}")
                _pendingMessage.value = null
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
    
    fun clearQueryResponse() {
        _queryResponse.value = null
    }
}
