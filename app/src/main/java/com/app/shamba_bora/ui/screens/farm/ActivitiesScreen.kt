package com.app.shamba_bora.ui.screens.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.ui.components.records.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmActivityViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToActivityDetail: (Long) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    viewModel: FarmActivityViewModel = hiltViewModel()
) {
    val activitiesState by viewModel.activitiesState.collectAsState()
    val patchesState by viewModel.patchesState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedPatchId by remember { mutableStateOf<Long?>(null) }
    var showPatchFilter by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadActivities()
        viewModel.loadPatches()
    }
    
    val patches = when (patchesState) {
        is Resource.Success -> patchesState.data ?: emptyList()
        else -> emptyList()
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
                onClick = { onNavigateToCreate() },
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
                val allActivities = state.data?.content ?: emptyList()
                
                // Apply filters
                val filteredActivities = allActivities.filter { activity ->
                    val matchesSearch = searchQuery.isEmpty() || 
                        activity.activityType?.contains(searchQuery, ignoreCase = true) == true ||
                        activity.cropType?.contains(searchQuery, ignoreCase = true) == true ||
                        activity.description?.contains(searchQuery, ignoreCase = true) == true
                    
                    val matchesPatch = selectedPatchId == null
                    
                    matchesSearch && matchesPatch
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search and Filter Bar
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Search field
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                placeholder = { Text("Search activities...") },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.large,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            // Patch Filter Button
                            Button(
                                onClick = { showPatchFilter = !showPatchFilter },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors()
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filter", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    selectedPatchId?.let { "Patch: $it" } ?: "Filter by Patch",
                                    modifier = Modifier.weight(1f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                )
                                if (selectedPatchId != null) {
                                    IconButton(
                                        onClick = { selectedPatchId = null },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear filter", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            
                            // Patch Filter Dropdown
                            if (showPatchFilter) {
                                PatchFilterDropdown(
                                    patches = patches,
                                    selectedPatchId = selectedPatchId,
                                    onPatchSelected = { patchId ->
                                        selectedPatchId = patchId
                                        showPatchFilter = false
                                    },
                                    onDismiss = { showPatchFilter = false }
                                )
                            }
                        }
                    }
                    
                    // Activities info card
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
                                        text = "${filteredActivities.size} Activities",
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
                    
                    if (filteredActivities.isEmpty()) {
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
                                        text = "No activities found",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (searchQuery.isNotEmpty() || selectedPatchId != null)
                                            "Try adjusting your search or filters"
                                        else
                                            "Tap + to add your first activity",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredActivities) { activity ->
                            ActivityCard(
                                activity = activity,
                                onViewDetails = onNavigateToActivityDetail
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    activity: FarmActivity,
    onViewDetails: (Long) -> Unit,
    viewModel: FarmActivityViewModel = hiltViewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    val remindersState by viewModel.remindersState.collectAsState()
    
    LaunchedEffect(expanded) {
        if (expanded && activity.id != null) {
            viewModel.loadActivityReminders(activity.id)
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { activity.id?.let { onViewDetails(it) } }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = getActivityIcon(activity.activityType),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = activity.activityType ?: "Unknown Activity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activity.cropType ?: "Unknown Crop",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.Info else Icons.Default.Info,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activity.activityDate ?: "No date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (!activity.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Expanded section with reminders
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (activity.areaSize != null && activity.areaSize > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
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
                                imageVector = Icons.Default.Info,
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
    
    if (showReminderDialog && activity.id != null) {
        AddReminderDialog(
            activityId = activity.id,
            activityName = activity.activityType ?: "Activity",
            onDismiss = { showReminderDialog = false },
            onSave = { request: ActivityReminderRequest ->
                viewModel.addReminder(activity.id, request)
                showReminderDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onSave: (FarmActivity) -> Unit
) {
    var activityType by remember { mutableStateOf(ActivityType.PLANTING) }
    var cropType by remember { mutableStateOf("Maize") }
    var description by remember { mutableStateOf("") }
    var activityDate by remember { mutableStateOf(java.time.LocalDate.now()) }
    var areaSize by remember { mutableStateOf("") }
    var units by remember { mutableStateOf(AreaUnit.HA) }
    var weatherConditions by remember { mutableStateOf(WeatherCondition.SUNNY) }
    var soilConditions by remember { mutableStateOf(SoilCondition.WELL_DRAINED) }
    var cost by remember { mutableStateOf("") }
    var laborHours by remember { mutableStateOf("") }
    var laborCost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Farm Activity") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormFieldLabel(text = "Activity Type", isRequired = true)
                Spacer(modifier = Modifier.height(4.dp))
                ActivityTypeDropdown(
                    selectedType = activityType,
                    onTypeChange = { activityType = it }
                )

                FormTextField(
                    label = "Crop Type",
                    value = cropType,
                    onValueChange = { cropType = it },
                    isRequired = true
                )

                FormDateField(
                    label = "Activity Date",
                    selectedDate = activityDate,
                    onDateChange = { activityDate = it }
                )

                FormTextField(
                    label = "Description",
                    value = description,
                    onValueChange = { description = it },
                    isRequired = true,
                    minLines = 2,
                    maxLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormTextField(
                        label = "Area Size",
                        value = areaSize,
                        onValueChange = { areaSize = it },
                        placeholder = "e.g., 1.5",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Decimal
                    )
                    AreaUnitDropdown(
                        selectedUnit = units,
                        onUnitChange = { units = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                WeatherConditionDropdown(
                    selectedCondition = weatherConditions,
                    onConditionChange = { weatherConditions = it }
                )

                SoilConditionDropdown(
                    selectedCondition = soilConditions,
                    onConditionChange = { soilConditions = it }
                )

                FormTextField(
                    label = "Activity Cost",
                    value = cost,
                    onValueChange = { cost = it },
                    placeholder = "0.00",
                    keyboardType = KeyboardType.Decimal
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormTextField(
                        label = "Labor Hours",
                        value = laborHours,
                        onValueChange = { laborHours = it },
                        placeholder = "Hours",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                    FormTextField(
                        label = "Labor Cost",
                        value = laborCost,
                        onValueChange = { laborCost = it },
                        placeholder = "0.00",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Decimal
                    )
                }

                FormTextField(
                    label = "Notes",
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "Additional observations...",
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        onSave(
                            FarmActivity(
                                activityType = activityType.displayName,
                                cropType = cropType,
                                activityDate = activityDate.toString(),
                                description = description,
                                areaSize = areaSize.toDoubleOrNull(),
                                units = units.name,
                                cost = cost.toDoubleOrNull(),
                                laborHours = laborHours.toIntOrNull(),
                                laborCost = laborCost.toDoubleOrNull(),
                                weatherConditions = weatherConditions.name,
                                soilConditions = soilConditions.name,
                                notes = notes.ifEmpty { null }
                            )
                        )
                    }
                },
                enabled = description.isNotBlank()
            ) {
                Text("Save Activity")
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
fun getActivityIcon(activityType: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (activityType?.lowercase()) {
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


