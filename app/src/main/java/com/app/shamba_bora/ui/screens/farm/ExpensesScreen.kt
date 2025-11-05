package com.app.shamba_bora.ui.screens.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.FarmExpense
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    onNavigateBack: () -> Unit,
    viewModel: FarmExpenseViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val expensesState by viewModel.expensesState.collectAsState()
    val totalExpensesState by viewModel.totalExpensesState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
        viewModel.loadTotalExpenses()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Expenses") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        when (val state = expensesState) {
            is Resource.Loading -> {
                LoadingIndicator()
            }
            is Resource.Error -> {
                ErrorView(
                    message = state.message ?: "Failed to load expenses",
                    onRetry = { viewModel.loadExpenses() }
                )
            }
            is Resource.Success -> {
                val expenses = state.data?.content ?: emptyList()
                val totalExpenses = when (val totalState = totalExpensesState) {
                    is Resource.Success -> totalState.data ?: 0.0
                    else -> 0.0
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    
                    // Total Expenses Card
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
                                    text = "Total Expenses",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "KES ${String.format("%.2f", totalExpenses)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    
                    if (expenses.isEmpty()) {
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
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No expenses yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Tap + to record your first expense",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(expenses) { expense ->
                            ExpenseCard(expense = expense)
                        }
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onSave = { expense ->
                viewModel.createExpense(expense)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ExpenseCard(expense: FarmExpense) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(
                    imageVector = getCategoryIcon(expense.category),
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = expense.expenseDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "KES ${String.format("%.2f", expense.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (FarmExpense) -> Unit
) {
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expenseDate by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var cropType by remember { mutableStateOf("Maize") }
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    
    val categories = listOf("Seeds", "Fertilizer", "Pesticides", "Labor", "Equipment", "Transport", "Irrigation", "Other")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = cropType,
                    onValueChange = { cropType = it },
                    label = { Text("Crop Type *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                
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
                        modifier = Modifier.fillMaxWidth().menuAnchor()
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
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (KES) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = expenseDate,
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
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (category.isNotBlank() && description.isNotBlank() && amount.isNotBlank() && cropType.isNotBlank()) {
                    onSave(
                        FarmExpense(
                            cropType = cropType,
                            category = category,
                            description = description,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            expenseDate = expenseDate
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
                            expenseDate = date.toString()
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
fun getCategoryIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category.lowercase()) {
        "seeds" -> Icons.Default.Info
        "fertilizer", "fertilizers" -> Icons.Default.Info
        "pesticides", "pesticide" -> Icons.Default.Info
        "labor", "labour" -> Icons.Default.Info
        "equipment" -> Icons.Default.Build
        "fuel" -> Icons.Default.Info
        "water", "irrigation" -> Icons.Default.Info
        "transport", "transportation" -> Icons.Default.Info
        "maintenance" -> Icons.Default.Settings
        else -> Icons.Default.Info
    }
}

