package com.app.shamba_bora.ui.screens.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.DailyForecast
import com.app.shamba_bora.data.model.Weather
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    var location by remember { mutableStateOf("Nairobi") }
    val currentWeatherState by viewModel.currentWeatherState.collectAsState()
    val forecastState by viewModel.forecastState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadCurrentWeather(location)
        viewModel.loadDailyForecast(location, 7)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Location Search
            item {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    placeholder = { Text("Enter city name") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.loadCurrentWeather(location)
                            viewModel.loadDailyForecast(location, 7)
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            // Current Weather
            item {
                when (val state = currentWeatherState) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Error -> {
                        ErrorView(
                            message = state.message ?: "Failed to load weather",
                            onRetry = { viewModel.loadCurrentWeather(location) }
                        )
                    }
                    is Resource.Success -> {
                        state.data?.let { weather ->
                            CurrentWeatherCard(weather)
                        }
                    }
                }
            }
            
            // 7-Day Forecast
            item {
                Text(
                    text = "7-Day Forecast",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                when (val state = forecastState) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Error -> {
                        ErrorView(
                            message = state.message ?: "Failed to load forecast",
                            onRetry = { viewModel.loadDailyForecast(location, 7) }
                        )
                    }
                    is Resource.Success -> {
                        state.data?.dailyForecasts?.let { forecasts ->
                            if (forecasts.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(forecasts) { forecast ->
                                        ForecastDayCard(forecast)
                                    }
                                }
                            } else {
                                Text(
                                    text = "No forecast data available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherCard(weather: Weather) {
    // Get current weather data - either from direct fields or first forecast
    val currentForecast = weather.dailyForecasts?.firstOrNull()
    val temperature = weather.temperature ?: currentForecast?.tempDay
    val description = weather.description ?: currentForecast?.weatherDescription
    val humidity = weather.humidity ?: currentForecast?.humidity
    val windSpeed = weather.windSpeed ?: currentForecast?.windSpeed
    val weatherMain = currentForecast?.weatherMain
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weather.locationName ?: weather.location ?: "Unknown",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            if (weather.country != null) {
                Text(
                    text = weather.country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = getWeatherIcon(description, weatherMain),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "${temperature?.toInt() ?: "--"}°C",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description?.replaceFirstChar { it.uppercase() } ?: "N/A",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetail(
                    icon = Icons.Default.Opacity,
                    label = "Humidity",
                    value = "${humidity ?: 0}%"
                )
                
                WeatherDetail(
                    icon = Icons.Default.Air,
                    label = "Wind",
                    value = "${String.format("%.1f", windSpeed ?: 0.0)} m/s"
                )
            }
            
            // Show temperature range if available
            if (currentForecast?.tempMin != null && currentForecast.tempMax != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetail(
                        icon = Icons.Default.ArrowDropDown,
                        label = "Min",
                        value = "${currentForecast.tempMin.toInt()}°C"
                    )
                    WeatherDetail(
                        icon = Icons.Default.KeyboardArrowUp,
                        label = "Max",
                        value = "${currentForecast.tempMax.toInt()}°C"
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetail(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun ForecastDayCard(forecast: DailyForecast) {
    Card(
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatDate(forecast.date),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Icon(
                imageVector = getWeatherIcon(forecast.weatherDescription, forecast.weatherMain),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = forecast.weatherDescription ?: "N/A",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${forecast.tempMax?.toInt() ?: "--"}°",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${forecast.tempMin?.toInt() ?: "--"}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (forecast.pop != null && forecast.pop > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Opacity,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${(forecast.pop * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

fun getWeatherIcon(description: String?, weatherMain: String? = null): androidx.compose.ui.graphics.vector.ImageVector {
    val mainCondition = weatherMain?.lowercase() ?: description?.lowercase() ?: ""
    return when {
        mainCondition.contains("clear") -> Icons.Default.WbSunny // Sunny
        mainCondition.contains("cloud") -> Icons.Default.Cloud // Cloudy
        mainCondition.contains("rain") || mainCondition.contains("drizzle") -> Icons.Default.CloudQueue // Rainy
        mainCondition.contains("thunder") -> Icons.Default.Thunderstorm // Thunderstorm
        mainCondition.contains("snow") -> Icons.Default.AcUnit // Snow
        mainCondition.contains("mist") || mainCondition.contains("fog") || mainCondition.contains("haze") -> Icons.Default.Cloud // Fog/Mist
        else -> Icons.Default.Cloud
    }
}

fun formatDate(dateString: String?): String {
    if (dateString == null) return "N/A"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}
