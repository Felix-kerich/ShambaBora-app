package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.YieldRecord
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class YieldRecordRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getYieldRecords(
        cropType: String? = null,
        page: Int = 0,
        size: Int = 10
    ): Resource<PageResponse<YieldRecord>> {
        return try {
            val response = apiService.getYieldRecords(cropType, page, size)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get yield records")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getYieldRecord(id: Long): Resource<YieldRecord> {
        return try {
            val response = apiService.getYieldRecord(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get yield record")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun createYieldRecord(yield: YieldRecord): Resource<YieldRecord> {
        return try {
            val response = apiService.createYieldRecord(yield)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create yield record")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateYieldRecord(id: Long, yield: YieldRecord): Resource<YieldRecord> {
        return try {
            val response = apiService.updateYieldRecord(id, yield)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to update yield record")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun deleteYieldRecord(id: Long): Resource<Unit> {
        return try {
            val response = apiService.deleteYieldRecord(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete yield record")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getTotalYield(cropType: String? = null): Resource<Double> {
        return try {
            val response = apiService.getTotalYield(cropType)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get total yield")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getTotalRevenue(cropType: String? = null): Resource<Double> {
        return try {
            val response = apiService.getTotalRevenue(cropType)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get total revenue")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getAverageYieldPerUnit(cropType: String): Resource<Double> {
        return try {
            val response = apiService.getAverageYieldPerUnit(cropType)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get average yield")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getBestYieldPerUnit(cropType: String): Resource<Double> {
        return try {
            val response = apiService.getBestYieldPerUnit(cropType)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get best yield")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getYieldTrends(
        cropType: String,
        startDate: String? = null,
        endDate: String? = null
    ): Resource<List<YieldRecord>> {
        return try {
            val response = apiService.getYieldTrends(cropType, startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get yield trends")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    // ========== DTO Methods ==========
    suspend fun createYieldRecordWithDTO(yield: com.app.shamba_bora.data.model.YieldRecordRequest): Resource<com.app.shamba_bora.data.model.YieldRecordResponse> {
        return try {
            val response = apiService.createYieldRecordWithDTO(yield)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create yield record")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

