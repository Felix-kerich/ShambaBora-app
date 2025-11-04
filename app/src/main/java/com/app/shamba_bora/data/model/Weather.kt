package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("location")
    val location: String? = null,
    
    @SerializedName("locationName")
    val locationName: String? = null,
    
    @SerializedName("country")
    val country: String? = null,
    
    @SerializedName("timezone")
    val timezone: String? = null,
    
    @SerializedName("temperature")
    val temperature: Double? = null,
    
    @SerializedName("humidity")
    val humidity: Int? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("windSpeed")
    val windSpeed: Double? = null,
    
    @SerializedName("timestamp")
    val timestamp: String? = null,
    
    @SerializedName("dailyForecasts")
    val dailyForecasts: List<DailyForecast>? = null
)

data class DailyForecast(
    @SerializedName("date")
    val date: String? = null,
    
    @SerializedName("tempDay")
    val tempDay: Double? = null,
    
    @SerializedName("tempMin")
    val tempMin: Double? = null,
    
    @SerializedName("tempMax")
    val tempMax: Double? = null,
    
    @SerializedName("tempNight")
    val tempNight: Double? = null,
    
    @SerializedName("tempEve")
    val tempEve: Double? = null,
    
    @SerializedName("tempMorn")
    val tempMorn: Double? = null,
    
    @SerializedName("feelsLikeDay")
    val feelsLikeDay: Double? = null,
    
    @SerializedName("feelsLikeNight")
    val feelsLikeNight: Double? = null,
    
    @SerializedName("feelsLikeEve")
    val feelsLikeEve: Double? = null,
    
    @SerializedName("feelsLikeMorn")
    val feelsLikeMorn: Double? = null,
    
    @SerializedName("humidity")
    val humidity: Int? = null,
    
    @SerializedName("pressure")
    val pressure: Int? = null,
    
    @SerializedName("windSpeed")
    val windSpeed: Double? = null,
    
    @SerializedName("windDeg")
    val windDeg: Int? = null,
    
    @SerializedName("windGust")
    val windGust: Double? = null,
    
    @SerializedName("weatherMain")
    val weatherMain: String? = null,
    
    @SerializedName("weatherDescription")
    val weatherDescription: String? = null,
    
    @SerializedName("weatherIcon")
    val weatherIcon: String? = null,
    
    @SerializedName("rain")
    val rain: Double? = null,
    
    @SerializedName("snow")
    val snow: Double? = null,
    
    @SerializedName("clouds")
    val clouds: Int? = null,
    
    @SerializedName("pop")
    val pop: Double? = null, // Probability of precipitation
    
    @SerializedName("sunrise")
    val sunrise: Long? = null,
    
    @SerializedName("sunset")
    val sunset: Long? = null
)

