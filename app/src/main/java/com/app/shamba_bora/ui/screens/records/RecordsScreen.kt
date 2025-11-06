package com.app.shamba_bora.ui.screens.records

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
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmActivityViewModel
import com.app.shamba_bora.viewmodel.FarmExpenseViewModel
import com.app.shamba_bora.viewmodel.YieldRecordViewModel

data class RecordCategory(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    val count: Int,
    val onClick: () -> Unit
)

@Composable
fun RecordCategoryCard(
    category: RecordCategory
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = category.onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = MaterialTheme.shapes.large,
                color = category.color.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    modifier = Modifier.padding(16.dp),
                    tint = category.color
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${category.count} records",
                    style = MaterialTheme.typography.bodySmall,
                    color = category.color,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecordCategoriesSection(
    onNavigateToActivities: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToYields: () -> Unit,
    activitiesCount: Int,
    expensesCount: Int,
    yieldsCount: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    
    val categories = listOf(
        RecordCategory(
            "Farm Activities",
            "Track planting, harvesting, and other farm operations",
            Icons.Default.Info,
            colorScheme.primary,
            activitiesCount,
            onNavigateToActivities
        ),
        RecordCategory(
            "Expenses",
            "Record and manage your farming expenses",
            Icons.Default.Info,
            colorScheme.error,
            expensesCount,
            onNavigateToExpenses
        ),
        RecordCategory(
            "Yields",
            "Log your harvest and yield information",
            Icons.Default.Info,
            colorScheme.tertiary,
            yieldsCount,
            onNavigateToYields
        )
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        categories.forEach { category ->
            RecordCategoryCard(category = category)
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
    onNavigateToActivities: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToYields: () -> Unit,
    activityViewModel: FarmActivityViewModel = hiltViewModel(),
    expenseViewModel: FarmExpenseViewModel = hiltViewModel(),
    yieldViewModel: YieldRecordViewModel = hiltViewModel()
) {
    val activitiesState by activityViewModel.activitiesState.collectAsState()
    val expensesState by expenseViewModel.expensesState.collectAsState()
    val yieldsState by yieldViewModel.yieldsState.collectAsState()
    val totalExpensesState by expenseViewModel.totalExpensesState.collectAsState()
    val totalRevenueState by yieldViewModel.totalRevenueState.collectAsState()
    
    LaunchedEffect(Unit) {
        activityViewModel.loadActivities()
        expenseViewModel.loadExpenses()
        expenseViewModel.loadTotalExpenses()
        yieldViewModel.loadYieldRecords()
        yieldViewModel.loadTotalRevenue()
    }
    
    val activitiesCount = when (val state = activitiesState) {
        is Resource.Success -> state.data?.totalElements ?: 0
        else -> 0
    }
    
    val expensesCount = when (val state = expensesState) {
        is Resource.Success -> state.data?.totalElements ?: 0
        else -> 0
    }
    
    val yieldsCount = when (val state = yieldsState) {
        is Resource.Success -> state.data?.totalElements ?: 0
        else -> 0
    }
    
    val totalExpenses = when (val state = totalExpensesState) {
        is Resource.Success -> state.data ?: 0.0
        else -> 0.0
    }
    
    val totalRevenue = when (val state = totalRevenueState) {
        is Resource.Success -> state.data ?: 0.0
        else -> 0.0
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Keeping") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Track and manage all your farming records",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Record Categories
        item {
            RecordCategoriesSection(
                onNavigateToActivities = onNavigateToActivities,
                onNavigateToExpenses = onNavigateToExpenses,
                onNavigateToYields = onNavigateToYields,
                activitiesCount = activitiesCount.toInt(),
                expensesCount = expensesCount.toInt(),
                yieldsCount = yieldsCount.toInt()
            )
        }
        
        // Statistics Section
        item {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Activities",
                    value = "$activitiesCount",
                    icon = Icons.Default.Info,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Expenses",
                    value = "KES ${String.format("%.0f", totalExpenses)}",
                    icon = Icons.Default.Check,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Records",
                    value = "$yieldsCount",
                    icon = Icons.Default.Info,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Revenue",
                    value = "KES ${String.format("%.0f", totalRevenue)}",
                    icon = Icons.Default.Info,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

}


