package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.ActivityReminder
import com.app.shamba_bora.data.model.ActivityReminderRequest
import com.app.shamba_bora.data.model.FarmActivity
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class FarmActivityRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getActivities(
        activityType: String? = null,
        page: Int = 0,
        size: Int = 10
    ): Resource<PageResponse<FarmActivity>> {
        return try {
            val response = apiService.getActivities(activityType, page, size)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get activities")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getActivity(id: Long): Resource<FarmActivity> {
        return try {
            val response = apiService.getActivity(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get activity")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun createActivity(activity: FarmActivity): Resource<FarmActivity> {
        return try {
            val response = apiService.createActivity(activity)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create activity")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateActivity(id: Long, activity: FarmActivity): Resource<FarmActivity> {
        return try {
            val response = apiService.updateActivity(id, activity)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to update activity")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun deleteActivity(id: Long): Resource<Unit> {
        return try {
            val response = apiService.deleteActivity(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete activity")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getActivityReminders(activityId: Long): Resource<List<ActivityReminder>> {
        return try {
            val response = apiService.getActivityReminders(activityId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get reminders")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun addActivityReminder(
        activityId: Long,
        request: ActivityReminderRequest
    ): Resource<ActivityReminder> {
        return try {
            val response = apiService.addActivityReminder(activityId, request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to add reminder")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getUpcomingReminders(): Resource<List<ActivityReminder>> {
        return try {
            val response = apiService.getUpcomingReminders()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get reminders")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    // ========== DTO Methods ==========
    suspend fun createActivityWithDTO(activity: com.app.shamba_bora.data.model.FarmActivityRequest): Resource<com.app.shamba_bora.data.model.FarmActivityResponse> {
        return try {
            val response = apiService.createActivityWithDTO(activity)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create activity")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

