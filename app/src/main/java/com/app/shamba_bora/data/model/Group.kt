package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class Group(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("ownerId")
    val ownerId: Long? = null,
    
    @SerializedName("ownerName")
    val ownerName: String? = null,
    
    @SerializedName("memberIds")
    val memberIds: List<Long>? = null,
    
    @SerializedName("memberCount")
    val memberCount: Int? = 0,
    
    @SerializedName("isMember")
    val isMember: Boolean? = false,
    
    @SerializedName("isPrivate")
    val isPrivate: Boolean? = false,
    
    @SerializedName("category")
    val category: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class GroupMembership(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("groupId")
    val groupId: Long,
    
    @SerializedName("groupName")
    val groupName: String? = null,
    
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("userName")
    val userName: String? = null,
    
    @SerializedName("role")
    val role: String? = "MEMBER", // OWNER, ADMIN, MODERATOR, MEMBER
    
    @SerializedName("status")
    val status: String? = "ACTIVE", // ACTIVE, PENDING, SUSPENDED, LEFT
    
    @SerializedName("joinedAt")
    val joinedAt: String? = null,
    
    @SerializedName("invitedBy")
    val invitedBy: Long? = null,
    
    @SerializedName("invitedByName")
    val invitedByName: String? = null
)

