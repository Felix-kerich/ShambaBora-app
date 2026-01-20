package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("authorId")
    val authorId: Long? = null,
    
    @SerializedName("authorName")
    val authorName: String? = null,
    
    @SerializedName("groupId")
    val groupId: Long? = null,
    
    @SerializedName("groupName")
    val groupName: String? = null,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    
    @SerializedName("postType")
    val postType: String? = "GENERAL", // GENERAL, QUESTION, ADVICE, SHARE_EXPERIENCE, MARKET_UPDATE, WEATHER_ALERT, ANNOUNCEMENT
    
    @SerializedName("tags")
    val tags: List<String>? = null,
    
    @SerializedName("status")
    val status: String? = "ACTIVE", // ACTIVE, PENDING_MODERATION, APPROVED, REJECTED, HIDDEN
    
    @SerializedName("moderatedBy")
    val moderatedBy: Long? = null,
    
    @SerializedName("moderationNotes")
    val moderationNotes: String? = null,
    
    @SerializedName("likeCount")
    val likeCount: Int? = 0,
    
    @SerializedName("commentCount")
    val commentCount: Int? = 0,
    
    @SerializedName("shareCount")
    val shareCount: Int? = 0,
    
    @SerializedName("recentComments")
    val recentComments: List<PostComment>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    
    @SerializedName("likedByCurrentUser")
    val likedByCurrentUser: Boolean? = false
)

data class PostComment(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("postId")
    val postId: Long,
    
    @SerializedName("authorId")
    val authorId: Long? = null,
    
    @SerializedName("authorName")
    val authorName: String? = null,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("parentCommentId")
    val parentCommentId: Long? = null,
    
    @SerializedName("status")
    val status: String? = "ACTIVE",
    
    @SerializedName("moderatedBy")
    val moderatedBy: Long? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    
    @SerializedName("likeCount")
    val likeCount: Int? = 0,
    
    @SerializedName("likedByCurrentUser")
    val likedByCurrentUser: Boolean? = false
)

