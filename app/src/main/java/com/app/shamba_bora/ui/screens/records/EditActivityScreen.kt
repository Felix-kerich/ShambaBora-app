package com.app.shamba_bora.ui.screens.records

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.viewmodel.FarmActivityViewModel

@Composable
fun EditActivityScreenWrapper(
    activityId: Long,
    onBack: () -> Unit,
    viewModel: FarmActivityViewModel = hiltViewModel()
) {
    // For now, just navigate back to the detail screen
    // In a full implementation, you would load the activity and show an edit form
    // For MVP, we'll use the Create screen which can be enhanced to support edit mode
    CreateActivityScreenWrapper(onBack = onBack)
}
