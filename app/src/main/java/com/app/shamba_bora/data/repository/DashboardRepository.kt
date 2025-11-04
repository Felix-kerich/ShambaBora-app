package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.Dashboard
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDashboard(): Resource<Dashboard> {
        return try {
            val response = apiService.getDashboard()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get dashboard")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

