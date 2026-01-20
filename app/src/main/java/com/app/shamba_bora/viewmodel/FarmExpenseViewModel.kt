package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.FarmExpense
import com.app.shamba_bora.data.model.FarmExpenseRequest
import com.app.shamba_bora.data.model.FarmExpenseResponse
import com.app.shamba_bora.data.model.MaizePatchDTO
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.data.repository.FarmExpenseRepository
import com.app.shamba_bora.data.repository.PatchRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmExpenseViewModel @Inject constructor(
    private val repository: FarmExpenseRepository,
    private val patchRepository: PatchRepository
) : ViewModel() {
    
    private val _expensesState = MutableStateFlow<Resource<PageResponse<FarmExpense>>>(Resource.Loading())
    val expensesState: StateFlow<Resource<PageResponse<FarmExpense>>> = _expensesState.asStateFlow()
    
    private val _expenseState = MutableStateFlow<Resource<FarmExpense>>(Resource.Loading())
    val expenseState: StateFlow<Resource<FarmExpense>> = _expenseState.asStateFlow()
    
    private val _totalExpensesState = MutableStateFlow<Resource<Double>>(Resource.Loading())
    val totalExpensesState: StateFlow<Resource<Double>> = _totalExpensesState.asStateFlow()
    
    private val _expensesByCategoryState = MutableStateFlow<Resource<Map<String, Double>>>(Resource.Loading())
    val expensesByCategoryState: StateFlow<Resource<Map<String, Double>>> = _expensesByCategoryState.asStateFlow()
    
    // initialize as Error (empty) so forms don't show a loading spinner by default
    private val _createWithDTOState = MutableStateFlow<Resource<FarmExpenseResponse>>(Resource.Error("", null))
    val createWithDTOState: StateFlow<Resource<FarmExpenseResponse>> = _createWithDTOState.asStateFlow()
    
    private val _patchesState = MutableStateFlow<Resource<List<MaizePatchDTO>>>(Resource.Loading())
    val patchesState: StateFlow<Resource<List<MaizePatchDTO>>> = _patchesState.asStateFlow()
    
    init {
        loadExpenses()
        loadTotalExpenses()
    }
    
    fun loadExpenses(cropType: String? = null, category: String? = null, page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _expensesState.value = Resource.Loading()
            _expensesState.value = repository.getExpenses(cropType, category, patchId = null, page, size)
        }
    }
    
    fun loadExpensesByPatch(patchId: Long, page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _expensesState.value = Resource.Loading()
            _expensesState.value = repository.getExpenses(cropType = null, category = null, patchId = patchId, page, size)
        }
    }
    
    fun loadExpense(id: Long) {
        viewModelScope.launch {
            _expenseState.value = Resource.Loading()
            _expenseState.value = repository.getExpense(id)
        }
    }
    
    fun createExpense(expense: FarmExpense) {
        viewModelScope.launch {
            repository.createExpense(expense)
            loadExpenses()
            loadTotalExpenses()
        }
    }
    
    fun updateExpense(id: Long, expense: FarmExpense) {
        viewModelScope.launch {
            repository.updateExpense(id, expense)
            loadExpenses()
            loadTotalExpenses()
        }
    }
    
    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteExpense(id)
            loadExpenses()
            loadTotalExpenses()
        }
    }
    
    fun loadTotalExpenses(cropType: String? = null) {
        viewModelScope.launch {
            _totalExpensesState.value = Resource.Loading()
            _totalExpensesState.value = repository.getTotalExpenses(cropType)
        }
    }
    
    fun loadExpensesByCategory(cropType: String) {
        viewModelScope.launch {
            _expensesByCategoryState.value = Resource.Loading()
            _expensesByCategoryState.value = repository.getExpensesByCategory(cropType)
        }
    }
    
    // ========== New DTO Methods ==========
    fun createExpenseWithDTO(expense: FarmExpenseRequest) {
        viewModelScope.launch {
            _createWithDTOState.value = Resource.Loading()
            _createWithDTOState.value = repository.createExpenseWithDTO(expense)
            if (_createWithDTOState.value is Resource.Success) {
                loadExpenses()
                loadTotalExpenses()
            }
        }
    }
    
    fun loadPatches() {
        viewModelScope.launch {
            _patchesState.value = Resource.Loading()
            _patchesState.value = patchRepository.getPatches()
        }
    }
}

