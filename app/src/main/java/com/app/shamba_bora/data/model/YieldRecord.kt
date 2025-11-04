package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class YieldRecord(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("cropType")
    val cropType: String,
    
    @SerializedName("harvestDate")
    val harvestDate: String, // YYYY-MM-DD
    
    @SerializedName("yieldAmount")
    val yieldAmount: Double,
    
    @SerializedName("unit")
    val unit: String,
    
    @SerializedName("areaHarvested")
    val areaHarvested: Double? = null,
    
    @SerializedName("yieldPerUnit")
    val yieldPerUnit: Double? = null,
    
    @SerializedName("marketPrice")
    val marketPrice: Double? = null,
    
    @SerializedName("totalRevenue")
    val totalRevenue: Double? = null,
    
    @SerializedName("qualityGrade")
    val qualityGrade: String? = null,
    
    @SerializedName("storageLocation")
    val storageLocation: String? = null,
    
    @SerializedName("buyer")
    val buyer: String? = null,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("farmActivityId")
    val farmActivityId: Long? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

