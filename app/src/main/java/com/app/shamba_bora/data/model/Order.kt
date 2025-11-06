package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("buyerId")
    val buyerId: Long,
    
    @SerializedName("sellerId")
    val sellerId: Long? = null,
    
    @SerializedName("productId")
    val productId: Long,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("totalPrice")
    val totalPrice: Double? = null,
    
    @SerializedName("status")
    val status: String? = "PENDING", // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    
    @SerializedName("deliveryAddress")
    val deliveryAddress: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

