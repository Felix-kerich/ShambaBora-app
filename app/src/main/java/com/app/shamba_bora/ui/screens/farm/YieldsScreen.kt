package com.app.shamba_bora.ui.screens.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = { Text("Yield Records") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        },
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No yield records yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Tap + to record your first harvest",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else{
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
                            imageVector = Icons.Default.Info,
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
            
            if ((yield.areaHarvested ?: 0.0) > 0.0 || yield.marketPrice != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if ((yield.areaHarvested ?: 0.0) > 0.0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${yield.areaHarvested} acres",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (yield.marketPrice != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "KES ${yield.marketPrice}/${yield.unit}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddYieldDialog(
    onDismiss: () -> Unit,
    onSave: (YieldRecord) -> Unit
) {
    var cropType by remember { mutableStateOf("") }
    var yieldAmount by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var marketPrice by remember { mutableStateOf("") }
    var areaHarvested by remember { mutableStateOf("") }
    var harvestDate by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedUnit by remember { mutableStateOf(false) }
    
    val units = listOf("kg", "tons", "bags", "pieces", "liters")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Yield Record") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = cropType,
                    onValueChange = { cropType = it },
                    label = { Text("Crop Type *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = harvestDate,
                    onValueChange = { },
                    label = { Text("Harvest Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = yieldAmount,
                        onValueChange = { yieldAmount = it },
                        label = { Text("Yield Amount *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedUnit,
                        onExpandedChange = { expandedUnit = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedUnit,
                            onDismissRequest = { expandedUnit = false }
                        ) {
                            units.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text(u) },
                                    onClick = {
                                        unit = u
                                        expandedUnit = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = areaHarvested,
                    onValueChange = { areaHarvested = it },
                    label = { Text("Area Harvested (acres)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = marketPrice,
                    onValueChange = { marketPrice = it },
                    label = { Text("Market Price per ${unit}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = yieldAmount.toDoubleOrNull() ?: 0.0
                    val price = marketPrice.toDoubleOrNull()
                    val area = areaHarvested.toDoubleOrNull()
                    onSave(YieldRecord(
                        cropType = cropType,
                        harvestDate = harvestDate,
                        yieldAmount = amount,
                        unit = unit,
                        marketPrice = price,
                        areaHarvested = area
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
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = java.time.Instant.ofEpochMilli(millis)
                            val date = java.time.LocalDate.ofInstant(instant, java.time.ZoneId.systemDefault())
                            harvestDate = date.toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
