package com.app.shamba_bora.data.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Patch (MaizePatch) - Represents a planted plot for a specific year & season
 * Based on FRONTEND_RECORDS_KEEPING.md - MaizePatchDTO
 */
data class MaizePatchDTO(
    val id: Long? = null,
    val farmerProfileId: Long? = null,
    val year: Int = LocalDate.now().year,
    val season: String = "LONG_RAIN",
    val name: String = "",
    val cropType: String = "Maize",
    val area: Double? = null,
    val areaUnit: String = "ha",
    val plantingDate: LocalDate? = null,
    val expectedHarvestDate: LocalDate? = null,
    val actualHarvestDate: LocalDate? = null,
    val location: String = "",
    val notes: String = "",
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val activities: List<FarmActivityResponse>? = null,
    val yields: List<YieldRecordResponse>? = null,
    val expenses: List<FarmExpenseResponse>? = null
) {
    fun isValid(): Boolean {
        return year > 0 && 
            name.isNotBlank() && 
            season.isNotBlank() && 
            cropType.isNotBlank()
    }
}

/**
 * Farm Activity - Represents a farm operation
 * Based on FRONTEND_RECORDS_KEEPING.md - FarmActivityRequest/Response
 */
data class FarmActivityRequest(
    val activityType: String = "",
    val cropType: String = "Maize",
    val activityDate: LocalDate = LocalDate.now(),
    val description: String = "",
    val areaSize: Double? = null,
    val units: String = "ha",
    val yield: Double? = null,
    val cost: Double? = null,
    val productUsed: String = "",
    val applicationRate: Double? = null,
    val weatherConditions: String = "",
    val soilConditions: String = "",
    val notes: String = "",
    val location: String = "",
    val laborHours: Int? = null,
    val equipmentUsed: String = "",
    val laborCost: Double? = null,
    val equipmentCost: Double? = null,
    val seedVarietyId: Long? = null,
    val seedVarietyName: String = "",
    val fertilizerProductId: Long? = null,
    val fertilizerProductName: String = "",
    val pesticideProductId: Long? = null,
    val pesticideProductName: String = "",
    val patchId: Long? = null
) {
    fun isValid(): Boolean {
        return activityType.isNotBlank() && 
            cropType.isNotBlank() && 
            description.isNotBlank()
    }
}

data class FarmActivityResponse(
    val id: Long? = null,
    val activityType: String = "",
    val cropType: String = "Maize",
    val activityDate: LocalDate = LocalDate.now(),
    val description: String = "",
    val areaSize: Double? = null,
    val units: String = "ha",
    val yield: Double? = null,
    val cost: Double? = null,
    val productUsed: String = "",
    val applicationRate: Double? = null,
    val weatherConditions: String = "",
    val soilConditions: String = "",
    val notes: String = "",
    val location: String = "",
    val laborHours: Int? = null,
    val equipmentUsed: String = "",
    val laborCost: Double? = null,
    val equipmentCost: Double? = null,
    val seedVarietyId: Long? = null,
    val seedVarietyName: String = "",
    val fertilizerProductId: Long? = null,
    val fertilizerProductName: String = "",
    val pesticideProductId: Long? = null,
    val pesticideProductName: String = "",
    val patchId: Long? = null,
    val patchName: String = "",
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val yieldTrend: String? = null,
    val percentageChange: Double? = null,
    val possibleReasons: String? = null
)

/**
 * Farm Expense - Represents money spent on farm operations
 * Based on FRONTEND_RECORDS_KEEPING.md - FarmExpenseRequest/Response
 */
data class FarmExpenseRequest(
    val cropType: String = "Maize",
    val category: String = "SEEDS",
    val description: String = "",
    val amount: Double = 0.0,
    val expenseDate: LocalDate = LocalDate.now(),
    val supplier: String = "",
    val invoiceNumber: String = "",
    val paymentMethod: String = "CASH",
    val notes: String = "",
    val growthStage: String = "PRE_PLANTING",
    val farmActivityId: Long? = null,
    val patchId: Long? = null,
    val isRecurring: Boolean = false,
    val recurringFrequency: String = ""
) {
    fun isValid(): Boolean {
        return cropType.isNotBlank() && 
            category.isNotBlank() && 
            description.isNotBlank() && 
            amount > 0
    }
}

data class FarmExpenseResponse(
    val id: Long? = null,
    val cropType: String = "Maize",
    val category: String = "SEEDS",
    val description: String = "",
    val amount: Double = 0.0,
    val expenseDate: LocalDate = LocalDate.now(),
    val supplier: String = "",
    val invoiceNumber: String = "",
    val paymentMethod: String = "CASH",
    val notes: String = "",
    val growthStage: String = "PRE_PLANTING",
    val farmActivityId: Long? = null,
    val patchId: Long? = null,
    val patchName: String = "",
    val isRecurring: Boolean = false,
    val recurringFrequency: String = "",
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

/**
 * Yield Record - Represents harvest results
 * Based on FRONTEND_RECORDS_KEEPING.md - YieldRecordRequest/Response
 */
data class YieldRecordRequest(
    val cropType: String = "Maize",
    val harvestDate: LocalDate = LocalDate.now(),
    val yieldAmount: Double = 0.0,
    val unit: String = "kg",
    val areaHarvested: Double? = null,
    val marketPrice: Double? = null,
    val qualityGrade: String = "GRADE_A",
    val storageLocation: String = "",
    val buyer: String = "",
    val notes: String = "",
    val farmActivityId: Long? = null,
    val patchId: Long? = null
) {
    fun isValid(): Boolean {
        return cropType.isNotBlank() && 
            yieldAmount > 0 && 
            unit.isNotBlank()
    }
}

data class YieldRecordResponse(
    val id: Long? = null,
    val cropType: String = "Maize",
    val harvestDate: LocalDate = LocalDate.now(),
    val yieldAmount: Double = 0.0,
    val unit: String = "kg",
    val areaHarvested: Double? = null,
    val yieldPerUnit: Double = 0.0,
    val marketPrice: Double? = null,
    val qualityGrade: String = "GRADE_A",
    val storageLocation: String = "",
    val buyer: String = "",
    val notes: String = "",
    val farmActivityId: Long? = null,
    val patchId: Long? = null,
    val patchName: String = "",
    val seedVarietyId: Long? = null,
    val seedVarietyName: String? = null,
    val primaryFertilizerId: Long? = null,
    val primaryPesticideId: Long? = null,
    val fertilizerProductName: String? = null,
    val pesticideProductName: String? = null,
    val soilConditionAtHarvest: String? = null,
    val weatherDuringGrowth: String? = null,
    val estimatedInputEffectiveness: Int? = null,
    val totalInputCost: Double? = null,
    val costPerKgProduced: Double? = null,
    val profitPerKg: Double? = null,
    val totalRevenue: Double? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

/**
 * Patch Summary for Analytics
 * Based on FRONTEND_RECORDS_KEEPING.md - PatchSummaryDTO
 */
data class PatchSummaryDTO(
    val patchId: Long = 0,
    val patchName: String = "",
    val year: Int = 0,
    val season: String = "",
    val cropType: String = "Maize",
    val area: Double? = null,
    val areaUnit: String = "ha",
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

/**
 * Patch Comparison DTO for Analytics
 * Based on FRONTEND_RECORDS_KEEPING.md - PatchComparisonDTO
 */
data class PatchComparisonDTO(
    val patches: List<PatchSummaryDTO> = emptyList()
)
