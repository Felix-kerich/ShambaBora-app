package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

/**
 * Message in a conversation
 */
data class ChatbotMessage(
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: String,
    val contexts: List<ChatbotContext>? = null
)

/**
 * Context document retrieved for a message
 */
data class ChatbotContext(
    val score: Double,
    val id: String,
    val text: String,
    val metadata: Map<String, Any>? = null
)

/**
 * Full conversation with all messages
 */
data class ChatbotConversation(
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("user_id")
    val userId: String,
    val title: String?,
    val messages: List<ChatbotMessage>? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val metadata: Map<String, Any>? = null
)

/**
 * Conversation summary for listing
 */
data class ChatbotConversationSummary(
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("user_id")
    val userId: String,
    val title: String?,
    @SerializedName("message_count")
    val messageCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("last_message")
    val lastMessage: String?
)

/**
 * Request to query the RAG system - NEW API
 */
data class ChatbotQueryRequest(
    val question: String,
    val k: Int = 4,
    @SerializedName("conversation_id")
    val conversationId: String? = null,
    @SerializedName("user_id")
    val userId: String? = null,
    @SerializedName("farmer_id")
    val farmerId: Long? = null,
    @SerializedName("include_farmer_data")
    val includeFarmerData: Boolean = true,
    @SerializedName("session_id")
    val sessionId: String? = null,
    @SerializedName("farm_context")
    val farmContext: FarmContextData? = null,
    @SerializedName("system_prompt")
    val systemPrompt: String? = null
)

/**
 * Response from a query - NEW API
 */
data class ChatbotQueryResponse(
    val response: String? = null,  // From new API
    val answer: String? = null,    // From old API (for backward compatibility)
    val contexts: List<ChatbotContext>? = null,  // Optional for new API
    @SerializedName("conversation_id")
    val conversationId: String = "",
    @SerializedName("session_id")
    val sessionId: String? = null,
    val sources: List<String>? = null,
    val category: String? = null,
    @SerializedName("farmer_data_used")
    val farmerDataUsed: Boolean = false,
    val metadata: Map<String, Any>? = null
) {
    // Convenience property to get the response text regardless of API version
    fun getResponseText(): String {
        return response ?: answer ?: ""
    }
}

/**
 * Request to create a new conversation
 */
data class CreateConversationRequest(
    val title: String,
    val description: String? = null,
    @SerializedName("farmer_id")
    val farmerId: Long? = null,
    val metadata: Map<String, Any>? = null
)

/**
 * Request to update a conversation
 */
data class UpdateConversationRequest(
    val title: String? = null,
    val metadata: Map<String, Any>? = null
)

/**
 * Farm advice response from the analytics API
 */
data class FarmAdviceResponse(
    val advice: String? = null,
    val fertilizerRecommendations: List<String> = emptyList(),
    val seedRecommendations: List<String> = emptyList(),
    val prioritizedActions: List<String> = emptyList(),
    val riskWarnings: List<String> = emptyList(),
    val overallAssessment: String? = null,
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val recommendations: List<FarmRecommendation> = emptyList(),
    val bestPractices: List<BestPractice> = emptyList(),
    val cropOptimizationAdvice: String? = null,
    val investmentAdvice: String? = null,
    val farmerProfileId: Long? = null
)

/**
 * Individual farm recommendation
 */
data class FarmRecommendation(
    val category: String,
    val recommendation: String,
    val rationale: String? = null,
    val expectedBenefit: String? = null,
    val evidence: String? = null,
    val priority: Int = 3
)

/**
 * Best practice from farm advice
 */
data class BestPractice(
    val practice: String,
    val patchExample: String? = null,
    val results: String? = null,
    val reason: String? = null
)

/**
 * History entry for farmer conversations
 */
data class ChatHistory(
    val id: Long? = null,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("farmer_id")
    val farmerId: Long,
    @SerializedName("user_message")
    val userMessage: String,
    @SerializedName("ai_response")
    val aiResponse: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("response_time")
    val responseTime: Double? = null
)

/**
 * Wrapper response for farmer conversations list
 */
data class FarmerConversationsResponse(
    val status: String,
    val data: List<ChatbotConversationSummary>? = null,
    @SerializedName("conversations")
    val conversationsList: List<ChatbotConversationSummary>? = null
) {
    fun getConversations(): List<ChatbotConversationSummary> {
        return data ?: conversationsList ?: emptyList()
    }
}

/**
 * Message from the backend conversation response
 * Format: { "user_message": "...", "ai_response": "..." }
 */
data class ConversationMessage(
    @SerializedName("user_message")
    val userMessage: String,
    @SerializedName("ai_response")
    val aiResponse: String,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("response_time")
    val responseTime: Double? = null
)

/**
 * Session info from backend
 */
data class ConversationSession(
    val id: Long?,
    @SerializedName("conversation_id")
    val conversationId: String,
    val title: String?,
    val description: String?,
    @SerializedName("farmer_id")
    val farmerId: Long?,
    @SerializedName("message_count")
    val messageCount: Int,
    @SerializedName("is_archived")
    val isArchived: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("last_message_at")
    val lastMessageAt: String?
)

/**
 * Wrapper response for getting a conversation
 * Backend returns: { "status": "success", "data": { "session": {...}, "messages": [...] } }
 */
data class ConversationDetailsResponse(
    val status: String,
    val data: ConversationDetailsData?
)

data class ConversationDetailsData(
    val session: ConversationSession?,
    val messages: List<ConversationMessage>?
)
