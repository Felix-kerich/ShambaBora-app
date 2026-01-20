package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.FarmAnalyticsDTO
import com.app.shamba_bora.data.model.PatchComparisonResponse
import com.app.shamba_bora.data.model.PatchSummaryDTO
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class FarmAnalyticsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getFarmAnalytics(
        cropType: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Resource<FarmAnalyticsDTO> {
        return try {
            val response = apiService.getFarmAnalytics(cropType, startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get farm analytics")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getPatchSummary(patchId: Long): Resource<PatchSummaryDTO> {
        return try {
            val response = apiService.getPatchSummary(patchId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get patch summary")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun comparePatches(patchIds: List<Long>): Resource<PatchComparisonResponse> {
        return try {
            val response = apiService.comparePatches(patchIds)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to compare patches")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
