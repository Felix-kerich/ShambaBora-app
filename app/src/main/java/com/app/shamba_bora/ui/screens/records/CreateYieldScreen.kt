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
import com.app.shamba_bora.data.constants.FarmingInputs
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.ui.components.records.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.YieldRecordViewModel
import java.time.LocalDate

@Composable
fun CreateYieldScreenWrapper(
    onBack: () -> Unit,
    viewModel: YieldRecordViewModel = hiltViewModel()
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
    
    CreateYieldScreen(
        patches = patches,
        onCreateYield = { yield ->
            viewModel.createYieldRecordWithDTO(yield)
        },
        onBack = onBack,
        isLoading = isLoading
    )
}

@Composable
fun CreateYieldScreen(
    patches: List<MaizePatchDTO> = emptyList(),
    onCreateYield: (YieldRecordRequest) -> Unit = {},
    onBack: () -> Unit = {},
    isLoading: Boolean = false
) {
    var cropType by remember { mutableStateOf("Maize") }
    var harvestDate by remember { mutableStateOf(LocalDate.now()) }
    var yieldAmount by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(YieldUnit.KG) }
    var areaHarvested by remember { mutableStateOf("") }
    var marketPrice by remember { mutableStateOf("") }
    var qualityGrade by remember { mutableStateOf(QualityGrade.GRADE_A) }
    var storageLocation by remember { mutableStateOf("") }
    var buyer by remember { mutableStateOf("") }
    var selectedPatchId by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }

    // Calculate derived values
    val yieldPerUnit = if ((areaHarvested.toDoubleOrNull() ?: 0.0) > 0 && (yieldAmount.toDoubleOrNull() ?: 0.0) > 0) {
        (yieldAmount.toDoubleOrNull() ?: 0.0) / (areaHarvested.toDoubleOrNull() ?: 1.0)
    } else {
        0.0
    }

    val totalRevenue = ((yieldAmount.toDoubleOrNull() ?: 0.0) * (marketPrice.toDoubleOrNull() ?: 0.0))

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
                    text = "Record Harvest Yield",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Document your harvest results",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Harvest Information
            item {
                FormSection(title = "Harvest Information") {
                    FormDateField(
                        label = "Harvest Date",
                        selectedDate = harvestDate,
                        onDateChange = { harvestDate = it },
                        isRequired = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FormNumberField(
                            label = "Yield Amount",
                            value = yieldAmount,
                            onValueChange = { yieldAmount = it },
                            placeholder = "e.g., 2000",
                            isRequired = true,
                            modifier = Modifier.weight(1f)
                        )
                        YieldUnitDropdown(
                            selectedUnit = unit,
                            onUnitChange = { unit = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Area & Yield Calculation
            item {
                FormSection(title = "Harvest Area") {
                    FormNumberField(
                        label = "Area Harvested",
                        value = areaHarvested,
                        onValueChange = { areaHarvested = it },
                        placeholder = "e.g., 1.25 (in hectares)"
                    )
                    if (areaHarvested.isNotEmpty() && (areaHarvested.toDoubleOrNull() ?: 0.0) > 0 && yieldAmount.isNotEmpty() && (yieldAmount.toDoubleOrNull() ?: 0.0) > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Calculated Yield per Unit Area",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = String.format("%.2f ${unit.displayName}/hectare", yieldPerUnit),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Quality & Grading
            item {
                FormSection(title = "Quality & Grading") {
                    QualityGradeDropdown(
                        selectedGrade = qualityGrade,
                        onGradeChange = { qualityGrade = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchableDropdown(
                        label = "Storage Location",
                        value = storageLocation,
                        onValueChange = { storageLocation = it },
                        options = FarmingInputs.STORAGE_LOCATIONS,
                        placeholder = "Select or type storage...",
                        allowCustomInput = true
                    )
                }
            }

            // Market Information
            item {
                FormSection(title = "Market Information") {
                    FormNumberField(
                        label = "Market Price per Unit",
                        value = marketPrice,
                        onValueChange = { marketPrice = it },
                        placeholder = "e.g., 40.00"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchableDropdown(
                        label = "Buyer Name",
                        value = buyer,
                        onValueChange = { buyer = it },
                        options = FarmingInputs.BUYERS,
                        placeholder = "Select or type buyer...",
                        allowCustomInput = true
                    )

                    if (marketPrice.isNotEmpty() && (marketPrice.toDoubleOrNull() ?: 0.0) > 0 && yieldAmount.isNotEmpty() && (yieldAmount.toDoubleOrNull() ?: 0.0) > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Projected Revenue",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = String.format("KES %.2f", totalRevenue),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Patch Selection
            item {
                FormSection(title = "Assign to Patch") {
                    PatchSelectorDropdown(
                        patches = patches,
                        selectedPatchId = selectedPatchId,
                        onPatchSelect = { selectedPatchId = it }
                    )
                }
            }

            // Advanced Options
            item {
                AdvancedOptionsSection(title = "Additional Details") {
                    FormTextField(
                        label = "Notes",
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = "Any observations or issues?",
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
            text = "Save Yield Record",
            onClick = {
                val yield = YieldRecordRequest(
                    cropType = cropType,
                    harvestDate = harvestDate,
                    yieldAmount = yieldAmount.toDoubleOrNull() ?: 0.0,
                    unit = unit.name,
                    areaHarvested = areaHarvested.toDoubleOrNull(),
                    marketPrice = marketPrice.toDoubleOrNull(),
                    qualityGrade = qualityGrade.name,
                    storageLocation = storageLocation,
                    buyer = buyer,
                    patchId = selectedPatchId,
                    notes = notes
                )
                if (yield.isValid()) {
                    onCreateYield(yield)
                }
            },
            enabled = yieldAmount.isNotBlank() && (yieldAmount.toDoubleOrNull() ?: 0.0) > 0,
            isLoading = isLoading
        )
    }
}
