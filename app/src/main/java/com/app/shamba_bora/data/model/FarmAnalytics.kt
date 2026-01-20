package com.app.shamba_bora.data.model

import java.time.LocalDate
import java.time.LocalDateTime

// ============ YIELD TRENDS ============
data class YieldTrend(
    val period: String = "",
    val yield: Double = 0.0,
    val yieldPerUnit: Double = 0.0,
    val trend: String = "FIRST_RECORD" // FIRST_RECORD, INCREASING, DECREASING, STABLE
)

// ============ EXPENSE TRENDS ============
data class ExpenseTrend(
    val period: String = "",
    val expense: Double = 0.0,
    val category: String = "",
    val trend: String = "STABLE" // INCREASING, DECREASING, STABLE
)

// ============ PROFITABILITY BY PERIOD ============
data class ProfitabilityByPeriod(
    val period: String = "",
    val totalRevenue: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netProfit: Double = 0.0,
    val profitMargin: Double = 0.0
)

// ============ PATCH ANALYTICS MODELS ============
data class PatchAnalytics(
    val patchId: Long = 0,
    val patchName: String = "",
    val season: String = "",
    val year: Int = 0,
    val location: String = "",
    val area: Double = 0.0,
    val areaUnit: String = "HA",
    val plantingDate: LocalDate? = null,
    val actualHarvestDate: LocalDate? = null,
    val totalExpenses: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val netProfit: Double = 0.0,
    val profitMargin: Double = 0.0,
    val totalYield: Double = 0.0,
    val yieldPerUnit: Double = 0.0,
    val yieldPerHectare: Double = 0.0,
    val labourCost: Double = 0.0,
    val fertiliserCost: Double = 0.0,
    val pesticideCost: Double = 0.0,
    val seedsCost: Double = 0.0,
    val otherCosts: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val expensesByGrowthStage: Map<String, Double> = emptyMap(),
    val performanceRating: String = "AVERAGE", // EXCELLENT, GOOD, AVERAGE, POOR
    val costPerUnitProduced: Double = 0.0,
    val revenuePerCostRatio: Double = 0.0
)

// ============ PATCH COMPARISON DTO ============
data class PatchComparisonData(
    val patchId: Long = 0,
    val patchName: String = "",
    val year: Int = 0,
    val season: String = "",
    val cropType: String = "",
    val area: Double = 0.0,
    val areaUnit: String = "HA",
    val totalExpenses: Double = 0.0,
    val totalYield: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val costPerKg: Double = 0.0,
    val profit: Double = 0.0,
    val profitPerKg: Double = 0.0,
    val roiPercentage: Double = 0.0,
    val activityTypes: List<String> = emptyList(),
    val inputSummaries: List<String> = emptyList(),
    val expenseSummaries: List<String> = emptyList()
)

// ============ MAIN FARM ANALYTICS ============
data class FarmAnalyticsDTO(
    val cropType: String = "",
    val analysisPeriodStart: LocalDate? = null,
    val analysisPeriodEnd: LocalDate? = null,
    val totalExpenses: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val netProfit: Double = 0.0,
    val profitMargin: Double = 0.0,
    val totalYield: Double = 0.0,
    val averageYieldPerUnit: Double = 0.0,
    val bestYield: Double = 0.0,
    val worstYield: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val expensesByGrowthStage: Map<String, Double> = emptyMap(),
    val yieldTrends: List<YieldTrend> = emptyList(),
    val expenseTrends: List<ExpenseTrend> = emptyList(),
    val profitabilityByPeriod: List<ProfitabilityByPeriod> = emptyList(),
    val patchesAnalytics: PatchesAnalyticsCollection = PatchesAnalyticsCollection(),
    val recommendations: List<String> = emptyList()
)

// ============ PATCHES ANALYTICS COLLECTION ============
data class PatchesAnalyticsCollection(
    val totalPatches: Int = 0,
    val patchesAnalyzed: Int = 0,
    val bestPerformingPatch: PatchAnalytics? = null,
    val worstPerformingPatch: PatchAnalytics? = null,
    val mostResourceIntensivePatch: PatchAnalytics? = null,
    val highestLabourCostPatch: PatchAnalytics? = null,
    val mostProfitablePatch: PatchAnalytics? = null,
    val highestExpensesPatch: PatchAnalytics? = null,
    val averageMetrics: PatchAnalytics? = null,
    val allPatchesAnalytics: List<PatchAnalytics> = emptyList()
)

// ============ PATCH COMPARISON RESPONSE ============
data class PatchComparisonResponse(
    val patches: List<PatchComparisonData> = emptyList()
)

// ============ SUMMARY MODELS FOR UI ============
data class AnalyticsSummaryCard(
    val title: String = "",
    val value: String = "",
    val subValue: String = "",
    val icon: String = "",
    val color: Long = 0xFF1976D2, // Material Blue
    val trend: String? = null, // UP, DOWN, STABLE
    val percentageChange: Double? = null
)

data class ChartDataPoint(
    val label: String = "",
    val value: Float = 0f,
    val color: Long = 0xFF1976D2
)

data class ExpenseBreakdown(
    val category: String = "",
    val amount: Double = 0.0,
    val percentage: Double = 0.0
)

data class PatchPerformanceRating(
    val patchId: Long = 0,
    val patchName: String = "",
    val rating: String = "AVERAGE", // EXCELLENT, GOOD, AVERAGE, POOR
    val score: Double = 0.0, // 0-100
    val profitMargin: Double = 0.0,
    val yieldPerHectare: Double = 0.0,
    val roi: Double = 0.0
)
