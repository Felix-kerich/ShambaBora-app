package com.app.shamba_bora.ui.screens.farm

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
import com.app.shamba_bora.data.model.FarmActivity
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    onNavigateBack: () -> Unit,
    viewModel: FarmActivityViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val activitiesState by viewModel.activitiesState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadActivities()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Activities") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Activity")
            }
        }
    ) { paddingValues ->
        when (val state = activitiesState) {
            is Resource.Loading -> {
                LoadingIndicator()
            }
            is Resource.Error -> {
                ErrorView(
                    message = state.message ?: "Failed to load activities",
                    onRetry = { viewModel.loadActivities() }
                )
            }
            is Resource.Success -> {
                val activities = state.data?.content ?: emptyList()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
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
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "${activities.size} Activities",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "Track all your farming operations",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                    
                    if (activities.isEmpty()) {
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
                                        text = "No activities yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Tap + to add your first activity",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(activities) { activity ->
                            ActivityCard(activity = activity)
                        }
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddActivityDialog(
            onDismiss = { showAddDialog = false },
            onSave = { activity ->
                viewModel.createActivity(activity)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ActivityCard(activity: FarmActivity) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = getActivityIcon(activity.activityType),
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = activity.activityType,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = activity.cropType,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = activity.activityDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (!activity.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = activity.description ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (activity.areaSize != null && activity.areaSize > 0 || activity.cost != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (activity.areaSize != null && activity.areaSize > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${activity.areaSize} ${activity.units ?: "acres"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (activity.cost != null && activity.cost > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "KES ${String.format("%.2f", activity.cost)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onSave: (FarmActivity) -> Unit
) {
    var activityType by remember { mutableStateOf("") }
    var cropType by remember { mutableStateOf("Maize") }
    var description by remember { mutableStateOf("") }
    var activityDate by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Farm Activity") },
        text = {
            Column {
                OutlinedTextField(
                    value = activityType,
                    onValueChange = { activityType = it },
                    label = { Text("Activity Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cropType,
                    onValueChange = { cropType = it },
                    label = { Text("Crop Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = activityDate,
                    onValueChange = { activityDate = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (activityType.isNotBlank() && cropType.isNotBlank() && activityDate.isNotBlank()) {
                    onSave(
                        FarmActivity(
                            activityType = activityType,
                            cropType = cropType,
                            activityDate = activityDate,
                            description = description.ifEmpty { null }
                        )
                    )
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun getActivityIcon(activityType: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (activityType.lowercase()) {
        "planting", "plowing", "seeding" -> Icons.Default.Info
        "watering", "irrigation" -> Icons.Default.Info
        "fertilizing", "fertilizer" -> Icons.Default.Info
        "harvesting", "harvest" -> Icons.Default.Info
        "weeding" -> Icons.Default.Info
        "spraying", "pesticide" -> Icons.Default.Info
        "pruning" -> Icons.Default.Info
        else -> Icons.Default.Info
    }
}

