package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("groupId")
    val groupId: Long? = null,
    
    @SerializedName("senderId")
    val senderId: Long,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("attachmentUrl")
    val attachmentUrl: String? = null,
    
    @SerializedName("sentAt")
    val sentAt: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null
)

data class DirectMessage(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("senderId")
    val senderId: Long,
    
    @SerializedName("senderName")
    val senderName: String? = null,
    
    @SerializedName("recipientId")
    val recipientId: Long,
    
    @SerializedName("recipientName")
    val recipientName: String? = null,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    
    @SerializedName("messageType")
    val messageType: String? = "TEXT", // TEXT, IMAGE, FILE, LOCATION
    
    @SerializedName("status")
    val status: String? = "SENT", // SENT, DELIVERED, READ, FAILED
    
    @SerializedName("readAt")
    val readAt: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null
)

