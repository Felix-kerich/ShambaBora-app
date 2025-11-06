package com.app.shamba_bora.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.FarmerProfileRequest
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    isFarmerProfile: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userState by viewModel.userState.collectAsState()
    val farmerProfileState by viewModel.farmerProfileState.collectAsState()
    val updateUserState by viewModel.updateUserState.collectAsState()
    val updateFarmerProfileState by viewModel.updateFarmerProfileState.collectAsState()
    
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    // Farmer profile fields
    var farmName by remember { mutableStateOf("") }
    var farmSize by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var county by remember { mutableStateOf("") }
    var farmDescription by remember { mutableStateOf("") }
    var alternatePhone by remember { mutableStateOf("") }
    var postalAddress by remember { mutableStateOf("") }
    var primaryCrops by remember { mutableStateOf("") }
    var farmingExperience by remember { mutableStateOf("") }
    var certifications by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        if (isFarmerProfile) {
            viewModel.loadFarmerProfile()
        } else {
            viewModel.loadUser()
        }
    }
    
    LaunchedEffect(userState) {
        if (userState is Resource.Success && !isFarmerProfile) {
            val user = userState.data
            fullName = user?.fullName ?: ""
            phoneNumber = user?.phoneNumber ?: ""
            email = user?.email ?: ""
        }
    }
    
    LaunchedEffect(farmerProfileState) {
        if (farmerProfileState is Resource.Success && isFarmerProfile) {
            val profile = farmerProfileState.data
            farmName = profile?.farmName ?: ""
            farmSize = profile?.farmSize?.toString() ?: ""
            location = profile?.location ?: ""
            county = profile?.county ?: ""
            farmDescription = profile?.farmDescription ?: ""
            alternatePhone = profile?.alternatePhone ?: ""
            postalAddress = profile?.postalAddress ?: ""
            primaryCrops = profile?.primaryCrops?.joinToString(", ") ?: ""
            farmingExperience = profile?.farmingExperience?.toString() ?: ""
            certifications = profile?.certifications?.joinToString(", ") ?: ""
        }
    }
    
    LaunchedEffect(updateUserState) {
        if (updateUserState is Resource.Success) {
            onNavigateBack()
        }
    }
    
    LaunchedEffect(updateFarmerProfileState) {
        if (updateFarmerProfileState is Resource.Success) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isFarmerProfile) "Edit Farmer Profile" else "Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            (isFarmerProfile && farmerProfileState is Resource.Loading) || 
            (!isFarmerProfile && userState is Resource.Loading) -> {
                LoadingIndicator()
            }
            (isFarmerProfile && farmerProfileState is Resource.Error) -> {
                ErrorView(
                    message = farmerProfileState.message ?: "Failed to load profile",
                    onRetry = { viewModel.refreshFarmerProfile() }
                )
            }
            (!isFarmerProfile && userState is Resource.Error) -> {
                ErrorView(
                    message = userState.message ?: "Failed to load profile",
                    onRetry = { viewModel.refreshUser() }
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isFarmerProfile) {
                        // Farmer Profile Fields
                        OutlinedTextField(
                            value = farmName,
                            onValueChange = { farmName = it },
                            label = { Text("Farm Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = farmSize,
                            onValueChange = { farmSize = it },
                            label = { Text("Farm Size (acres)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = county,
                            onValueChange = { county = it },
                            label = { Text("County") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = farmDescription,
                            onValueChange = { farmDescription = it },
                            label = { Text("Farm Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                        
                        OutlinedTextField(
                            value = alternatePhone,
                            onValueChange = { alternatePhone = it },
                            label = { Text("Alternate Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = postalAddress,
                            onValueChange = { postalAddress = it },
                            label = { Text("Postal Address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = primaryCrops,
                            onValueChange = { primaryCrops = it },
                            label = { Text("Primary Crops (comma separated)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = farmingExperience,
                            onValueChange = { farmingExperience = it },
                            label = { Text("Farming Experience (years)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = certifications,
                            onValueChange = { certifications = it },
                            label = { Text("Certifications (comma separated)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Button(
                            onClick = {
                                val request = FarmerProfileRequest(
                                    farmName = farmName,
                                    farmSize = farmSize.toDoubleOrNull(),
                                    location = location.ifBlank { null },
                                    county = county.ifBlank { null },
                                    farmDescription = farmDescription.ifBlank { null },
                                    alternatePhone = alternatePhone.ifBlank { null },
                                    postalAddress = postalAddress.ifBlank { null },
                                    primaryCrops = primaryCrops.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                    farmingExperience = farmingExperience.toIntOrNull(),
                                    certifications = certifications.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                )
                                
                                if (farmerProfileState is Resource.Success && farmerProfileState.data != null) {
                                    viewModel.updateFarmerProfile(request)
                                } else {
                                    viewModel.createFarmerProfile(request)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = farmName.isNotBlank()
                        ) {
                            Text("Save Farmer Profile")
                        }
                        
                        if (updateFarmerProfileState is Resource.Error) {
                            Text(
                                text = updateFarmerProfileState.message ?: "Failed to update profile",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        // User Profile Fields
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Button(
                            onClick = {
                                val request = com.app.shamba_bora.data.network.UpdateUserRequest(
                                    fullName = fullName.ifBlank { null },
                                    phoneNumber = phoneNumber.ifBlank { null },
                                    email = email.ifBlank { null }
                                )
                                viewModel.updateUser(request)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Profile")
                        }
                        
                        if (updateUserState is Resource.Error) {
                            Text(
                                text = updateUserState.message ?: "Failed to update profile",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

