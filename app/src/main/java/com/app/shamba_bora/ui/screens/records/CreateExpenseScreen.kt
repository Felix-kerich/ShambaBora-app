package com.app.shamba_bora.ui.screens.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.data.constants.FarmingInputs
import com.app.shamba_bora.ui.components.ErrorView
import com.app.shamba_bora.ui.components.LoadingIndicator
import com.app.shamba_bora.ui.components.records.*
import com.app.shamba_bora.utils.Resource
import com.app.shamba_bora.viewmodel.FarmExpenseViewModel
import java.time.LocalDate

@Composable
fun CreateExpenseScreenWrapper(
    onBack: () -> Unit,
    viewModel: FarmExpenseViewModel = hiltViewModel()
) {
    val patchesState by viewModel.patchesState.collectAsState()
    val createState by viewModel.createWithDTOState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadPatches()
    }
    
    LaunchedEffect(createState) {
        if (createState is Resource.Success) {
            onBack()
        }
    }
    
    val patches = when (val state = patchesState) {
        is Resource.Success -> state.data ?: emptyList()
        else -> emptyList()
    }
    
    val isLoading = createState is Resource.Loading
    
    CreateExpenseScreen(
        patches = patches,
        onCreateExpense = { expense ->
            viewModel.createExpenseWithDTO(expense)
        },
        onBack = onBack,
        isLoading = isLoading
    )
}

@Composable
fun CreateExpenseScreen(
    patches: List<MaizePatchDTO> = emptyList(),
    onCreateExpense: (FarmExpenseRequest) -> Unit = {},
    onBack: () -> Unit = {},
    isLoading: Boolean = false
) {
    var cropType by remember { mutableStateOf("Maize") }
    var category by remember { mutableStateOf(ExpenseCategory.SEEDS) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expenseDate by remember { mutableStateOf(LocalDate.now()) }
    var supplier by remember { mutableStateOf("") }
    var invoiceNumber by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var growthStage by remember { mutableStateOf(GrowthStage.PRE_PLANTING) }
    var selectedPatchId by remember { mutableStateOf<Long?>(null) }
    var isRecurring by remember { mutableStateOf(false) }
    var recurringFrequency by remember { mutableStateOf(RecurringFrequency.MONTHLY) }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text(
                    text = "Record Farm Expense",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track all farm-related costs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Expense Basics
            item {
                FormSection(title = "Expense Information") {
                    ExpenseCategoryDropdown(
                        selectedCategory = category,
                        onCategoryChange = { category = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormTextField(
                        label = "Description",
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "What was purchased/paid for?",
                        isRequired = true,
                        minLines = 2,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormNumberField(
                        label = "Amount (Currency)",
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = "0.00",
                        isRequired = true
                    )
                }
            }

            // Date & Supplier
            item {
                FormSection(title = "Date & Supplier") {
                    FormDateField(
                        label = "Expense Date",
                        selectedDate = expenseDate,
                        onDateChange = { expenseDate = it },
                        isRequired = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormTextField(
                        label = "Supplier Name",
                        value = supplier,
                        onValueChange = { supplier = it },
                        placeholder = "e.g., Local Agro-Dealer"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchableDropdown(
                        label = "Select Supplier (or type custom)",
                        value = supplier,
                        onValueChange = { supplier = it },
                        options = FarmingInputs.SUPPLIERS,
                        placeholder = "Search suppliers...",
                        allowCustomInput = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FormTextField(
                        label = "Invoice Number",
                        value = invoiceNumber,
                        onValueChange = { invoiceNumber = it },
                        placeholder = "Receipt or invoice number"
                    )
                }
            }

            // Payment Details
            item {
                FormSection(title = "Payment Information") {
                    PaymentMethodDropdown(
                        selectedMethod = paymentMethod,
                        onMethodChange = { paymentMethod = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GrowthStageDropdown(
                        selectedStage = growthStage,
                        onStageChange = { growthStage = it }
                    )
                }
            }

            // Patch Selection
            item {
                FormSection(title = "Assign to Patch") {
                    PatchSelectorDropdown(
                        patches = patches,
                        selectedPatchId = selectedPatchId,
                        onPatchSelect = { selectedPatchId = it }
                    )
                }
            }

            // Recurring Options
            item {
                FormSection(title = "Recurring Expense") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "This is a recurring expense",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Checkbox(
                            checked = isRecurring,
                            onCheckedChange = { isRecurring = it }
                        )
                    }

                    if (isRecurring) {
                        Spacer(modifier = Modifier.height(12.dp))
                        RecurringFrequencyDropdown(
                            selectedFrequency = recurringFrequency,
                            onFrequencyChange = { recurringFrequency = it }
                        )
                    }
                }
            }

            // Advanced Options
            item {
                AdvancedOptionsSection(title = "Additional Details") {
                    FormTextField(
                        label = "Notes",
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = "Any additional information...",
                        minLines = 2,
                        maxLines = 3
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Submit Button
        FormSubmitButton(
            text = "Save Expense",
            onClick = {
                val expense = FarmExpenseRequest(
                    cropType = cropType,
                    category = category.name,
                    description = description,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    expenseDate = expenseDate,
                    supplier = supplier,
                    invoiceNumber = invoiceNumber,
                    paymentMethod = paymentMethod.name,
                    growthStage = growthStage.name,
                    patchId = selectedPatchId,
                    isRecurring = isRecurring,
                    recurringFrequency = if (isRecurring) recurringFrequency.name else "",
                    notes = notes
                )
                if (expense.isValid()) {
                    onCreateExpense(expense)
                }
            },
            enabled = description.isNotBlank() && amount.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
            isLoading = isLoading
        )
    }
}
