package com.app.shamba_bora.ui.screens.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.ActivityReminder
import com.app.shamba_bora.data.model.ActivityReminderRequest
import com.app.shamba_bora.data.model.FarmActivity
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmActivityViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
fun ActivityCard(
    activity: FarmActivity,
    onViewDetails: (Long) -> Unit = {},
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            text = activity.activityType,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activity.cropType,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
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
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activity.activityDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (activity.description != null) {
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
                                imageVector = Icons.Default.Landscape,
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
                                imageVector = Icons.Default.Payments,
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
            activityName = activity.activityType,
            onDismiss = { showReminderDialog = false },
            onSave = { request ->
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
    var activityType by remember { mutableStateOf("") }
    var cropType by remember { mutableStateOf("Maize") }
    var description by remember { mutableStateOf("") }
    var activityDate by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedActivityType by remember { mutableStateOf(false) }
    
    val activityTypes = listOf("Planting", "Plowing", "Watering", "Fertilizing", "Weeding", "Spraying", "Harvesting", "Pruning", "Other")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Farm Activity") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                ExposedDropdownMenuBox(
                    expanded = expandedActivityType,
                    onExpandedChange = { expandedActivityType = it }
                ) {
                    OutlinedTextField(
                        value = activityType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Activity Type *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivityType) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedActivityType,
                        onDismissRequest = { expandedActivityType = false }
                    ) {
                        activityTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    activityType = type
                                    expandedActivityType = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = cropType,
                    onValueChange = { cropType = it },
                    label = { Text("Crop Type *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = activityDate,
                    onValueChange = { },
                    label = { Text("Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (activityType.isNotBlank() && cropType.isNotBlank()) {
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
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = java.time.Instant.ofEpochMilli(millis)
                            val date = java.time.LocalDate.ofInstant(instant, java.time.ZoneId.systemDefault())
                            activityDate = date.toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ReminderItem(reminder: ActivityReminder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.message,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = formatDateTime(reminder.reminderDateTime),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
                if (reminder.repeatInterval != null) {
                    Text(
                        text = "Repeats: ${reminder.repeatInterval}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    activityId: Long,
    activityName: String,
    onDismiss: () -> Unit,
    onSave: (ActivityReminderRequest) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var reminderDate by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var reminderTime by remember { mutableStateOf("09:00") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var repeatInterval by remember { mutableStateOf("NONE") }
    var expandedRepeat by remember { mutableStateOf(false) }
    
    val repeatOptions = listOf("NONE", "DAILY", "WEEKLY", "MONTHLY")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Reminder for $activityName") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Reminder Message *") },
                    placeholder = { Text("e.g., Time to water the crops") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = reminderDate,
                    onValueChange = { },
                    label = { Text("Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = { reminderTime = it },
                    label = { Text("Time (HH:mm) *") },
                    placeholder = { Text("09:00") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expandedRepeat,
                    onExpandedChange = { expandedRepeat = it }
                ) {
                    OutlinedTextField(
                        value = repeatInterval,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Repeat") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRepeat) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRepeat,
                        onDismissRequest = { expandedRepeat = false }
                    ) {
                        repeatOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    repeatInterval = option
                                    expandedRepeat = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        val dateTime = "${reminderDate}T${reminderTime}:00"
                        onSave(
                            ActivityReminderRequest(
                                reminderDateTime = dateTime,
                                message = message,
                                repeatInterval = if (repeatInterval == "NONE") null else repeatInterval
                            )
                        )
                    }
                },
                enabled = message.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = java.time.Instant.ofEpochMilli(millis)
                            val date = java.time.LocalDate.ofInstant(instant, java.time.ZoneId.systemDefault())
                            reminderDate = date.toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

fun formatDateTime(dateTime: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val parsed = LocalDateTime.parse(dateTime, formatter)
        val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
        parsed.format(displayFormatter)
    } catch (e: Exception) {
        dateTime
    }
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

