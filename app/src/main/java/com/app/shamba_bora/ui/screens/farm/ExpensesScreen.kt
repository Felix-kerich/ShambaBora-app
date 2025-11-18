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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.ui.components.records.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmExpenseViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToExpenseDetail: (Long) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    viewModel: FarmExpenseViewModel = hiltViewModel()
) {
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
                onClick = { onNavigateToCreate() },
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
                            ExpenseCard(
                                expense = expense,
                                onClick = { expense.id?.let { onNavigateToExpenseDetail(it) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(
    expense: FarmExpense,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
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
    var cropType by remember { mutableStateOf("Maize") }
    var category by remember { mutableStateOf(ExpenseCategory.SEEDS) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expenseDate by remember { mutableStateOf(java.time.LocalDate.now()) }
    var supplier by remember { mutableStateOf("") }
    var invoiceNumber by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var growthStage by remember { mutableStateOf(GrowthStage.PRE_PLANTING) }
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormTextField(
                    label = "Crop Type",
                    value = cropType,
                    onValueChange = { cropType = it },
                    isRequired = true
                )

                FormFieldLabel(text = "Category", isRequired = true)
                Spacer(modifier = Modifier.height(4.dp))
                ExpenseCategoryDropdown(
                    selectedCategory = category,
                    onCategoryChange = { category = it }
                )

                FormTextField(
                    label = "Description",
                    value = description,
                    onValueChange = { description = it },
                    isRequired = true,
                    minLines = 2,
                    maxLines = 3
                )

                FormTextField(
                    label = "Amount (KES)",
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = "0.00",
                    isRequired = true,
                    keyboardType = KeyboardType.Decimal
                )

                FormDateField(
                    label = "Expense Date",
                    selectedDate = expenseDate,
                    onDateChange = { expenseDate = it }
                )

                FormTextField(
                    label = "Supplier",
                    value = supplier,
                    onValueChange = { supplier = it },
                    placeholder = "Vendor name"
                )

                FormTextField(
                    label = "Invoice Number",
                    value = invoiceNumber,
                    onValueChange = { invoiceNumber = it },
                    placeholder = "Receipt #"
                )

                FormFieldLabel(text = "Payment Method", isRequired = false)
                Spacer(modifier = Modifier.height(4.dp))
                PaymentMethodDropdown(
                    selectedMethod = paymentMethod,
                    onMethodChange = { paymentMethod = it }
                )

                FormFieldLabel(text = "Growth Stage", isRequired = false)
                Spacer(modifier = Modifier.height(4.dp))
                GrowthStageDropdown(
                    selectedStage = growthStage,
                    onStageChange = { growthStage = it }
                )

                FormTextField(
                    label = "Notes",
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "Additional details...",
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank() && amount.isNotBlank()) {
                        onSave(
                            FarmExpense(
                                cropType = cropType,
                                category = category.displayName,
                                description = description,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                expenseDate = expenseDate.toString(),
                                supplier = supplier.ifEmpty { null },
                                invoiceNumber = invoiceNumber.ifEmpty { null },
                                paymentMethod = paymentMethod.name,
                                growthStage = growthStage.name,
                                notes = notes.ifEmpty { null }
                            )
                        )
                    }
                },
                enabled = description.isNotBlank() && amount.isNotBlank()
            ) {
                Text("Save Expense")
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

