package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.Weather
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCurrentWeather(location: String): Resource<Weather> {
        return try {
            val response = apiService.getCurrentWeather(location)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get weather")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getWeatherForecast(location: String): Resource<Weather> {
        return try {
            val response = apiService.getWeatherForecast(location)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get forecast")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getDailyForecast(location: String, days: Int = 7): Resource<Weather> {
        return try {
            val response = apiService.getDailyForecast(location, days)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get daily forecast")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getMonthlyStats(location: String, month: Int): Resource<Weather> {
        return try {
            val response = apiService.getMonthlyStats(location, month)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get monthly stats")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

