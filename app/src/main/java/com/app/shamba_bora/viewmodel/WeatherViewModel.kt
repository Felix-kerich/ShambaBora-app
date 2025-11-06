package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.Weather
import com.app.shamba_bora.data.repository.WeatherRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    
    private val _currentWeatherState = MutableStateFlow<Resource<Weather>>(Resource.Loading())
    val currentWeatherState: StateFlow<Resource<Weather>> = _currentWeatherState.asStateFlow()
    
    private val _forecastState = MutableStateFlow<Resource<Weather>>(Resource.Loading())
    val forecastState: StateFlow<Resource<Weather>> = _forecastState.asStateFlow()
    
    fun loadCurrentWeather(location: String) {
        viewModelScope.launch {
            _currentWeatherState.value = Resource.Loading()
            _currentWeatherState.value = repository.getCurrentWeather(location)
        }
    }
    
    fun loadWeatherForecast(location: String) {
        viewModelScope.launch {
            _forecastState.value = Resource.Loading()
            _forecastState.value = repository.getWeatherForecast(location)
        }
    }
    
    fun loadDailyForecast(location: String, days: Int = 7) {
        viewModelScope.launch {
            _forecastState.value = Resource.Loading()
            _forecastState.value = repository.getDailyForecast(location, days)
        }
    }
}

