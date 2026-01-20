package com.app.shamba_bora.ui.screens.records

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmAnalyticsViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FarmAnalyticsScreen(
    onNavigateToPatchComparison: () -> Unit,
    viewModel: FarmAnalyticsViewModel = hiltViewModel()
) {
    val analyticsState by viewModel.farmAnalytics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Date filter state
    var startDate by remember { mutableStateOf<String?>(null) }
    var endDate by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(startDate, endDate) {
        viewModel.getFarmAnalytics(startDate = startDate, endDate = endDate)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (analyticsState) {
            is Resource.Loading -> {
                LoadingIndicator()
            }
            is Resource.Success -> {
                val analytics = (analyticsState as? Resource.Success<FarmAnalyticsDTO>)?.data
                if (analytics != null) {
                    FarmAnalyticsContent(
                        analytics = analytics,
                        onNavigateToPatchComparison = onNavigateToPatchComparison,
                        viewModel = viewModel,
                        startDate = startDate,
                        endDate = endDate,
                        onStartDateChange = { startDate = it },
                        onEndDateChange = { endDate = it }
                    )
                }
            }
            is Resource.Error -> {
                val errorMessage = (analyticsState as? Resource.Error<FarmAnalyticsDTO>)?.message
                ErrorScreen(
                    message = errorMessage ?: "Failed to load analytics",
                    onRetry = { viewModel.getFarmAnalytics() }
                )
            }
        }
        
        if (error != null) {
            LaunchedEffect(error) {
                // Show error message
            }
        }
    }
}

@Composable
fun FarmAnalyticsContent(
    analytics: FarmAnalyticsDTO,
    onNavigateToPatchComparison: () -> Unit,
    viewModel: FarmAnalyticsViewModel,
    startDate: String?,
    endDate: String?,
    onStartDateChange: (String?) -> Unit,
    onEndDateChange: (String?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        item {
            FarmAnalyticsHeader(analytics)
        }
        
        // Date Filter Section
        item {
            DateFilterSection(
                startDate = startDate,
                endDate = endDate,
                onStartDateChange = onStartDateChange,
                onEndDateChange = onEndDateChange
            )
        }
        
        // Key Metrics Overview
        item {
            KeyMetricsOverview(analytics, viewModel)
        }
        
        // Expense Breakdown
        if (analytics.expensesByCategory.isNotEmpty()) {
            item {
                ExpenseBreakdownSection(analytics, viewModel)
            }
        }
        
        // Yield Trends
        if (analytics.yieldTrends.isNotEmpty()) {
            item {
                YieldTrendsSection(analytics, viewModel)
            }
        }
        
        // Patches Analytics Summary
        if (analytics.patchesAnalytics.totalPatches > 0) {
            item {
                PatchesAnalyticsSummary(analytics, viewModel)
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateToPatchComparison,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Compare Patches",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Compare Patches",
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        // Recommendations
        if (analytics.recommendations.isNotEmpty()) {
            item {
                RecommendationsSection(analytics)
            }
        }
    }
}

@Composable
fun FarmAnalyticsHeader(analytics: FarmAnalyticsDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Farm Analytics",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${analytics.analysisPeriodStart} to ${analytics.analysisPeriodEnd}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Crop: ${analytics.cropType.uppercase()}",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFilterSection(
    startDate: String?,
    endDate: String?,
    onStartDateChange: (String?) -> Unit,
    onEndDateChange: (String?) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displayFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date Filter",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Filter by Date Range",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start Date Picker
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStartDatePicker = true },
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "From",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = startDate?.let {
                                try {
                                    displayFormatter.format(dateFormatter.parse(it) ?: Date())
                                } catch (e: Exception) {
                                    "Select Date"
                                }
                            } ?: "Select Date",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // End Date Picker
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showEndDatePicker = true },
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "To",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = endDate?.let {
                                try {
                                    displayFormatter.format(dateFormatter.parse(it) ?: Date())
                                } catch (e: Exception) {
                                    "Select Date"
                                }
                            } ?: "Select Date",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Clear filters button
            if (startDate != null || endDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        onStartDateChange(null)
                        onEndDateChange(null)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Filters")
                }
            }
        }
    }
    
    // Date Picker Dialogs
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            onStartDateChange(dateFormatter.format(date))
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            onEndDateChange(dateFormatter.format(date))
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Composable
fun KeyMetricsOverview(
    analytics: FarmAnalyticsDTO,
    viewModel: FarmAnalyticsViewModel
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Key Metrics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // First Row - Revenue and Expenses
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Revenue",
                value = formatCurrency(analytics.totalRevenue),
                icon = Icons.Default.TrendingUp,
                color = 0xFF4CAF50,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Total Expenses",
                value = formatCurrency(analytics.totalExpenses),
                icon = Icons.Default.TrendingDown,
                color = 0xFFF44336,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Second Row - Profit and Margin
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Net Profit",
                value = formatCurrency(analytics.netProfit),
                icon = Icons.Default.Savings,
                color = 0xFF2196F3,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Profit Margin",
                value = "${String.format("%.2f", analytics.profitMargin)}%",
                icon = Icons.Default.Assessment,
                color = 0xFF9C27B0,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Third Row - Yield Metrics
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Yield",
                value = "${String.format("%.0f", analytics.totalYield)} kg",
                icon = Icons.Default.Inventory2,
                color = 0xFF00BCD4,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Avg. Yield/Unit",
                value = "${String.format("%.2f", analytics.averageYieldPerUnit)} kg",
                icon = Icons.Default.ShowChart,
                color = 0xFFFF9800,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(color).copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, Color(color).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    tint = Color(color)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(color),
                maxLines = 2
            )
        }
    }
}

@Composable
fun ExpenseBreakdownSection(
    analytics: FarmAnalyticsDTO,
    viewModel: FarmAnalyticsViewModel
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Expense Breakdown",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        val breakdown = viewModel.calculateExpenseBreakdown(analytics.expensesByCategory)
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                breakdown.forEachIndexed { index, expense ->
                    if (index > 0) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = expense.category,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${String.format("%.1f", expense.percentage)}% of total",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = formatCurrency(expense.amount),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YieldTrendsSection(
    analytics: FarmAnalyticsDTO,
    viewModel: FarmAnalyticsViewModel
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Yield Trends",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            items(analytics.yieldTrends) { trend ->
                YieldTrendCard(trend, viewModel)
            }
        }
    }
}

@Composable
fun YieldTrendCard(
    trend: YieldTrend,
    viewModel: FarmAnalyticsViewModel
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = trend.period,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Icon(
                imageVector = when (trend.trend) {
                    "INCREASING" -> Icons.Default.KeyboardArrowUp
                    "DECREASING" -> Icons.Default.KeyboardArrowDown
                    else -> Icons.Default.HorizontalRule
                },
                contentDescription = trend.trend,
                modifier = Modifier.size(24.dp),
                tint = Color(viewModel.getTrendColor(trend.trend))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${String.format("%.0f", trend.yield)} kg",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "${String.format("%.2f", trend.yieldPerUnit)}/unit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PatchesAnalyticsSummary(
    analytics: FarmAnalyticsDTO,
    viewModel: FarmAnalyticsViewModel
) {
    var expandedSummary by remember { mutableStateOf(true) }
    var expandedDetails by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Patch Performance Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        val patchesData = analytics.patchesAnalytics
        
        // Total Patches Info - Always Visible
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoPill(
                label = "Total Patches",
                value = "${patchesData.totalPatches}",
                color = 0xFF2196F3
            )
            InfoPill(
                label = "Analyzed",
                value = "${patchesData.patchesAnalyzed}",
                color = 0xFF4CAF50
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Collapsible Summary Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedSummary = !expandedSummary }
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Top Performing Patches",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (expandedSummary) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                if (expandedSummary) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Best Performing Patch
                    if (patchesData.bestPerformingPatch != null) {
                        PatchPerformanceCard(
                            title = "Best Performing",
                            patch = patchesData.bestPerformingPatch,
                            color = 0xFF4CAF50
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Most Profitable Patch
                    if (patchesData.mostProfitablePatch != null) {
                        PatchPerformanceCard(
                            title = "Most Profitable",
                            patch = patchesData.mostProfitablePatch,
                            color = 0xFF2196F3
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Worst Performing Patch
                    if (patchesData.worstPerformingPatch != null) {
                        PatchPerformanceCard(
                            title = "Needs Attention",
                            patch = patchesData.worstPerformingPatch,
                            color = 0xFFFF9800
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Collapsible Details Section
        if (patchesData.allPatchesAnalytics.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedDetails = !expandedDetails }
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "All Patches (${patchesData.allPatchesAnalytics.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (expandedDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    if (expandedDetails) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        patchesData.allPatchesAnalytics.forEachIndexed { index, patch ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            PatchDetailCard(patch)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatchPerformanceCard(
    title: String,
    patch: PatchAnalytics,
    color: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, Color(color).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(color),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = patch.patchName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (patch.season.isNotEmpty()) {
                        Text(
                            text = "${patch.season} • ${patch.year}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color(color).copy(alpha = 0.1f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = patch.performanceRating.take(1),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(color)
                        )
                    }
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Location & Area (Simple)
            if (patch.location.isNotEmpty() || patch.area > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (patch.location.isNotEmpty()) {
                        MetricItem(
                            label = "Location",
                            value = patch.location
                        )
                    }
                    if (patch.area > 0) {
                        MetricItem(
                            label = "Area",
                            value = "${String.format("%.2f", patch.area)} ${patch.areaUnit}"
                        )
                    }
                }
            }
            
            // Financial Metrics - 2x2 Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    label = "Revenue",
                    value = formatCurrency(patch.totalRevenue)
                )
                MetricItem(
                    label = "Expenses",
                    value = formatCurrency(patch.totalExpenses)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    label = "Net Profit",
                    value = formatCurrency(patch.netProfit)
                )
                MetricItem(
                    label = "Profit Margin",
                    value = "${String.format("%.2f", patch.profitMargin)}%"
                )
            }
            
            // Yield Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    label = "Total Yield",
                    value = "${String.format("%.0f", patch.totalYield)} kg"
                )
                MetricItem(
                    label = "Yield/Ha",
                    value = "${String.format("%.2f", patch.yieldPerHectare)} kg"
                )
            }
        }
    }
}

@Composable
fun CostBreakdownItem(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PatchDetailCard(patch: PatchAnalytics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patch.patchName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (patch.season.isNotEmpty()) {
                        Text(
                            text = "${patch.season} • ${patch.year}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (patch.performanceRating) {
                                "EXCELLENT" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                                "GOOD" -> Color(0xFF2196F3).copy(alpha = 0.1f)
                                "AVERAGE" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                                else -> Color(0xFFF44336).copy(alpha = 0.1f)
                            }
                        ),
                    color = Color.Transparent
                ) {
                    Text(
                        text = patch.performanceRating,
                        modifier = Modifier.padding(6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (patch.performanceRating) {
                            "EXCELLENT" -> Color(0xFF4CAF50)
                            "GOOD" -> Color(0xFF2196F3)
                            "AVERAGE" -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Key Metrics - Simplified Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CompactMetricBox("Revenue", formatCurrency(patch.totalRevenue), 0xFF4CAF50, Modifier.weight(1f))
                CompactMetricBox("Expenses", formatCurrency(patch.totalExpenses), 0xFFF44336, Modifier.weight(1f))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CompactMetricBox("Profit", formatCurrency(patch.netProfit), 0xFF2196F3, Modifier.weight(1f))
                CompactMetricBox("Yield", "${String.format("%.0f", patch.totalYield)} kg", 0xFF00BCD4, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun CompactMetricItem(label: String, value: String) {
    Column(modifier = Modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun CompactMetricBox(
    label: String,
    value: String,
    color: Long,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(color).copy(alpha = 0.1f))
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(color),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(color),
            maxLines = 1
        )
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun InfoPill(label: String, value: String, color: Long) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(color).copy(alpha = 0.1f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(color),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(color)
        )
    }
}

@Composable
fun RecommendationsSection(analytics: FarmAnalyticsDTO) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "AI Recommendations",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                analytics.recommendations.forEachIndexed { index, recommendation ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Recommendation",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading Analytics...")
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Failed to Load Analytics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
    return format.format(amount).replace("KES", "").trim()
}
