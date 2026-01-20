package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmAnalyticsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    // Farm Analytics State
    private val _farmAnalytics = MutableStateFlow<Resource<FarmAnalyticsDTO>>(Resource.Loading())
    val farmAnalytics: StateFlow<Resource<FarmAnalyticsDTO>> = _farmAnalytics
    
    // Patch Comparison State
    private val _patchComparison = MutableStateFlow<Resource<PatchComparisonResponse>>(Resource.Loading())
    val patchComparison: StateFlow<Resource<PatchComparisonResponse>> = _patchComparison
    
    // Selected Patches for Comparison
    private val _selectedPatchesForComparison = MutableStateFlow<List<Long>>(emptyList())
    val selectedPatchesForComparison: StateFlow<List<Long>> = _selectedPatchesForComparison
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // ============ FARM ANALYTICS METHODS ============
    
    fun getFarmAnalytics(
        cropType: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = apiService.getFarmAnalytics(cropType, startDate, endDate)
                
                if (response.isSuccessful && response.body() != null) {
                    _farmAnalytics.value = Resource.Success(response.body()!!)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    _error.value = errorMessage
                    _farmAnalytics.value = Resource.Error(errorMessage)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
                _farmAnalytics.value = Resource.Error(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // ============ PATCH COMPARISON METHODS ============
    
    fun comparePatches(patchIds: List<Long>) {
        if (patchIds.isEmpty()) {
            _error.value = "Please select at least 2 patches to compare"
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = apiService.comparePatches(patchIds)
                
                if (response.isSuccessful && response.body() != null) {
                    _patchComparison.value = Resource.Success(response.body()!!)
                    _selectedPatchesForComparison.value = patchIds
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    _error.value = errorMessage
                    _patchComparison.value = Resource.Error(errorMessage)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
                _patchComparison.value = Resource.Error(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun resetComparison() {
        _patchComparison.value = Resource.Loading()
        _selectedPatchesForComparison.value = emptyList()
    }
    
    // ============ UTILITY METHODS FOR UI ============
    
    fun getPerformanceRating(profitMargin: Double): String {
        return when {
            profitMargin >= 80.0 -> "EXCELLENT"
            profitMargin >= 60.0 -> "GOOD"
            profitMargin >= 40.0 -> "AVERAGE"
            else -> "POOR"
        }
    }
    
    fun getPerformanceColor(rating: String): Long {
        return when (rating) {
            "EXCELLENT" -> 0xFF4CAF50 // Green
            "GOOD" -> 0xFF8BC34A    // Light Green
            "AVERAGE" -> 0xFFFFC107 // Amber
            "POOR" -> 0xFFF44336    // Red
            else -> 0xFF9E9E9E      // Grey
        }
    }
    
    fun calculateExpenseBreakdown(expenses: Map<String, Double>): List<ExpenseBreakdown> {
        if (expenses.isEmpty()) return emptyList()
        
        val total = expenses.values.sum()
        return expenses.map { (category, amount) ->
            ExpenseBreakdown(
                category = category,
                amount = amount,
                percentage = if (total > 0) (amount / total) * 100 else 0.0
            )
        }
    }
    
    fun getTrendIcon(trend: String?): String {
        return when (trend) {
            "INCREASING" -> "trending_up"
            "DECREASING" -> "trending_down"
            else -> "trending_flat"
        }
    }
    
    fun getTrendColor(trend: String?): Long {
        return when (trend) {
            "INCREASING" -> 0xFF4CAF50 // Green
            "DECREASING" -> 0xFFF44336 // Red
            else -> 0xFF9E9E9E         // Grey
        }
    }
}
