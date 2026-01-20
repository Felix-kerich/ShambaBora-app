package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.db.entities.SavedAdvice
import com.app.shamba_bora.data.model.FarmAdviceResponse
import com.app.shamba_bora.data.repository.SavedAdviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing saved farm advice
 */
@HiltViewModel
class SavedAdviceViewModel @Inject constructor(
    private val repository: SavedAdviceRepository
) : ViewModel() {
    
    // State for saved advices list
    val savedAdvices: StateFlow<List<SavedAdvice>> = repository.getAllSavedAdvices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // State for selected advice to view
    private val _selectedAdvice = MutableStateFlow<FarmAdviceResponse?>(null)
    val selectedAdvice: StateFlow<FarmAdviceResponse?> = _selectedAdvice.asStateFlow()
    
    // State for showing advice dialog
    private val _showAdviceDialog = MutableStateFlow(false)
    val showAdviceDialog: StateFlow<Boolean> = _showAdviceDialog.asStateFlow()
    
    // UI state for operations
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    /**
     * Load a saved advice by ID and display it
     */
    fun loadAndDisplayAdvice(id: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val savedAdvice = repository.getSavedAdviceById(id)
                if (savedAdvice != null) {
                    _selectedAdvice.value = repository.toFarmAdviceResponse(savedAdvice)
                    _showAdviceDialog.value = true
                    _uiState.value = UiState.Success
                } else {
                    _uiState.value = UiState.Error("Advice not found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load advice")
            }
        }
    }
    
    /**
     * Delete a saved advice
     */
    fun deleteAdvice(advice: SavedAdvice) {
        viewModelScope.launch {
            try {
                repository.deleteAdvice(advice)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete advice")
            }
        }
    }
    
    /**
     * Close the advice dialog
     */
    fun closeAdviceDialog() {
        _showAdviceDialog.value = false
        _selectedAdvice.value = null
    }
    
    /**
     * Reset UI state
     */
    fun resetUiState() {
        _uiState.value = UiState.Idle
    }
    
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }
}
