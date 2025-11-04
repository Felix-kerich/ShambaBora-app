package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("unit")
    val unit: String,
    
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    
    @SerializedName("location")
    val location: String? = null,
    
    @SerializedName("available")
    val available: Boolean = true,
    
    @SerializedName("sellerId")
    val sellerId: Long? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

