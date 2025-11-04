package com.app.shamba_bora.ui.screens.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RecordsScreen(
    onNavigateToActivities: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToYields: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Record Keeping",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Manage your farm activities, expenses, and yields",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Record Categories
        items(getRecordCategories(onNavigateToActivities, onNavigateToExpenses, onNavigateToYields)) { category ->
            RecordCategoryCard(category = category)
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
                    value = "24",
                    icon = Icons.Default.Agriculture,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Expenses",
                    value = "KES 45,000",
                    icon = Icons.Default.Payments,
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
                    title = "Total Yields",
                    value = "1,200 kg",
                    icon = Icons.Default.Inventory,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Revenue",
                    value = "KES 120,000",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

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
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp)
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
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class RecordCategory(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    val count: Int,
    val onClick: () -> Unit
)

@Composable
fun getRecordCategories(
    onNavigateToActivities: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToYields: () -> Unit
): List<RecordCategory> {
    return listOf(
        RecordCategory(
            "Farm Activities",
            "Track planting, harvesting, and other farm operations",
            Icons.Default.Agriculture,
            MaterialTheme.colorScheme.primary,
            12,
            onNavigateToActivities
        ),
        RecordCategory(
            "Expenses",
            "Record and manage your farming expenses",
            Icons.Default.Payments,
            MaterialTheme.colorScheme.error,
            8,
            onNavigateToExpenses
        ),
        RecordCategory(
            "Yields",
            "Log your harvest and yield information",
            Icons.Default.Inventory,
            MaterialTheme.colorScheme.tertiary,
            4,
            onNavigateToYields
        )
    )
}

