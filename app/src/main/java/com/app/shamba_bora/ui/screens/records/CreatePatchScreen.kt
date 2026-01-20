@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import com.app.shamba_bora.ui.components.records.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.PatchViewModel
import java.time.LocalDate

@Composable
fun CreatePatchScreenWrapper(
    onBack: () -> Unit,
    viewModel: PatchViewModel = hiltViewModel()
) {
    val createState by viewModel.createState.collectAsState()

    LaunchedEffect(createState) {
        if (createState is Resource.Success) {
            onBack()
        }
    }

    val isLoading = createState is Resource.Loading

    CreatePatchScreen(
        onCreatePatch = { patch -> viewModel.createPatch(patch) },
        onBack = onBack,
        isLoading = isLoading
    )
}

@Composable
fun CreatePatchScreen(
    onCreatePatch: (MaizePatchDTO) -> Unit = {},
    onBack: () -> Unit = {},
    isLoading: Boolean = false
) {
    var patchName by remember { mutableStateOf("") }
    var year by remember { mutableStateOf(LocalDate.now().year.toString()) }
    var season by remember { mutableStateOf(Season.LONG_RAIN) }
    var areaUnit by remember { mutableStateOf(AreaUnit.HA) }
    var area by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var plantingDate by remember { mutableStateOf(LocalDate.now()) }
    var expectedHarvestDate by remember { mutableStateOf(LocalDate.now().plusMonths(5)) }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Patch") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    FormTextField(
                        label = "Patch Name",
                        value = patchName,
                        onValueChange = { patchName = it },
                        placeholder = "e.g., Block A - 2025",
                        isRequired = true
                    )
                }

                item {
                    LocationPickerField(
                        label = "Location",
                        location = location,
                        onLocationChange = { location = it },
                        placeholder = "Field name or use GPS",
                        isRequired = true
                    )
                }

                item {
                    FormTextField(
                        label = "Year",
                        value = year,
                        onValueChange = { if (it.length <= 4) year = it },
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                }

                item {
                    SeasonDropdown(
                        selectedSeason = season,
                        onSeasonChange = { season = it }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FormTextField(
                            label = "Area Size",
                            value = area,
                            onValueChange = { area = it },
                            placeholder = "e.g., 1.5",
                            modifier = Modifier.weight(1f),
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        )
                        AreaUnitDropdown(
                            selectedUnit = areaUnit,
                            onUnitChange = { areaUnit = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    FormDateField(
                        label = "Planting Date",
                        selectedDate = plantingDate,
                        onDateChange = { plantingDate = it }
                    )
                }

                item {
                    FormDateField(
                        label = "Expected Harvest Date",
                        selectedDate = expectedHarvestDate,
                        onDateChange = { expectedHarvestDate = it }
                    )
                }

                item {
                    FormTextField(
                        label = "Notes",
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = "Additional details...",
                        minLines = 2,
                        maxLines = 3
                    )
                }
            }

            FormSubmitButton(
                text = "Create Patch",
                onClick = {
                    val patch = MaizePatchDTO(
                        name = patchName,
                        year = year.toIntOrNull() ?: LocalDate.now().year,
                        season = season.name,
                        areaUnit = areaUnit.name,
                        area = area.toDoubleOrNull(),
                        location = location,
                        plantingDate = plantingDate,
                        expectedHarvestDate = expectedHarvestDate,
                        notes = notes
                    )
                    onCreatePatch(patch)
                },
                enabled = patchName.isNotBlank() && location.isNotBlank(),
                isLoading = isLoading
            )
        }
    }
}
