package com.app.shamba_bora.ui.screens.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.shamba_bora.data.model.Product
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: (Product, Int) -> Unit,
    onNavigateToMessageUser: (Long, String) -> Unit = { _, _ -> },
    viewModel: MarketplaceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val productState by viewModel.productState.collectAsState()
    var showOrderDialog by remember { mutableStateOf(false) }
    var orderQuantity by remember { mutableStateOf("1") }
    
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Message button to message the seller
                    if (productState is Resource.Success && productState.data?.sellerId != null) {
                        IconButton(
                            onClick = {
                                val product = (productState as Resource.Success).data
                                product?.let {
                                    onNavigateToMessageUser(
                                        it.sellerId ?: 0L,
                                        "Seller #${it.sellerId}"
                                    )
                                }
                            }
                        ) {
                            Icon(Icons.Default.MailOutline, contentDescription = "Message Seller")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = productState) {
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
                        message = state.message ?: "Failed to load product",
                        onRetry = { viewModel.loadProduct(productId) }
                    )
                }
            }
            is Resource.Success -> {
                val product = state.data
                if (product != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Product Image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            if (!product.imageUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(product.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = product.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(android.R.drawable.ic_menu_gallery)
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            // Availability Badge
                            if (product.available) {
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(16.dp),
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = "Available",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                        
                        // Product Info
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = product.name ?: "Unknown Product",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "KES ${String.format("%.2f", product.price ?: 0.0)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = product.category ?: "General",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Divider()
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Details Section
                            Text(
                                text = "Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            DetailRow(
                                icon = Icons.Default.Info,
                                label = "Quantity Available",
                                value = "${product.quantity ?: 0} ${product.unit ?: "units"}"
                            )
                            
                            if (!product.location.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailRow(
                                    icon = Icons.Default.LocationOn,
                                    label = "Location",
                                    value = product.location
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Divider()
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Description Section
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = product.description ?: "No description available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Order Button
                            if (product.available) {
                                Button(
                                    onClick = { showOrderDialog = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Place Order")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false
                                ) {
                                    Text("Out of Stock")
                                }
                            }
                        }
                    }
                    
                    // Order Dialog
                    if (showOrderDialog) {
                        AlertDialog(
                            onDismissRequest = { showOrderDialog = false },
                            title = { Text("Select Quantity") },
                            text = {
                                Column {
                                    OutlinedTextField(
                                        value = orderQuantity,
                                        onValueChange = { orderQuantity = it },
                                        label = { Text("Quantity") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val qty = orderQuantity.toIntOrNull() ?: 1
                                    val total = (product.price ?: 0.0) * qty
                                    Text(
                                        text = "Total: KES ${String.format("%.2f", total)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val qty = orderQuantity.toIntOrNull() ?: 1
                                        if (qty > 0) {
                                            showOrderDialog = false
                                            onNavigateToCheckout(product, qty)
                                        }
                                    }
                                ) {
                                    Text("Proceed to Checkout")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showOrderDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
