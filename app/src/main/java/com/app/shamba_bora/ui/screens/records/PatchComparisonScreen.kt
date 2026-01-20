package com.app.shamba_bora.ui.screens.records

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.PatchComparisonData
import com.app.shamba_bora.data.model.PatchComparisonResponse
import com.app.shamba_bora.data.model.MaizePatchDTO
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmAnalyticsViewModel
import com.app.shamba_bora.viewmodel.PatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchComparisonScreen(
    onBackClick: () -> Unit,
    analyticsViewModel: FarmAnalyticsViewModel = hiltViewModel(),
    patchViewModel: PatchViewModel = hiltViewModel()
) {
    val comparisonState by analyticsViewModel.patchComparison.collectAsState()
    val isLoading by analyticsViewModel.isLoading.collectAsState()
    val patchesState by patchViewModel.patchesState.collectAsState()
    
    var selectedPatchIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var showPatchSelector by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        patchViewModel.loadPatches()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            showPatchSelector -> {
                when (val state = patchesState) {
                    is Resource.Loading -> {
                        LoadingIndicator()
                    }
                    is Resource.Success -> {
                        val patches = state.data ?: emptyList()
                        if (patches.isEmpty()) {
                            NoPatchesScreen(onBackClick)
                        } else {
                            PatchSelectorScreen(
                                patches = patches,
                                selectedPatchIds = selectedPatchIds,
                                onSelectionChange = { selectedPatchIds = it },
                                onCompare = {
                                    if (selectedPatchIds.size >= 2) {
                                        analyticsViewModel.comparePatches(selectedPatchIds.toList())
                                        showPatchSelector = false
                                    }
                                },
                                onBackClick = onBackClick,
                                isLoading = isLoading
                            )
                        }
                    }
                    is Resource.Error -> {
                        ErrorScreen(
                            message = state.message ?: "Failed to load patches",
                            onRetry = { patchViewModel.loadPatches() }
                        )
                    }
                }
            }
            isLoading -> {
                LoadingIndicator()
            }
            comparisonState is Resource.Success -> {
                val comparison = (comparisonState as? Resource.Success<PatchComparisonResponse>)?.data
                if (comparison != null) {
                    PatchComparisonContent(
                        comparison = comparison,
                        onBackClick = onBackClick,
                        onSelectDifferent = { showPatchSelector = true }
                    )
                }
            }
            comparisonState is Resource.Error -> {
                val error = (comparisonState as? Resource.Error<PatchComparisonResponse>)?.message
                ErrorScreen(
                    message = error ?: "Failed to compare patches",
                    onRetry = {
                        if (selectedPatchIds.size >= 2) {
                            analyticsViewModel.comparePatches(selectedPatchIds.toList())
                        }
                    }
                )
            }
            else -> {
                // Initial loading state
                LoadingIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchSelectorScreen(
    patches: List<MaizePatchDTO>,
    selectedPatchIds: Set<Long>,
    onSelectionChange: (Set<Long>) -> Unit,
    onCompare: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        TopAppBar(
            title = { Text("Select Patches to Compare") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = patches,
                key = { it.id ?: 0L }
            ) { patch ->
                PatchChecklistItem(
                    patchId = patch.id ?: 0L,
                    patchName = patch.name,
                    patchLocation = patch.location,
                    patchArea = patch.area ?: 0.0,
                    isSelected = (patch.id ?: 0L) in selectedPatchIds,
                    onSelectionChange = { isSelected ->
                        val newSelection = selectedPatchIds.toMutableSet()
                        val patchId = patch.id ?: 0L
                        if (isSelected) {
                            newSelection.add(patchId)
                        } else {
                            newSelection.remove(patchId)
                        }
                        onSelectionChange(newSelection)
                    }
                )
            }
        }
        
        // Footer with Compare Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Button(
                onClick = onCompare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = selectedPatchIds.size >= 2 && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    "Compare (${selectedPatchIds.size} selected)",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PatchChecklistItem(
    patchId: Long,
    patchName: String,
    patchLocation: String,
    patchArea: Double,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelectionChange(!isSelected) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patchName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (patchLocation.isNotEmpty()) {
                        Text(
                            text = patchLocation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (patchArea > 0) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format("%.2f", patchArea)} ha",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchComparisonContent(
    comparison: PatchComparisonResponse,
    onBackClick: () -> Unit,
    onSelectDifferent: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        item {
            TopAppBar(
                title = { Text("Patch Comparison") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSelectDifferent) {
                        Icon(Icons.Default.Edit, contentDescription = "Select Different Patches")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        
        // Comparison cards for each patch
        items(comparison.patches) { patch ->
            PatchComparisonDetailCard(patch)
        }
        
        // Comparison table
        if (comparison.patches.size >= 2) {
            item {
                ComparisonMetricsTable(comparison.patches)
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PatchComparisonDetailCard(patch: PatchComparisonData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with patch name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Grain,
                        contentDescription = "Patch",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = patch.patchName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${patch.season} ${patch.year}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = patch.cropType,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Metrics Grid
            ComparisonMetricsGrid(patch)
        }
    }
}

@Composable
fun ComparisonMetricsGrid(patch: PatchComparisonData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1 - Area and Yield
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ComparisonMetricBox(
                label = "Area",
                value = "${String.format("%.1f", patch.area)} ${patch.areaUnit}",
                modifier = Modifier.weight(1f)
            )
            ComparisonMetricBox(
                label = "Total Yield",
                value = "${String.format("%.0f", patch.totalYield)} kg",
                modifier = Modifier.weight(1f)
            )
        }
        
        // Row 2 - Expenses and Revenue
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ComparisonMetricBox(
                label = "Expenses",
                value = formatCurrency(patch.totalExpenses),
                valueColor = 0xFFF44336,
                modifier = Modifier.weight(1f)
            )
            ComparisonMetricBox(
                label = "Revenue",
                value = formatCurrency(patch.totalRevenue),
                valueColor = 0xFF4CAF50,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Row 3 - Profit and Cost Per Kg
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ComparisonMetricBox(
                label = "Profit",
                value = formatCurrency(patch.profit),
                valueColor = 0xFF2196F3,
                modifier = Modifier.weight(1f)
            )
            ComparisonMetricBox(
                label = "Cost/Kg",
                value = formatCurrency(patch.costPerKg),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Row 4 - Profit Per Kg and ROI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ComparisonMetricBox(
                label = "Profit/Kg",
                value = formatCurrency(patch.profitPerKg),
                modifier = Modifier.weight(1f)
            )
            ComparisonMetricBox(
                label = "ROI",
                value = "${String.format("%.2f", patch.roiPercentage)}%",
                valueColor = 0xFF9C27B0,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Activities
        if (patch.activityTypes.isNotEmpty()) {
            Column {
                Text(
                    text = "Activities",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    patch.activityTypes.forEach { activity ->
                        Surface(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = activity,
                                modifier = Modifier.padding(6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
        
        // Expense Summaries
        if (patch.expenseSummaries.isNotEmpty()) {
            Column {
                Text(
                    text = "Expense Summary",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                patch.expenseSummaries.forEach { summary ->
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ComparisonMetricBox(
    label: String,
    value: String,
    valueColor: Long = 0xFF1976D2,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(valueColor).copy(alpha = 0.1f))
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(valueColor),
            maxLines = 2
        )
    }
}

@Composable
fun ComparisonMetricsTable(patches: List<PatchComparisonData>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Metrics Comparison Table",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Metric",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.5f)
                    )
                    patches.forEach { patch ->
                        Text(
                            text = patch.patchName.take(10),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Divider()
                
                // Metric rows
                val metrics = listOf(
                    "Area" to { p: PatchComparisonData -> "${String.format("%.1f", p.area)}" },
                    "Yield" to { p: PatchComparisonData -> "${String.format("%.0f", p.totalYield)}" },
                    "Profit" to { p: PatchComparisonData -> "${String.format("%.0f", p.profit)}" },
                    "Profit/Kg" to { p: PatchComparisonData -> "${String.format("%.2f", p.profitPerKg)}" },
                    "ROI %" to { p: PatchComparisonData -> "${String.format("%.2f", p.roiPercentage)}" }
                )
                
                metrics.forEach { (metricName, extractor) ->
                    Divider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = metricName,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1.5f),
                            fontWeight = FontWeight.SemiBold
                        )
                        patches.forEach { patch ->
                            Text(
                                text = extractor(patch),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoPatchesScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Dashboard,
            contentDescription = "No Patches",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Patches Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create patches first to compare them",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Go Back")
        }
    }
}
