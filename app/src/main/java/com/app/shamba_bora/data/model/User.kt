package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("fullName")
    val fullName: String? = null,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    
    @SerializedName("role")
    val role: String? = null,
    
    @SerializedName("roles")
    val roles: List<String>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    
    @SerializedName("isActive")
    val isActive: Boolean? = true
)

