package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

/**
 * Comprehensive farm context data sent with chatbot queries
 * This includes analytics, patches, activities, expenses, yields, and farmer profile
 */
data class FarmContextData(
    @SerializedName("farmerName")
    val farmerName: String? = null,
    
    @SerializedName("farmName")
    val farmName: String? = null,
    
    @SerializedName("location")
    val location: String? = null,
    
    @SerializedName("farmSize")
    val farmSize: Double? = null,
    
    @SerializedName("primaryCrop")
    val primaryCrop: String? = null,
    
    @SerializedName("farmAnalytics")
    val farmAnalytics: FarmAnalyticsDTO? = null,
    
    @SerializedName("patches")
    val patches: List<PatchAnalytics> = emptyList(),
    
    @SerializedName("recentActivities")
    val recentActivities: List<FarmActivitySummary> = emptyList(),
    
    @SerializedName("recentExpenses")
    val recentExpenses: List<ExpenseSummary> = emptyList(),
    
    @SerializedName("recentYields")
    val recentYields: List<YieldSummary> = emptyList()
)

/**
 * Simplified activity summary for context
 */
data class FarmActivitySummary(
    @SerializedName("activityType")
    val activityType: String = "",
    
    @SerializedName("activityDate")
    val activityDate: String = "",
    
    @SerializedName("description")
    val description: String? = null
)

/**
 * Simplified expense summary for context
 */
data class ExpenseSummary(
    @SerializedName("category")
    val category: String = "",
    
    @SerializedName("amount")
    val amount: Double = 0.0,
    
    @SerializedName("expenseDate")
    val expenseDate: String = "",
    
    @SerializedName("description")
    val description: String? = null
)

/**
 * Simplified yield summary for context
 */
data class YieldSummary(
    @SerializedName("yieldAmount")
    val yieldAmount: Double = 0.0,
    
    @SerializedName("unit")
    val unit: String = "kg",
    
    @SerializedName("harvestDate")
    val harvestDate: String = "",
    
    @SerializedName("totalRevenue")
    val totalRevenue: Double? = null
)
