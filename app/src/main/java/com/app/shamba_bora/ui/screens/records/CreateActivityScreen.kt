package com.app.shamba_bora.ui.screens.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.ui.components.records.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmActivityViewModel
import java.time.LocalDate

@Composable
fun CreateActivityScreenWrapper(
    onBack: () -> Unit,
    viewModel: FarmActivityViewModel = hiltViewModel()
) {
    val patchesState by viewModel.patchesState.collectAsState()
    val createState by viewModel.createWithDTOState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadPatches()
    }
    
    LaunchedEffect(createState) {
        if (createState is Resource.Success) {
            onBack()
        }
    }
    
    val patches = when (val state = patchesState) {
        is Resource.Success -> state.data ?: emptyList()
        else -> emptyList()
    }
    
    val isLoading = createState is Resource.Loading
    
    CreateActivityScreen(
        patches = patches,
        onCreateActivity = { activity ->
            viewModel.createActivityWithDTO(activity)
        },
        onBack = onBack,
        isLoading = isLoading
    )
}

@Composable
fun CreateActivityScreen(
    patches: List<MaizePatchDTO> = emptyList(),
    onCreateActivity: (FarmActivityRequest) -> Unit = {},
    onBack: () -> Unit = {},
    isLoading: Boolean = false
) {
    var activityType by remember { mutableStateOf(ActivityType.PLANTING) }
    var cropType by remember { mutableStateOf("Maize") }
    var activityDate by remember { mutableStateOf(LocalDate.now()) }
    var description by remember { mutableStateOf("") }
    var areaSize by remember { mutableStateOf("") }
    var units by remember { mutableStateOf(AreaUnit.HA) }
    var productUsed by remember { mutableStateOf("") }
    var applicationRate by remember { mutableStateOf("") }
    var weatherConditions by remember { mutableStateOf(WeatherCondition.SUNNY) }
    var soilConditions by remember { mutableStateOf(SoilCondition.WELL_DRAINED) }
    var cost by remember { mutableStateOf("") }
    var laborHours by remember { mutableStateOf("") }
    var laborCost by remember { mutableStateOf("") }
    var equipmentUsed by remember { mutableStateOf("") }
    var equipmentCost by remember { mutableStateOf("") }
    var selectedPatchId by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }
    var seedVarietyName by remember { mutableStateOf("") }
    var fertilizerProductName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text(
                    text = "Record Farm Activity",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track your farming operations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Basic Info Section
            item {
                FormSection(title = "Basic Information") {
                    ActivityTypeDropdown(
                        selectedType = activityType,
                        onTypeChange = { activityType = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormDateField(
                        label = "Activity Date",
                        selectedDate = activityDate,
                        onDateChange = { activityDate = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormTextField(
                        label = "Description",
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Describe the activity",
                        isRequired = true,
                        minLines = 2,
                        maxLines = 3
                    )
                }
            }

            // Location & Area
            item {
                FormSection(title = "Location & Area") {
                    FormTextField(
                        label = "Location",
                        value = "",
                        onValueChange = {},
                        placeholder = "Field name or plot number"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FormNumberField(
                            label = "Area Size",
                            value = areaSize,
                            onValueChange = { areaSize = it },
                            placeholder = "e.g., 1.5",
                            modifier = Modifier.weight(1f)
                        )
                        AreaUnitDropdown(
                            selectedUnit = units,
                            onUnitChange = { units = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Patch Selection
            item {
                FormSection(title = "Select Patch") {
                    PatchSelectorDropdown(
                        patches = patches,
                        selectedPatchId = selectedPatchId,
                        onPatchSelect = { selectedPatchId = it }
                    )
                }
            }

            // Products & Inputs
            item {
                FormSection(title = "Products & Inputs") {
                    FormTextField(
                        label = "Product Used",
                        value = productUsed,
                        onValueChange = { productUsed = it },
                        placeholder = "e.g., Urea, Insecticide"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormNumberField(
                        label = "Application Rate (per unit area)",
                        value = applicationRate,
                        onValueChange = { applicationRate = it },
                        placeholder = "e.g., 40"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormTextField(
                        label = "Seed Variety (if planting)",
                        value = seedVarietyName,
                        onValueChange = { seedVarietyName = it },
                        placeholder = "e.g., H511"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormTextField(
                        label = "Fertilizer Used (if applicable)",
                        value = fertilizerProductName,
                        onValueChange = { fertilizerProductName = it },
                        placeholder = "e.g., NPK 23-23-0"
                    )
                }
            }

            // Weather & Soil Conditions
            item {
                FormSection(title = "Conditions") {
                    WeatherConditionDropdown(
                        selectedCondition = weatherConditions,
                        onConditionChange = { weatherConditions = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SoilConditionDropdown(
                        selectedCondition = soilConditions,
                        onConditionChange = { soilConditions = it }
                    )
                }
            }

            // Costs
            item {
                FormSection(title = "Activity Costs") {
                    FormNumberField(
                        label = "Total Cost",
                        value = cost,
                        onValueChange = { cost = it },
                        placeholder = "Total amount spent"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormNumberField(
                        label = "Labor Hours",
                        value = laborHours,
                        onValueChange = { laborHours = it },
                        placeholder = "Number of hours"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormNumberField(
                        label = "Labor Cost",
                        value = laborCost,
                        onValueChange = { laborCost = it },
                        placeholder = "Cost of labor"
                    )
                }
            }

            // Advanced Options
            item {
                AdvancedOptionsSection(title = "Additional Details") {
                    FormTextField(
                        label = "Equipment Used",
                        value = equipmentUsed,
                        onValueChange = { equipmentUsed = it },
                        placeholder = "e.g., Tractor, Sprayer"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormNumberField(
                        label = "Equipment Cost",
                        value = equipmentCost,
                        onValueChange = { equipmentCost = it },
                        placeholder = "Cost for equipment"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormTextField(
                        label = "Notes",
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = "Additional observations",
                        minLines = 2,
                        maxLines = 3
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Submit Button
        FormSubmitButton(
            text = "Save Activity",
            onClick = {
                val activity = FarmActivityRequest(
                    activityType = activityType.name,
                    cropType = cropType,
                    activityDate = activityDate,
                    description = description,
                    areaSize = areaSize.toDoubleOrNull(),
                    units = units.name,
                    productUsed = productUsed,
                    applicationRate = applicationRate.toDoubleOrNull(),
                    weatherConditions = weatherConditions.name,
                    soilConditions = soilConditions.name,
                    cost = cost.toDoubleOrNull(),
                    laborHours = laborHours.toIntOrNull(),
                    laborCost = laborCost.toDoubleOrNull(),
                    equipmentUsed = equipmentUsed,
                    equipmentCost = equipmentCost.toDoubleOrNull(),
                    seedVarietyName = seedVarietyName,
                    fertilizerProductName = fertilizerProductName,
                    patchId = selectedPatchId,
                    notes = notes
                )
                if (activity.isValid()) {
                    onCreateActivity(activity)
                }
            },
            enabled = description.isNotBlank() && activityType != null,
            isLoading = isLoading
        )
    }
}
