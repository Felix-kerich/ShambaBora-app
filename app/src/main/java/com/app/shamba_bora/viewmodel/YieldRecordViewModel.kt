package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.YieldRecord
import com.app.shamba_bora.data.model.YieldRecordRequest
import com.app.shamba_bora.data.model.YieldRecordResponse
import com.app.shamba_bora.data.model.MaizePatchDTO
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.data.repository.YieldRecordRepository
import com.app.shamba_bora.data.repository.PatchRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YieldRecordViewModel @Inject constructor(
    private val repository: YieldRecordRepository,
    private val patchRepository: PatchRepository
) : ViewModel() {
    
    private val _yieldsState = MutableStateFlow<Resource<PageResponse<YieldRecord>>>(Resource.Loading())
    val yieldsState: StateFlow<Resource<PageResponse<YieldRecord>>> = _yieldsState.asStateFlow()
    
    private val _yieldState = MutableStateFlow<Resource<YieldRecord>>(Resource.Loading())
    val yieldState: StateFlow<Resource<YieldRecord>> = _yieldState.asStateFlow()
    
    private val _totalYieldState = MutableStateFlow<Resource<Double>>(Resource.Loading())
    val totalYieldState: StateFlow<Resource<Double>> = _totalYieldState.asStateFlow()
    
    private val _totalRevenueState = MutableStateFlow<Resource<Double>>(Resource.Loading())
    val totalRevenueState: StateFlow<Resource<Double>> = _totalRevenueState.asStateFlow()
    
    // initialize as Error (empty) so forms don't show a loading spinner by default
    private val _createWithDTOState = MutableStateFlow<Resource<YieldRecordResponse>>(Resource.Error("", null))
    val createWithDTOState: StateFlow<Resource<YieldRecordResponse>> = _createWithDTOState.asStateFlow()
    
    private val _patchesState = MutableStateFlow<Resource<List<MaizePatchDTO>>>(Resource.Loading())
    val patchesState: StateFlow<Resource<List<MaizePatchDTO>>> = _patchesState.asStateFlow()
    
    init {
        loadYieldRecords()
        loadTotalYield()
        loadTotalRevenue()
    }
    
    fun loadYieldRecords(cropType: String? = null, page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _yieldsState.value = Resource.Loading()
            _yieldsState.value = repository.getYieldRecords(cropType, page, size)
        }
    }
    
    fun loadYieldRecord(id: Long) {
        viewModelScope.launch {
            _yieldState.value = Resource.Loading()
            _yieldState.value = repository.getYieldRecord(id)
        }
    }
    
    fun createYieldRecord(yield: YieldRecord) {
        viewModelScope.launch {
            repository.createYieldRecord(yield)
            loadYieldRecords()
            loadTotalYield()
            loadTotalRevenue()
        }
    }
    
    fun updateYieldRecord(id: Long, yield: YieldRecord) {
        viewModelScope.launch {
            repository.updateYieldRecord(id, yield)
            loadYieldRecords()
            loadTotalYield()
            loadTotalRevenue()
        }
    }
    
    fun deleteYieldRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteYieldRecord(id)
            loadYieldRecords()
            loadTotalYield()
            loadTotalRevenue()
        }
    }
    
    fun loadTotalYield(cropType: String? = null) {
        viewModelScope.launch {
            _totalYieldState.value = Resource.Loading()
            _totalYieldState.value = repository.getTotalYield(cropType)
        }
    }
    
    fun loadTotalRevenue(cropType: String? = null) {
        viewModelScope.launch {
            _totalRevenueState.value = Resource.Loading()
            _totalRevenueState.value = repository.getTotalRevenue(cropType)
        }
    }
    
    fun loadYieldTrends(cropType: String, startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            repository.getYieldTrends(cropType, startDate, endDate)
        }
    }
    
    // ========== New DTO Methods ==========
    fun createYieldRecordWithDTO(yield: YieldRecordRequest) {
        viewModelScope.launch {
            _createWithDTOState.value = Resource.Loading()
            _createWithDTOState.value = repository.createYieldRecordWithDTO(yield)
            if (_createWithDTOState.value is Resource.Success) {
                loadYieldRecords()
                loadTotalYield()
                loadTotalRevenue()
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

