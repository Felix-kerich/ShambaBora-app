package com.app.shamba_bora.ui.screens.records

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.viewmodel.FarmExpenseViewModel

@Composable
fun EditExpenseScreenWrapper(
    expenseId: Long,
    onBack: () -> Unit,
    viewModel: FarmExpenseViewModel = hiltViewModel()
) {
    // For now, just navigate to create expense screen
    // In a full implementation, you would load the expense and show an edit form
    CreateExpenseScreenWrapper(onBack = onBack)
}
