package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("category")
    val category: String? = null,
    
    @SerializedName("price")
    val price: Double? = null,
    
    @SerializedName("quantity")
    val quantity: Int? = null,
    
    @SerializedName("unit")
    val unit: String? = null,
    
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

