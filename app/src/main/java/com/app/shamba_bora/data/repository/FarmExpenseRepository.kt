package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.FarmExpense
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class FarmExpenseRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getExpenses(
        cropType: String? = null,
        category: String? = null,
        page: Int = 0,
        size: Int = 10
    ): Resource<PageResponse<FarmExpense>> {
        return try {
            val response = apiService.getExpenses(cropType, category, page, size)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get expenses")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getExpense(id: Long): Resource<FarmExpense> {
        return try {
            val response = apiService.getExpense(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get expense")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun createExpense(expense: FarmExpense): Resource<FarmExpense> {
        return try {
            val response = apiService.createExpense(expense)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create expense")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateExpense(id: Long, expense: FarmExpense): Resource<FarmExpense> {
        return try {
            val response = apiService.updateExpense(id, expense)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to update expense")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun deleteExpense(id: Long): Resource<Unit> {
        return try {
            val response = apiService.deleteExpense(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete expense")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getTotalExpenses(cropType: String? = null): Resource<Double> {
        return try {
            val response = apiService.getTotalExpenses(cropType)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get total expenses")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getExpensesByCategory(cropType: String): Resource<Map<String, Double>> {
        return try {
            val response = apiService.getExpensesByCategory(cropType)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get expenses by category")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getExpensesByGrowthStage(cropType: String): Resource<Map<String, Double>> {
        return try {
            val response = apiService.getExpensesByGrowthStage(cropType)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get expenses by growth stage")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    // ========== DTO Methods ==========
    suspend fun createExpenseWithDTO(expense: com.app.shamba_bora.data.model.FarmExpenseRequest): Resource<com.app.shamba_bora.data.model.FarmExpenseResponse> {
        return try {
            val response = apiService.createExpenseWithDTO(expense)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create expense")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

