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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
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
    val updateUserStateRaw by viewModel.updateUserState.collectAsState()
    val updateFarmerProfileStateRaw by viewModel.updateFarmerProfileState.collectAsState()

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
        // Always load user data first to get name and phone for auto-fill
        viewModel.loadUser()

        if (isFarmerProfile) {
            viewModel.loadFarmerProfile()
        }
    }

    LaunchedEffect(userState) {
        if (userState is Resource.Success) {
            val user = userState.data
            fullName = user?.fullName ?: ""
            phoneNumber = user?.phoneNumber ?: ""
            email = user?.email ?: ""

            // Auto-fill alternate phone with main phone if empty
            if (alternatePhone.isEmpty() && phoneNumber.isNotEmpty()) {
                alternatePhone = phoneNumber
            }
        }
    }

    LaunchedEffect(farmerProfileState) {
        if (isFarmerProfile) {
            when (farmerProfileState) {
                is Resource.Success -> {
                    val profile = farmerProfileState.data
                    if (profile != null) {
                        farmName = profile.farmName ?: ""
                        farmSize = profile.farmSize?.toString() ?: ""
                        location = profile.location ?: ""
                        county = profile.county ?: ""
                        farmDescription = profile.farmDescription ?: ""
                        alternatePhone = profile.alternatePhone ?: phoneNumber
                        postalAddress = profile.postalAddress ?: ""
                        primaryCrops = profile.primaryCrops?.joinToString(", ") ?: ""
                        farmingExperience = profile.farmingExperience?.toString() ?: ""
                        certifications = profile.certifications?.joinToString(", ") ?: ""
                    }
                }
                is Resource.Error -> {
                    // Auto-fill alternate phone with main phone for new profiles
                    if (alternatePhone.isEmpty() && phoneNumber.isNotEmpty()) {
                        alternatePhone = phoneNumber
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    // Track if we've already handled success to prevent multiple navigations
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(updateUserStateRaw) {
        if (updateUserStateRaw is Resource.Success && !hasNavigated) {
            hasNavigated = true
            // Brief delay to ensure UI updates before navigation
            kotlinx.coroutines.delay(300)
            onNavigateBack()
        }
    }

    LaunchedEffect(updateFarmerProfileStateRaw) {
        if (updateFarmerProfileStateRaw is Resource.Success && !hasNavigated) {
            hasNavigated = true
            // Brief delay to ensure UI updates before navigation
            kotlinx.coroutines.delay(300)
            onNavigateBack()
        }
    }

    // Reset navigation flag when screen is revisited
    LaunchedEffect(Unit) {
        hasNavigated = false
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
            (isFarmerProfile && farmerProfileState is Resource.Success) -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FarmerProfileForm(
                        fullName = fullName,
                        onFullNameChange = { fullName = it },
                        phoneNumber = phoneNumber,
                        onPhoneNumberChange = { phoneNumber = it },
                        email = email,
                        onEmailChange = { email = it },
                        farmName = farmName,
                        onFarmNameChange = { farmName = it },
                        farmSize = farmSize,
                        onFarmSizeChange = { farmSize = it },
                        location = location,
                        onLocationChange = { location = it },
                        county = county,
                        onCountyChange = { county = it },
                        farmDescription = farmDescription,
                        onFarmDescriptionChange = { farmDescription = it },
                        alternatePhone = alternatePhone,
                        onAlternatePhoneChange = { alternatePhone = it },
                        postalAddress = postalAddress,
                        onPostalAddressChange = { postalAddress = it },
                        primaryCrops = primaryCrops,
                        onPrimaryCropsChange = { primaryCrops = it },
                        farmingExperience = farmingExperience,
                        onFarmingExperienceChange = { farmingExperience = it },
                        certifications = certifications,
                        onCertificationsChange = { certifications = it },
                        onSubmit = {
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
                            viewModel.updateFarmerProfile(request)
                        },
                        isSubmitting = updateFarmerProfileStateRaw is Resource.Loading,
                        errorMessage = if (updateFarmerProfileStateRaw is Resource.Error) {
                            (updateFarmerProfileStateRaw as? Resource.Error)?.message ?: "Failed to save profile"
                        } else null,
                        isEditMode = true
                    )
                }
            }
            (isFarmerProfile && farmerProfileState is Resource.Error) -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FarmerProfileForm(
                        fullName = fullName,
                        onFullNameChange = { fullName = it },
                        phoneNumber = phoneNumber,
                        onPhoneNumberChange = { phoneNumber = it },
                        email = email,
                        onEmailChange = { email = it },
                        farmName = farmName,
                        onFarmNameChange = { farmName = it },
                        farmSize = farmSize,
                        onFarmSizeChange = { farmSize = it },
                        location = location,
                        onLocationChange = { location = it },
                        county = county,
                        onCountyChange = { county = it },
                        farmDescription = farmDescription,
                        onFarmDescriptionChange = { farmDescription = it },
                        alternatePhone = alternatePhone,
                        onAlternatePhoneChange = { alternatePhone = it },
                        postalAddress = postalAddress,
                        onPostalAddressChange = { postalAddress = it },
                        primaryCrops = primaryCrops,
                        onPrimaryCropsChange = { primaryCrops = it },
                        farmingExperience = farmingExperience,
                        onFarmingExperienceChange = { farmingExperience = it },
                        certifications = certifications,
                        onCertificationsChange = { certifications = it },
                        onSubmit = {
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
                            viewModel.createFarmerProfile(request)
                        },
                        isSubmitting = updateFarmerProfileStateRaw is Resource.Loading,
                        errorMessage = if (updateFarmerProfileStateRaw is Resource.Error) {
                            (updateFarmerProfileStateRaw as? Resource.Error)?.message ?: "Failed to save profile"
                        } else null,
                        isEditMode = false
                    )
                }
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
                    if (!isFarmerProfile) {
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
                            modifier = Modifier.fillMaxWidth(),
                            enabled = updateUserStateRaw !is Resource.Loading
                        ) {
                            if (updateUserStateRaw is Resource.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Save Profile")
                            }
                        }

                        if (updateUserStateRaw is Resource.Error) {
                            Text(
                                text = (updateUserStateRaw as? Resource.Error)?.message ?: "Failed to update profile",
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

@Composable
private fun FarmerProfileForm(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    farmName: String,
    onFarmNameChange: (String) -> Unit,
    farmSize: String,
    onFarmSizeChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    county: String,
    onCountyChange: (String) -> Unit,
    farmDescription: String,
    onFarmDescriptionChange: (String) -> Unit,
    alternatePhone: String,
    onAlternatePhoneChange: (String) -> Unit,
    postalAddress: String,
    onPostalAddressChange: (String) -> Unit,
    primaryCrops: String,
    onPrimaryCropsChange: (String) -> Unit,
    farmingExperience: String,
    onFarmingExperienceChange: (String) -> Unit,
    certifications: String,
    onCertificationsChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isSubmitting: Boolean = false,
    errorMessage: String? = null,
    isEditMode: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Information Section
        Text(
            text = "Your Information",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Full Name *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = fullName.isBlank()
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Phone Number *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = phoneNumber.isBlank()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Farm Information",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = farmName,
            onValueChange = onFarmNameChange,
            label = { Text("Farm Name *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = farmName.isBlank()
        )

        OutlinedTextField(
            value = farmSize,
            onValueChange = onFarmSizeChange,
            label = { Text("Farm Size (acres)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = county,
            onValueChange = onCountyChange,
            label = { Text("County") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = farmDescription,
            onValueChange = onFarmDescriptionChange,
            label = { Text("Farm Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        OutlinedTextField(
            value = alternatePhone,
            onValueChange = onAlternatePhoneChange,
            label = { Text("Alternate Phone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = postalAddress,
            onValueChange = onPostalAddressChange,
            label = { Text("Postal Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = primaryCrops,
            onValueChange = onPrimaryCropsChange,
            label = { Text("Primary Crops (comma separated)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = farmingExperience,
            onValueChange = onFarmingExperienceChange,
            label = { Text("Farming Experience (years)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = certifications,
            onValueChange = onCertificationsChange,
            label = { Text("Certifications (comma separated)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = farmName.isNotBlank() && fullName.isNotBlank() && phoneNumber.isNotBlank() && !isSubmitting
        ) {
            if (isSubmitting) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text("Saving...")
                }
            } else {
                Text(if (isEditMode) "Save Profile" else "Create Profile")
            }
        }

        // Debug info - remove after testing
        if (isSubmitting) {
            Text(
                text = "Debug: Button is in submitting state",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}