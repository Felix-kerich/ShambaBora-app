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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.ui.components.records.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.YieldRecordViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YieldsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToYieldDetail: (Long) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    viewModel: YieldRecordViewModel = hiltViewModel()
) {
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
                onClick = { onNavigateToCreate() },
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
                            YieldCard(
                                yield = yield,
                                onClick = { yield.id?.let { onNavigateToYieldDetail(it) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YieldCard(
    yield: YieldRecord,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
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
    var yieldUnit by remember { mutableStateOf(YieldUnit.KG) }
    var marketPrice by remember { mutableStateOf("") }
    var areaHarvested by remember { mutableStateOf("") }
    var areaUnit by remember { mutableStateOf(AreaUnit.ACRES) }
    var harvestDate by remember { mutableStateOf(LocalDate.now()) }
    var qualityGrade by remember { mutableStateOf(QualityGrade.GRADE_A) }
    var notes by remember { mutableStateOf("") }
    
    // Auto-calculated fields
    val yieldValue = yieldAmount.toDoubleOrNull() ?: 0.0
    val areaValue = areaHarvested.toDoubleOrNull() ?: 0.0
    val priceValue = marketPrice.toDoubleOrNull() ?: 0.0
    
    val yieldPerUnit = if (areaValue > 0) yieldValue / areaValue else 0.0
    val projectedRevenue = yieldValue * priceValue
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Yield Record") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    FormTextField(
                        label = "Crop Type",
                        value = cropType,
                        onValueChange = { cropType = it },
                        isRequired = true
                    )
                }
                
                item {
                    FormDateField(
                        label = "Harvest Date",
                        selectedDate = harvestDate,
                        onDateChange = { harvestDate = it }
                    )
                }
                
                item {
                    FormFieldLabel(text = "Yield & Unit", isRequired = true)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FormNumberField(
                            label = "Amount",
                            value = yieldAmount,
                            onValueChange = { yieldAmount = it },
                            modifier = Modifier.weight(1f)
                        )
                        
                        Box(modifier = Modifier.weight(0.8f)) {
                            YieldUnitDropdown(
                                selectedUnit = yieldUnit,
                                onUnitChange = { yieldUnit = it }
                            )
                        }
                    }
                }
                
                item {
                    FormFieldLabel(text = "Area Harvested", isRequired = false)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FormNumberField(
                            label = "Area",
                            value = areaHarvested,
                            onValueChange = { areaHarvested = it },
                            modifier = Modifier.weight(1f)
                        )
                        
                        Box(modifier = Modifier.weight(0.8f)) {
                            AreaUnitDropdown(
                                selectedUnit = areaUnit,
                                onUnitChange = { areaUnit = it }
                            )
                        }
                    }
                }
                
                item {
                    FormNumberField(
                        label = "Market Price (KES per ${yieldUnit.displayName})",
                        value = marketPrice,
                        onValueChange = { marketPrice = it },
                        placeholder = "0.00",
                        keyboardType = KeyboardType.Decimal
                    )
                }
                
                item {
                    FormFieldLabel(text = "Quality Grade", isRequired = false)
                    Spacer(modifier = Modifier.height(4.dp))
                    QualityGradeDropdown(
                        selectedGrade = qualityGrade,
                        onGradeChange = { qualityGrade = it }
                    )
                }
                
                item {
                    // Auto-calculated metrics
                    if (areaValue > 0 || priceValue > 0) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (areaValue > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Yield per ${areaUnit.displayName}:",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "${String.format("%.2f", yieldPerUnit)} ${yieldUnit.displayName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                if (priceValue > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Projected Revenue:",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "KES ${String.format("%.2f", projectedRevenue)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    FormTextField(
                        label = "Notes",
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = "Additional yield details...",
                        minLines = 2,
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (cropType.isNotBlank() && yieldAmount.isNotBlank()) {
                        onSave(
                            YieldRecord(
                                cropType = cropType,
                                harvestDate = harvestDate.toString(),
                                yieldAmount = yieldAmount.toDoubleOrNull() ?: 0.0,
                                unit = yieldUnit.name,
                                marketPrice = if (marketPrice.isNotEmpty()) marketPrice.toDoubleOrNull() else null,
                                areaHarvested = if (areaHarvested.isNotEmpty()) areaHarvested.toDoubleOrNull() else null,
                                qualityGrade = qualityGrade.name,
                                notes = notes.ifEmpty { null }
                            )
                        )
                    }
                },
                enabled = cropType.isNotBlank() && yieldAmount.isNotBlank()
            ) {
                Text("Save Yield Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
