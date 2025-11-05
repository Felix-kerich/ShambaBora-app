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
fun ActivityDetailScreen(
    activityId: Long,
    onNavigateBack: () -> Unit,
    viewModel: FarmActivityViewModel = hiltViewModel()
) {
    val activityState by viewModel.activityState.collectAsState()
    val remindersState by viewModel.remindersState.collectAsState()
    var showReminderDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(activityId) {
        viewModel.loadActivity(activityId)
        viewModel.loadActivityReminders(activityId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showReminderDialog = true }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Add Reminder")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (val state = activityState) {
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
                        message = state.message ?: "Failed to load activity",
                        onRetry = { viewModel.loadActivity(activityId) }
                    )
                }
            }
            is Resource.Success -> {
                val activity = state.data
                if (activity != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Activity Header Card
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = getActivityIcon(activity.activityType),
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = activity.activityType ?: "Unknown Activity",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = activity.cropType ?: "Unknown Crop",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = activity.activityDate ?: "No date",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Description Section
                        if (!activity.description.isNullOrBlank()) {
                            item {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Description",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = activity.description,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Activity Details Card
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Activity Details",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    if (activity.areaSize != null && activity.areaSize > 0) {
                                        DetailItem(
                                            icon = Icons.Default.Place,
                                            label = "Area Size",
                                            value = "${activity.areaSize} ${activity.units ?: "acres"}"
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    
                                    if (activity.cost != null && activity.cost > 0) {
                                        DetailItem(
                                            icon = Icons.Default.Info,
                                            label = "Cost",
                                            value = "KES ${String.format("%.2f", activity.cost)}"
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    
                                    if (!activity.productUsed.isNullOrBlank()) {
                                        DetailItem(
                                            icon = Icons.Default.Build,
                                            label = "Product Used",
                                            value = activity.productUsed
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    
                                    if (!activity.location.isNullOrBlank()) {
                                        DetailItem(
                                            icon = Icons.Default.LocationOn,
                                            label = "Location",
                                            value = activity.location
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    
                                    if (activity.laborHours != null && activity.laborHours > 0) {
                                        DetailItem(
                                            icon = Icons.Default.Person,
                                            label = "Labor Hours",
                                            value = "${activity.laborHours} hours"
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Conditions Card
                        if (!activity.weatherConditions.isNullOrBlank() || !activity.soilConditions.isNullOrBlank()) {
                            item {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Conditions",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        if (!activity.weatherConditions.isNullOrBlank()) {
                                            DetailItem(
                                                icon = Icons.Default.Info,
                                                label = "Weather",
                                                value = activity.weatherConditions
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                        
                                        if (!activity.soilConditions.isNullOrBlank()) {
                                            DetailItem(
                                                icon = Icons.Default.Info,
                                                label = "Soil",
                                                value = activity.soilConditions
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Reminders Section
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
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
                                        Text(
                                            text = "Reminders",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        IconButton(onClick = { showReminderDialog = true }) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add Reminder"
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    when (val reminderState = remindersState) {
                                        is Resource.Loading -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        }
                                        is Resource.Error -> {
                                            Text(
                                                text = "Failed to load reminders",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        is Resource.Success -> {
                                            val reminders = reminderState.data ?: emptyList()
                                            if (reminders.isEmpty()) {
                                                Text(
                                                    text = "No reminders set for this activity",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            } else {
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    reminders.forEach { reminder ->
                                                        ReminderItem(reminder)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Notes Section
                        if (!activity.notes.isNullOrBlank()) {
                            item {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Notes",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = activity.notes,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Add Reminder Dialog
                    if (showReminderDialog) {
                        AddReminderDialog(
                            activityId = activityId,
                            activityName = activity.activityType ?: "Activity",
                            onDismiss = { showReminderDialog = false },
                            onSave = { request: ActivityReminderRequest ->
                                viewModel.addReminder(activityId, request)
                                showReminderDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(
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
                style = MaterialTheme.typography.labelMedium,
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
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
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
