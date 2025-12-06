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
import com.app.shamba_bora.data.model.FarmAdviceResponse
import com.app.shamba_bora.ui.screens.chatbot.EnhancedFarmAdviceDialog
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.ChatbotViewModel
import com.app.shamba_bora.viewmodel.FarmActivityViewModel
import com.app.shamba_bora.viewmodel.FarmExpenseViewModel
import com.app.shamba_bora.viewmodel.PatchViewModel
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
    onNavigateToPatches: () -> Unit,
    activityViewModel: FarmActivityViewModel = hiltViewModel(),
    expenseViewModel: FarmExpenseViewModel = hiltViewModel(),
    yieldViewModel: YieldRecordViewModel = hiltViewModel(),
    patchViewModel: PatchViewModel = hiltViewModel(),
    chatbotViewModel: ChatbotViewModel = hiltViewModel()
) {
    var showAdviceDialog by remember { mutableStateOf(false) }
    var showAdviceErrorDialog by remember { mutableStateOf(false) }
    var showAdviceLoadingDialog by remember { mutableStateOf(false) }
    var farmAdviceReady by remember { mutableStateOf<FarmAdviceResponse?>(null) }
    var showAdviceReadyDialog by remember { mutableStateOf(false) }
    
    val activitiesState by activityViewModel.activitiesState.collectAsState()
    val expensesState by expenseViewModel.expensesState.collectAsState()
    val yieldsState by yieldViewModel.yieldsState.collectAsState()
    val totalExpensesState by expenseViewModel.totalExpensesState.collectAsState()
    val totalRevenueState by yieldViewModel.totalRevenueState.collectAsState()
    val patchesState by patchViewModel.patchesState.collectAsState()
    val farmAdviceState by chatbotViewModel.farmAdvice.collectAsState()
    
    LaunchedEffect(Unit) {
        activityViewModel.loadActivities()
        expenseViewModel.loadExpenses()
        expenseViewModel.loadTotalExpenses()
        yieldViewModel.loadYieldRecords()
        yieldViewModel.loadTotalRevenue()
        patchViewModel.loadPatches()
    }
    
    // Handle farm advice state changes - show dialog immediately when success
    LaunchedEffect(farmAdviceState) {
        when (farmAdviceState) {
            is Resource.Loading -> {
                showAdviceLoadingDialog = true
                showAdviceErrorDialog = false
                showAdviceReadyDialog = false
            }
            is Resource.Success -> {
                showAdviceLoadingDialog = false
                showAdviceErrorDialog = false
                farmAdviceReady = (farmAdviceState as Resource.Success<FarmAdviceResponse>).data
                showAdviceReadyDialog = true
            }
            is Resource.Error -> {
                showAdviceLoadingDialog = false
                showAdviceErrorDialog = true
                showAdviceReadyDialog = false
            }
            else -> {
                showAdviceLoadingDialog = false
                showAdviceErrorDialog = false
                showAdviceReadyDialog = false
            }
        }
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
    
    val patchesCount = when (val state = patchesState) {
        is Resource.Success -> state.data?.size ?: 0
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
                title = { Text("Record Keeping & Analytics") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { chatbotViewModel.getFarmAdvice() }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Get Farm Advice",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
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
        
        // Patches Management (new section)
        item {
            RecordCategory(
                title = "My Patches",
                description = "Manage your farm plots and planting seasons",
                icon = Icons.Default.Settings,
                color = MaterialTheme.colorScheme.tertiary,
                count = patchesCount,
                onClick = onNavigateToPatches
            ).let { category ->
                RecordCategoryCard(category = category)
            }
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
    
    // Farm Advice Ready Dialog - using the same dialog from chatbot
    if (showAdviceReadyDialog && farmAdviceReady != null) {
        EnhancedFarmAdviceDialog(
            advice = farmAdviceReady!!,
            onDismiss = {
                showAdviceReadyDialog = false
                chatbotViewModel.clearFarmAdvice()
            }
        )
    }
    
    // Farm Advice Loading Dialog - with "Please Wait" message
    farmAdviceState?.let { advice ->
        if (advice is Resource.Loading) {
            AlertDialog(
                onDismissRequest = {
                    // Allow user to close dialog and continue processing in background
                    chatbotViewModel.startFarmAdviceBackgroundPolling()
                    showAdviceLoadingDialog = false
                },
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Getting Farm Advice")
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        advice.message?.let { msg ->
                            Text(
                                msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } ?: run {
                            Text(
                                "Analyzing your farm records...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        // Show what we're doing
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            ProgressStep(text = "Retrieving farm data", isActive = true)
                            ProgressStep(text = "Analyzing conditions", isActive = true)
                            ProgressStep(text = "Generating recommendations", isActive = true)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            chatbotViewModel.startFarmAdviceBackgroundPolling()
                            showAdviceLoadingDialog = false
                        }
                    ) {
                        Text("Processing in background")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { /* Keep processing */ }) {
                        Text("Keep waiting")
                    }
                }
            )
        }
    }
    
    // Farm Advice Error Dialog
    if (showAdviceErrorDialog && farmAdviceState is Resource.Error) {
        val error = (farmAdviceState as Resource.Error)
        AlertDialog(
            onDismissRequest = { 
                showAdviceErrorDialog = false
                chatbotViewModel.clearFarmAdvice()
            },
            title = { Text("Farm Advice") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        error.message ?: "Failed to get farm advice",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    // Show helpful suggestions for common errors
                    when {
                        error.message?.contains("Access denied", ignoreCase = true) == true || 
                        error.message?.contains("Authentication", ignoreCase = true) == true -> {
                            Text(
                                "Tip: Make sure you're logged in with a valid account. You may need to log out and log back in to refresh your session.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        error.message?.contains("Farm analytics data not found", ignoreCase = true) == true -> {
                            Text(
                                "Tip: Complete your farm profile by adding farm details, soil information, and crop data. This helps the AI provide better advice.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        error.message?.contains("Server error", ignoreCase = true) == true -> {
                            Text(
                                "Tip: The server is experiencing issues. Please try again in a few moments.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        error.message?.contains("taking longer", ignoreCase = true) == true -> {
                            Text(
                                "Your farm advice is being processed in the background. You'll be notified when it's ready!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    showAdviceErrorDialog = false
                    chatbotViewModel.clearFarmAdvice()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAdviceErrorDialog = false
                    chatbotViewModel.clearFarmAdvice()
                    chatbotViewModel.getFarmAdvice() // Retry
                }) {
                    Text("Retry")
                }
            }
        )
    }
}

@Composable
fun ProgressStep(text: String, isActive: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isActive) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 1.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}


