package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.User
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.data.network.LoginRequest
import com.app.shamba_bora.data.network.RegisterRequest
import com.app.shamba_bora.data.network.AuthResponse
import com.app.shamba_bora.data.network.UpdateUserRequest
import com.app.shamba_bora.utils.PreferenceManager
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun register(request: RegisterRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Save token and user info
                PreferenceManager.saveToken(authResponse.token)
                authResponse.userId?.let { PreferenceManager.saveUserId(it) }
                PreferenceManager.saveUsername(authResponse.username)
                PreferenceManager.saveEmail(authResponse.email)
                authResponse.roles?.let { PreferenceManager.saveUserRoles(it) }
                PreferenceManager.setLoggedIn(true)
                Resource.Success(authResponse)
            } else {
                Resource.Error(response.message() ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun login(request: LoginRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Save token and user info
                PreferenceManager.saveToken(authResponse.token)
                authResponse.userId?.let { PreferenceManager.saveUserId(it) }
                PreferenceManager.saveUsername(authResponse.username)
                PreferenceManager.saveEmail(authResponse.email)
                authResponse.roles?.let { PreferenceManager.saveUserRoles(it) }
                PreferenceManager.setLoggedIn(true)
                Resource.Success(authResponse)
            } else {
                Resource.Error(response.message() ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
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
    
    suspend fun deleteUser(): Resource<Unit> {
        return try {
            val response = apiService.deleteUser()
            if (response.isSuccessful) {
                PreferenceManager.clear()
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    fun logout() {
        PreferenceManager.clear()
    }
}

