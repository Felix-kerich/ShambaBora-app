package com.app.shamba_bora.ui.screens.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.app.shamba_bora.data.model.YieldRecord
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.YieldRecordViewModel

@Composable
fun YieldsScreen(
    onNavigateBack: () -> Unit,
    viewModel: YieldRecordViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val yieldsState by viewModel.yieldsState.collectAsState()
    val totalYieldState by viewModel.totalYieldState.collectAsState()
    val totalRevenueState by viewModel.totalRevenueState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadYieldRecords()
        viewModel.loadTotalYield()
        viewModel.loadTotalRevenue()
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Yield")
            }
        }
    ) { paddingValues ->
        when (val state = yieldsState) {
            is Resource.Loading -> {
                LoadingIndicator()
            }
            is Resource.Error -> {
                ErrorView(
                    message = state.message ?: "Failed to load yield records",
                    onRetry = { viewModel.loadYieldRecords() }
                )
            }
            is Resource.Success -> {
                val yields = state.data?.content ?: emptyList()
                val totalYield = when (val totalState = totalYieldState) {
                    is Resource.Success -> totalState.data ?: 0.0
                    else -> 0.0
                }
                val totalRevenue = when (val revenueState = totalRevenueState) {
                    is Resource.Success -> revenueState.data ?: 0.0
                    else -> 0.0
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Yield Records",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Track your harvest and yields",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Summary Cards
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Total Yield",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = "${String.format("%.2f", totalYield)}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                            
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Total Revenue",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "KES ${String.format("%.2f", totalRevenue)}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                    
                    if (yields.isEmpty()) {
                        item {
                            Text(
                                text = "No yield records yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(yields) { yield ->
                            YieldCard(yield = yield)
                        }
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddYieldDialog(
            onDismiss = { showAddDialog = false },
            onSave = { yield ->
                viewModel.createYieldRecord(yield)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun YieldCard(yield: YieldRecord) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = yield.cropType,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = yield.harvestDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${yield.yieldAmount} ${yield.unit}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "KES ${String.format("%.2f", (yield.marketPrice ?: 0.0) * (yield.yieldAmount ?: 0.0))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if ((yield.areaHarvested ?: 0.0) > 0.0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "Area: ${yield.areaHarvested} acres",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Price: KES ${yield.marketPrice}/${yield.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AddYieldDialog(
    onDismiss: () -> Unit,
    onSave: (YieldRecord) -> Unit
) {
    var cropType by remember { mutableStateOf("") }
    var yieldAmount by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Yield Record") },
        text = {
            Column {
                OutlinedTextField(
                    value = cropType,
                    onValueChange = { cropType = it },
                    label = { Text("Crop Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = yieldAmount,
                    onValueChange = { yieldAmount = it },
                    label = { Text("Yield Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = yieldAmount.toDoubleOrNull() ?: 0.0
                    onSave(YieldRecord(
                        id = null, // Will be set by the server/database
                        cropType = cropType,
                        harvestDate = java.time.LocalDate.now().toString(),
                        yieldAmount = amount,
                        unit = "kg", // Default unit, can be made configurable
                        marketPrice = 0.0, // Default value, can be made configurable
                        areaHarvested = 0.0 // Default value, can be made configurable
                    ))
                },
                enabled = cropType.isNotBlank() && yieldAmount.isNotBlank() && yieldAmount.toDoubleOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

data class YieldRecord(
    val id: Long,
    val cropType: String,
    val harvestDate: String,
    val yieldAmount: Double,
    val unit: String,
    val areaHarvested: Double,
    val marketPrice: Double
)

@Composable
fun getSampleYields(): List<YieldRecord> {
    return listOf(
        YieldRecord(1, "Maize", "2024-03-10", 500.0, "kg", 1.0, 120.0),
        YieldRecord(2, "Maize", "2024-03-15", 450.0, "kg", 1.0, 120.0),
        YieldRecord(3, "Maize", "2024-03-20", 250.0, "kg", 0.5, 125.0)
    )
}

