package com.app.shamba_bora.ui.screens.marketplace

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
import com.app.shamba_bora.data.model.Order
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrdersScreen(
    sellerId: Long,
    onNavigateBack: () -> Unit,
    viewModel: MarketplaceViewModel = hiltViewModel()
) {
    val ordersState by viewModel.sellerOrdersState.collectAsState()
    
    LaunchedEffect(sellerId) {
        viewModel.loadOrdersBySeller(sellerId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders to Fulfill") },
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
        }
    ) { paddingValues ->
        when (val state = ordersState) {
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorView(
                        message = state.message ?: "Failed to load orders",
                        onRetry = { viewModel.loadOrdersBySeller(sellerId) }
                    )
                }
            }
            is Resource.Success -> {
                val orders = state.data?.content ?: emptyList()
                if (orders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No orders yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Orders for your products will appear here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            SellerOrderCard(
                                order = order,
                                onUpdateStatus = { orderId, newStatus ->
                                    viewModel.updateOrderStatus(orderId, newStatus)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SellerOrderCard(
    order: Order,
    onUpdateStatus: (Long, String) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    
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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #${order.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Buyer ID: ${order.buyerId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OrderStatusChip(status = order.status)
                    
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${order.quantity} items",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "KES ${String.format("%.2f", order.totalPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                if (order.deliveryAddress != null) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Delivery Address",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = order.deliveryAddress,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                if (order.createdAt != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ordered on ${order.createdAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Update Status Button
                Box {
                    OutlinedButton(
                        onClick = { showStatusMenu = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Update,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Update Status")
                    }
                    
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        listOf("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    order.id?.let { orderId ->
                                        onUpdateStatus(orderId, status)
                                    }
                                    showStatusMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getStatusIcon(status),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getStatusIcon(status: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status.uppercase()) {
        "PENDING" -> Icons.Default.Schedule
        "CONFIRMED" -> Icons.Default.CheckCircle
        "SHIPPED" -> Icons.Default.LocalShipping
        "DELIVERED" -> Icons.Default.Done
        "CANCELLED" -> Icons.Default.Cancel
        else -> Icons.Default.Info
    }
}
