package com.app.shamba_bora.ui.screens.records

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.viewmodel.YieldRecordViewModel

@Composable
fun EditYieldScreenWrapper(
    yieldId: Long,
    onBack: () -> Unit,
    viewModel: YieldRecordViewModel = hiltViewModel()
) {
    // For now, just navigate to create yield screen
    // In a full implementation, you would load the yield and show an edit form
    CreateYieldScreenWrapper(onBack = onBack)
}
