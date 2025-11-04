package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class FarmExpense(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("cropType")
    val cropType: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("expenseDate")
    val expenseDate: String, // YYYY-MM-DD
    
    @SerializedName("supplier")
    val supplier: String? = null,
    
    @SerializedName("invoiceNumber")
    val invoiceNumber: String? = null,
    
    @SerializedName("paymentMethod")
    val paymentMethod: String? = null,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("growthStage")
    val growthStage: String? = null,
    
    @SerializedName("farmActivityId")
    val farmActivityId: Long? = null,
    
    @SerializedName("isRecurring")
    val isRecurring: Boolean? = false,
    
    @SerializedName("recurringFrequency")
    val recurringFrequency: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

