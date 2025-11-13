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
    val messages: List<ChatbotMessage>,
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
 * Request to query the RAG system
 */
data class ChatbotQueryRequest(
    val question: String,
    val k: Int = 4,
    @SerializedName("conversation_id")
    val conversationId: String? = null,
    @SerializedName("user_id")
    val userId: String
)

/**
 * Response from a query
 */
data class ChatbotQueryResponse(
    val answer: String,
    val contexts: List<ChatbotContext>,
    @SerializedName("conversation_id")
    val conversationId: String
)

/**
 * Request to create a new conversation
 */
data class CreateConversationRequest(
    @SerializedName("user_id")
    val userId: String,
    val title: String? = null,
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
    val advice: String,
    val fertilizerRecommendations: List<String>,
    val seedRecommendations: List<String>,
    val prioritizedActions: List<String>,
    val riskWarnings: List<String>
)
