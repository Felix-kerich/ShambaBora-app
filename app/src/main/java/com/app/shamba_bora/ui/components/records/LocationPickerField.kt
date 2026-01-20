package com.app.shamba_bora.ui.components.records

import android.Manifest
import android.content.Context
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.shamba_bora.utils.LocationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Location Picker Component with GPS integration
 * Allows farmers to use their current location or enter manually
 */
@Composable
fun LocationPickerField(
    label: String,
    location: String,
    onLocationChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter location or use GPS",
    isRequired: Boolean = false,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val scope = rememberCoroutineScope()
    
    var isLoadingLocation by remember { mutableStateOf(false) }
    var showLocationError by remember { mutableStateOf(false) }
    var locationErrorMessage by remember { mutableStateOf("") }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // Permission granted, get location
            scope.launch {
                fetchCurrentLocation(
                    locationHelper = locationHelper,
                    context = context,
                    onLocationFetched = { loc ->
                        onLocationChange(loc)
                        isLoadingLocation = false
                    },
                    onError = { error ->
                        locationErrorMessage = error
                        showLocationError = true
                        isLoadingLocation = false
                    }
                )
            }
        } else {
            locationErrorMessage = "Location permission is required to use GPS"
            showLocationError = true
            isLoadingLocation = false
        }
    }

    Column(modifier = modifier) {
        // Label
        if (label.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isRequired) {
                    Text(
                        text = " *",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        // Location input field
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = {
                            if (locationHelper.hasLocationPermission()) {
                                isLoadingLocation = true
                                scope.launch {
                                    fetchCurrentLocation(
                                        locationHelper = locationHelper,
                                        context = context,
                                        onLocationFetched = { loc ->
                                            onLocationChange(loc)
                                            isLoadingLocation = false
                                        },
                                        onError = { error ->
                                            locationErrorMessage = error
                                            showLocationError = true
                                            isLoadingLocation = false
                                        }
                                    )
                                }
                            } else {
                                // Request permission
                                permissionLauncher.launch(LocationHelper.REQUIRED_PERMISSIONS)
                            }
                        },
                        enabled = enabled && !isLoadingLocation
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Use current location",
                            tint = if (enabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            enabled = enabled && !isLoadingLocation,
            singleLine = true
        )

        // Helper text
        if (!isLoadingLocation && location.isEmpty()) {
            Text(
                text = "Tap the GPS icon to use your current location",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // Location coordinates (if available)
        if (location.contains(",") && location.split(",").size == 2) {
            val parts = location.split(",")
            val lat = parts[0].trim().toDoubleOrNull()
            val lon = parts[1].trim().toDoubleOrNull()
            if (lat != null && lon != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "GPS Coordinates",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Lat: $lat, Lon: $lon",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }

    // Error snackbar
    if (showLocationError) {
        LaunchedEffect(showLocationError) {
            // Show error for 3 seconds
            kotlinx.coroutines.delay(3000)
            showLocationError = false
        }
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { showLocationError = false }) {
                    Text("OK")
                }
            }
        ) {
            Text(locationErrorMessage)
        }
    }
}

private suspend fun fetchCurrentLocation(
    locationHelper: LocationHelper,
    context: Context,
    onLocationFetched: (String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val location = locationHelper.getCurrentLocation()
        if (location != null) {
            // Try to get address name
            val locationString = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        // Create a readable address
                        val parts = mutableListOf<String>()
                        address.subLocality?.let { parts.add(it) }
                        address.locality?.let { parts.add(it) }
                        address.subAdminArea?.let { if (!parts.contains(it)) parts.add(it) }
                        
                        if (parts.isNotEmpty()) {
                            parts.joinToString(", ")
                        } else {
                            locationHelper.formatLocation(location)
                        }
                    } else {
                        locationHelper.formatLocation(location)
                    }
                } catch (e: Exception) {
                    // Fallback to coordinates
                    locationHelper.formatLocation(location)
                }
            }
            onLocationFetched(locationString)
        } else {
            onError("Unable to get location. Please try again or enter manually.")
        }
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Unknown error"}")
    }
}
