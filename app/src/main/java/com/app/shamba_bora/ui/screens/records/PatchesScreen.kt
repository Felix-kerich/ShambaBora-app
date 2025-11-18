package com.app.shamba_bora.ui.screens.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.records.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.PatchViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit = {},
    viewModel: PatchViewModel = hiltViewModel()
) {
    val patchesState by viewModel.patchesState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadPatches()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Patches") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Patch")
            }
        }
    ) { paddingValues ->
        when (val state = patchesState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }
            is Resource.Error -> {
                ErrorView(
                    message = state.message ?: "Failed to load patches",
                    onRetry = { viewModel.loadPatches() }
                )
            }
            is Resource.Success -> {
                val patches = state.data ?: emptyList()
                displayPatchesContent(patches, paddingValues, viewModel)
            }
        }
    }
}

@Composable
private fun displayPatchesContent(
    patches: List<MaizePatchDTO>,
    paddingValues: PaddingValues,
    viewModel: PatchViewModel
) {
    when {
        patches.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "No patches",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No patches created yet",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Create a new patch to start tracking your farm plots",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "${patches.size} Patches",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Your farm plots",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                    
                    items(patches) { patch ->
                        PatchCard(
                            patch = patch,
                            onSelect = {},
                            onDelete = { patch.id?.let { viewModel.deletePatch(it) } }
                        )
                    }
                }
            }
        }
}

@Composable
fun PatchCard(
    patch: MaizePatchDTO,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Patch info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patch.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PatchBadge(label = patch.season, icon = Icons.Default.Info)
                    patch.area?.let {
                        PatchBadge(label = "$it ${patch.areaUnit}", icon = Icons.Default.Settings)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Year: ${patch.year}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onSelect,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PatchBadge(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = Modifier
            .height(24.dp)
            .padding(horizontal = 4.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CreatePatchDialog(
    onConfirm: (MaizePatchDTO) -> Unit,
    onDismiss: () -> Unit
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Patch") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
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
                    FormTextField(
                        label = "Location",
                        value = location,
                        onValueChange = { location = it },
                        placeholder = "e.g., Field 1",
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
        },
        confirmButton = {
            Button(
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
                    onConfirm(patch)
                },
                enabled = patchName.isNotBlank() && location.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
