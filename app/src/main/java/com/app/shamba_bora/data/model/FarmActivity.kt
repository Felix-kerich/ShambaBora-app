package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class FarmActivity(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("activityType")
    val activityType: String,
    
    @SerializedName("cropType")
    val cropType: String,
    
    @SerializedName("activityDate")
    val activityDate: String, // YYYY-MM-DD
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("areaSize")
    val areaSize: Double? = null,
    
    @SerializedName("units")
    val units: String? = null,
    
    @SerializedName("yield")
    val yield: Double? = null,
    
    @SerializedName("cost")
    val cost: Double? = null,
    
    @SerializedName("productUsed")
    val productUsed: String? = null,
    
    @SerializedName("applicationRate")
    val applicationRate: Double? = null,
    
    @SerializedName("weatherConditions")
    val weatherConditions: String? = null,
    
    @SerializedName("soilConditions")
    val soilConditions: String? = null,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("location")
    val location: String? = null,
    
    @SerializedName("laborHours")
    val laborHours: Int? = null,
    
    @SerializedName("equipmentUsed")
    val equipmentUsed: String? = null,
    
    @SerializedName("laborCost")
    val laborCost: Double? = null,
    
    @SerializedName("equipmentCost")
    val equipmentCost: Double? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    
    @SerializedName("yieldTrend")
    val yieldTrend: String? = null,
    
    @SerializedName("percentageChange")
    val percentageChange: Double? = null,
    
    @SerializedName("possibleReasons")
    val possibleReasons: String? = null
)

data class ActivityReminder(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("activityId")
    val activityId: Long,
    
    @SerializedName("reminderDateTime")
    val reminderDateTime: String, // ISO 8601
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("repeatInterval")
    val repeatInterval: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class ActivityReminderRequest(
    @SerializedName("reminderDateTime")
    val reminderDateTime: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("repeatInterval")
    val repeatInterval: String? = null
)

