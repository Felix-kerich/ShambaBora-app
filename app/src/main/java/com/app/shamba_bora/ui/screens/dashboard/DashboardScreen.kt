package com.app.shamba_bora.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.app.shamba_bora.ui.components.DashboardCard
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onNavigateToActivities: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToYields: () -> Unit,
    onNavigateToWeather: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }
    
    when (val state = dashboardState) {
        is Resource.Loading -> {
            LoadingIndicator()
        }
        is Resource.Error -> {
            ErrorView(
                message = state.message ?: "Failed to load dashboard",
                onRetry = { viewModel.refreshDashboard() }
            )
        }
        is Resource.Success -> {
            val dashboard = state.data
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Welcome ${dashboard?.farmerName ?: "Back"}!",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = dashboard?.farmName ?: "Manage your maize farming efficiently",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                
                // Quick Stats
                item {
                    Text(
                        text = "Quick Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(getQuickStats(dashboard)) { stat ->
                            DashboardCard(
                                title = stat.title,
                                value = stat.value,
                                icon = stat.icon,
                                color = stat.color,
                                onClick = stat.onClick
                            )
                        }
                    }
                }
                
                // Quick Actions
                item {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(getQuickActions(onNavigateToActivities, onNavigateToExpenses, onNavigateToYields, onNavigateToWeather)) { action ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = action.onClick
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.title,
                                modifier = Modifier.size(40.dp),
                                tint = action.color
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = action.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = action.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Navigate",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Recent Activities Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Activities",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToActivities) {
                            Text("View All")
                        }
                    }
                }
                
                val recentActivities = dashboard?.recentActivities ?: emptyList()
                if (recentActivities.isEmpty()) {
                    item {
                        Text(
                            text = "No recent activities",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(recentActivities.take(5)) { activity ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Agriculture,
                                        contentDescription = null,
                                        modifier = Modifier.padding(12.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activity.activityType,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = activity.activityDate,
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
    }
}

@Composable
fun getQuickStats(dashboard: com.app.shamba_bora.data.model.Dashboard?): List<QuickStat> {
    return listOf(
        QuickStat(
            "Activities",
            "${dashboard?.totalActivities ?: 0}",
            Icons.Default.Agriculture,
            MaterialTheme.colorScheme.primary
        ) {},
        QuickStat(
            "Expenses",
            "KES ${String.format("%.2f", dashboard?.totalExpensesAmount ?: 0.0)}",
            Icons.Default.Payments,
            MaterialTheme.colorScheme.error
        ) {},
        QuickStat(
            "Yields",
            "${dashboard?.totalYieldRecords ?: 0} records",
            Icons.Default.Inventory,
            MaterialTheme.colorScheme.tertiary
        ) {},
        QuickStat(
            "Revenue",
            "KES ${String.format("%.2f", dashboard?.totalRevenue ?: 0.0)}",
            Icons.Default.TrendingUp,
            MaterialTheme.colorScheme.secondary
        ) {}
    )
}

@Composable
fun getQuickActions(
    onNavigateToActivities: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToYields: () -> Unit,
    onNavigateToWeather: () -> Unit
): List<QuickAction> {
    return listOf(
        QuickAction(
            "Record Activity",
            "Log a new farm activity",
            Icons.Default.AddCircle,
            MaterialTheme.colorScheme.primary,
            onNavigateToActivities
        ),
        QuickAction(
            "Add Expense",
            "Record a new expense",
            Icons.Default.Payments,
            MaterialTheme.colorScheme.error,
            onNavigateToExpenses
        ),
        QuickAction(
            "Record Yield",
            "Log harvest information",
            Icons.Default.Inventory,
            MaterialTheme.colorScheme.tertiary,
            onNavigateToYields
        ),
        QuickAction(
            "Weather Forecast",
            "Check weather conditions",
            Icons.Default.WbSunny,
            MaterialTheme.colorScheme.secondary,
            onNavigateToWeather
        )
    )
}

data class QuickStat(
    val title: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit
)

data class QuickAction(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit
)

