package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.FarmerProfile
import com.app.shamba_bora.data.model.FarmerProfileRequest
import com.app.shamba_bora.data.model.User
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.data.network.UpdateUserRequest
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCurrentUser(): Resource<User> {
        return try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateUser(request: UpdateUserRequest): Resource<User> {
        return try {
            val response = apiService.updateUser(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to update user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getMyFarmerProfile(): Resource<FarmerProfile> {
        return try {
            val response = apiService.getMyFarmerProfile()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get farmer profile")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun createFarmerProfile(request: FarmerProfileRequest): Resource<FarmerProfile> {
        return try {
            val response = apiService.createFarmerProfile(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create farmer profile")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateMyFarmerProfile(request: FarmerProfileRequest): Resource<FarmerProfile> {
        return try {
            val response = apiService.updateMyFarmerProfile(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to update farmer profile")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

