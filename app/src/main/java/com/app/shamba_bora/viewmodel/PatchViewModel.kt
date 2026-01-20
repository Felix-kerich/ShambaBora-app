package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.MaizePatchDTO
import com.app.shamba_bora.data.model.PatchComparisonResponse
import com.app.shamba_bora.data.model.PatchSummaryDTO
import com.app.shamba_bora.data.repository.PatchRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatchViewModel @Inject constructor(
    private val repository: PatchRepository
) : ViewModel() {
    
    private val _patchesState = MutableStateFlow<Resource<List<MaizePatchDTO>>>(Resource.Loading())
    val patchesState: StateFlow<Resource<List<MaizePatchDTO>>> = _patchesState.asStateFlow()
    
    private val _patchState = MutableStateFlow<Resource<MaizePatchDTO>>(Resource.Loading())
    val patchState: StateFlow<Resource<MaizePatchDTO>> = _patchState.asStateFlow()
    
    // initialize as non-loading to avoid form submit button showing spinner on open
    private val _createState = MutableStateFlow<Resource<MaizePatchDTO>>(Resource.Error("", null))
    val createState: StateFlow<Resource<MaizePatchDTO>> = _createState.asStateFlow()
    
    private val _patchSummaryState = MutableStateFlow<Resource<PatchSummaryDTO>>(Resource.Loading())
    val patchSummaryState: StateFlow<Resource<PatchSummaryDTO>> = _patchSummaryState.asStateFlow()
    
    private val _comparisonState = MutableStateFlow<Resource<PatchComparisonResponse>>(Resource.Loading())
    val comparisonState: StateFlow<Resource<PatchComparisonResponse>> = _comparisonState.asStateFlow()
    
    init {
        loadPatches()
    }
    
    fun loadPatches() {
        viewModelScope.launch {
            _patchesState.value = Resource.Loading()
            _patchesState.value = repository.getPatches()
        }
    }
    
    fun loadPatch(id: Long) {
        viewModelScope.launch {
            _patchState.value = Resource.Loading()
            _patchState.value = repository.getPatch(id)
        }
    }
    
    fun createPatch(patch: MaizePatchDTO) {
        viewModelScope.launch {
            _createState.value = Resource.Loading()
            val result = repository.createPatch(patch)
            _createState.value = result
            if (result is Resource.Success) {
                loadPatches()
            }
        }
    }
    
    fun updatePatch(id: Long, patch: MaizePatchDTO) {
        viewModelScope.launch {
            repository.updatePatch(id, patch)
            loadPatches()
        }
    }
    
    fun deletePatch(id: Long) {
        viewModelScope.launch {
            repository.deletePatch(id)
            loadPatches()
        }
    }
    
    fun loadPatchSummary(patchId: Long) {
        viewModelScope.launch {
            _patchSummaryState.value = Resource.Loading()
            _patchSummaryState.value = repository.getPatchSummary(patchId)
        }
    }
    
    fun comparePatches(patchIds: List<Long>) {
        viewModelScope.launch {
            _comparisonState.value = Resource.Loading()
            _comparisonState.value = repository.comparePatches(patchIds)
        }
    }
}
