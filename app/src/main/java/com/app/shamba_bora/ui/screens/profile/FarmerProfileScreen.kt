package com.app.shamba_bora.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val farmerProfileState by viewModel.farmerProfileState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadFarmerProfile()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = farmerProfileState) {
            is Resource.Loading -> {
                LoadingIndicator()
            }
            is Resource.Error -> {
                ErrorView(
                    message = state.message ?: "Failed to load farmer profile",
                    onRetry = { viewModel.refreshFarmerProfile() }
                )
            }
            is Resource.Success -> {
                val profile = state.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        // Header Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = profile?.farmName ?: "Farm Name",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                profile?.farmDescription?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Farm Details Section
                    item {
                        Text(
                            text = "Farm Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    
                    item {
                        DetailCard(
                            icon = Icons.Default.LocationOn,
                            label = "Location",
                            value = profile?.location ?: "Not set"
                        )
                    }
                    
                    item {
                        DetailCard(
                            icon = Icons.Default.LocationOn,
                            label = "County",
                            value = profile?.county ?: "Not set"
                        )
                    }
                    
                    item {
                        DetailCard(
                            icon = Icons.Default.Build,
                            label = "Farm Size",
                            value = "${profile?.farmSize ?: 0.0} acres"
                        )
                    }
                    
                    item {
                        DetailCard(
                            icon = Icons.Default.MailOutline,
                            label = "Postal Address",
                            value = profile?.postalAddress ?: "Not set"
                        )
                    }
                    
                    item {
                        DetailCard(
                            icon = Icons.Default.Phone,
                            label = "Alternate Phone",
                            value = profile?.alternatePhone ?: "Not set"
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Farming Information Section
                    item {
                        Text(
                            text = "Farming Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    
                    item {
                        DetailCard(
                            icon = Icons.Default.Build,
                            label = "Primary Crops",
                            value = profile?.primaryCrops?.joinToString(", ") ?: "Not set"
                        )
                    }
                    
                    item {
                        DetailCard(
                            icon = Icons.Default.Build,
                            label = "Farming Experience",
                            value = "${profile?.farmingExperience ?: 0} years"
                        )
                    }
                    
                    item {
                        if (profile?.certifications?.isNotEmpty() == true) {
                            DetailCard(
                                icon = Icons.Default.Build,
                                label = "Certifications",
                                value = profile.certifications.joinToString(", ")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

