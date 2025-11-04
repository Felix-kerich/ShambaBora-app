package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class Dashboard(
    @SerializedName("farmerName")
    val farmerName: String? = null,
    
    @SerializedName("farmName")
    val farmName: String? = null,
    
    @SerializedName("dashboardDate")
    val dashboardDate: String? = null,
    
    @SerializedName("totalActivities")
    val totalActivities: Int? = 0,
    
    @SerializedName("totalExpenses")
    val totalExpenses: Int? = 0,
    
    @SerializedName("totalYieldRecords")
    val totalYieldRecords: Int? = 0,
    
    @SerializedName("upcomingRemindersCount")
    val upcomingRemindersCount: Int? = 0,
    
    @SerializedName("totalExpensesAmount")
    val totalExpensesAmount: Double? = 0.0,
    
    @SerializedName("totalRevenue")
    val totalRevenue: Double? = 0.0,
    
    @SerializedName("netProfit")
    val netProfit: Double? = 0.0,
    
    @SerializedName("profitMargin")
    val profitMargin: Double? = 0.0,
    
    @SerializedName("cropSummaries")
    val cropSummaries: List<CropSummary>? = null,
    
    @SerializedName("expensesByCategory")
    val expensesByCategory: Map<String, Double>? = null,
    
    @SerializedName("expensesByGrowthStage")
    val expensesByGrowthStage: Map<String, Double>? = null,
    
    @SerializedName("recentActivities")
    val recentActivities: List<FarmActivity>? = null,
    
    @SerializedName("recentExpenses")
    val recentExpenses: List<FarmExpense>? = null,
    
    @SerializedName("recentYields")
    val recentYields: List<YieldRecord>? = null,
    
    @SerializedName("upcomingReminders")
    val upcomingReminders: List<ActivityReminder>? = null,
    
    @SerializedName("performanceMetrics")
    val performanceMetrics: List<PerformanceMetric>? = null,
    
    @SerializedName("recommendations")
    val recommendations: List<String>? = null
)

data class CropSummary(
    @SerializedName("cropType")
    val cropType: String,
    
    @SerializedName("activityCount")
    val activityCount: Int,
    
    @SerializedName("totalExpenses")
    val totalExpenses: Double,
    
    @SerializedName("totalYield")
    val totalYield: Double,
    
    @SerializedName("totalRevenue")
    val totalRevenue: Double,
    
    @SerializedName("averageYieldPerUnit")
    val averageYieldPerUnit: Double,
    
    @SerializedName("bestPerformingCrop")
    val bestPerformingCrop: String? = null
)

data class PerformanceMetric(
    @SerializedName("metricName")
    val metricName: String,
    
    @SerializedName("currentValue")
    val currentValue: String,
    
    @SerializedName("previousValue")
    val previousValue: String? = null,
    
    @SerializedName("trend")
    val trend: String? = null,
    
    @SerializedName("unit")
    val unit: String? = null
)

