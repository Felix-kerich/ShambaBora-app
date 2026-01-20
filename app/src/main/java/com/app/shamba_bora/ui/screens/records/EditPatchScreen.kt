@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.app.shamba_bora.ui.screens.records

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.shamba_bora.viewmodel.PatchViewModel

@Composable
fun EditPatchScreenWrapper(
    patchId: Long,
    onBack: () -> Unit,
    viewModel: PatchViewModel = hiltViewModel()
) {
    // For now, just navigate to create patch screen
    // In a full implementation, you would load the patch and show an edit form
    CreatePatchScreenWrapper(onBack = onBack)
}
