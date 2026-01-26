package com.app.shamba_bora.ui.screens.marketplace

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.shamba_bora.data.model.Product
import com.app.shamba_bora.utils.CloudinaryUploader
import com.app.shamba_bora.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    productId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: MarketplaceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Seeds") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var imageUrl by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageInputMethod by remember { mutableStateOf("url") } // "url" or "file"
    var location by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedUnit by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }
    
    val categories = listOf("Seeds", "Fertilizer", "Equipment", "Crops", "Livestock", "Tools", "Other")
    val units = listOf("kg", "g", "liters", "pieces", "bags", "tons")
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            // Upload to Cloudinary
            isUploadingImage = true
            scope.launch {
                val result = CloudinaryUploader.uploadImage(
                    context = context,
                    imageUri = it,
                    folder = "shamba_bora/marketplace"
                )
                isUploadingImage = false
                result.onSuccess { url ->
                    imageUrl = url
                }.onFailure { error ->
                    errorMessage = "Failed to upload image: ${error.message}"
                    showError = true
                    imageUri = null
                }
            }
        }
    }
    
    // Load product if editing
    LaunchedEffect(productId) {
        productId?.let {
            viewModel.loadProduct(it)
        }
    }
    
    val productState by viewModel.productState.collectAsState()
    
    LaunchedEffect(productState) {
        if (productState is com.app.shamba_bora.utils.Resource.Success) {
            val product = (productState as com.app.shamba_bora.utils.Resource.Success).data
            product?.let {
                name = it.name ?: ""
                description = it.description ?: ""
                category = it.category ?: "Seeds"
                price = it.price?.toString() ?: ""
                quantity = it.quantity?.toString() ?: ""
                unit = it.unit ?: "kg"
                imageUrl = it.imageUrl ?: ""
                location = it.location ?: ""
            }
        }
    }
    
    fun validateAndSave() {
        when {
            name.isBlank() -> {
                errorMessage = "Product name is required"
                showError = true
            }
            description.isBlank() -> {
                errorMessage = "Description is required"
                showError = true
            }
            price.toDoubleOrNull() == null || price.toDouble() <= 0 -> {
                errorMessage = "Valid price is required"
                showError = true
            }
            quantity.toIntOrNull() == null || quantity.toInt() <= 0 -> {
                errorMessage = "Valid quantity is required"
                showError = true
            }
            else -> {
                isLoading = true
                val product = Product(
                    id = productId,
                    name = name,
                    description = description,
                    category = category,
                    price = price.toDouble(),
                    quantity = quantity.toInt(),
                    unit = unit,
                    imageUrl = imageUrl.ifBlank { null },
                    location = location.ifBlank { null },
                    sellerId = viewModel.getCurrentUserId()
                )
                
                if (productId != null) {
                    viewModel.updateProduct(productId, product)
                } else {
                    viewModel.createProduct(product)
                }
                onNavigateBack()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId != null) "Edit Product" else "Add Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (showError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                expandedCategory = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (KES) *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Unit Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedUnit,
                onExpandedChange = { expandedUnit = it }
            ) {
                OutlinedTextField(
                    value = unit,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Image Input Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Product Image",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Tab Buttons for image input method
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = imageInputMethod == "url",
                            onClick = { 
                                imageInputMethod = "url"
                                imageUri = null
                            },
                            label = { Text("Image URL") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        
                        FilterChip(
                            selected = imageInputMethod == "file",
                            onClick = { 
                                imageInputMethod = "file"
                                imageUrl = ""
                            },
                            label = { Text("Pick from Files") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Show appropriate input based on selected method
                    if (imageInputMethod == "url") {
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("Enter Image URL") },
                            placeholder = { Text("https://example.com/image.jpg") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Link, contentDescription = null)
                            }
                        )
                    } else {
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            enabled = !isUploadingImage
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (imageUri != null) "Change Image" else "Pick Image from Files")
                        }
                        
                        if (isUploadingImage) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Uploading image to cloud storage...",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else if (imageUri != null && imageUrl.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Image uploaded successfully",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    // Image Preview
                    if ((imageUrl.isNotBlank() && imageUrl != "null") || imageUri != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUri ?: imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Product image preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        // Clear image button
                        TextButton(
                            onClick = {
                                imageUrl = ""
                                imageUri = null
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove Image")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { validateAndSave() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && !isUploadingImage
            ) {
                if (isLoading || isUploadingImage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isUploadingImage) "Uploading Image..." else "Saving...")
                } else {
                    Text(if (productId != null) "Update Product" else "Add Product")
                }
            }
        }
    }
}
