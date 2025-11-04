package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.Dashboard
import com.app.shamba_bora.data.repository.DashboardRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {
    
    private val _dashboardState = MutableStateFlow<Resource<Dashboard>>(Resource.Loading())
    val dashboardState: StateFlow<Resource<Dashboard>> = _dashboardState.asStateFlow()
    
    init {
        loadDashboard()
    }
    
    fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.value = Resource.Loading()
            _dashboardState.value = repository.getDashboard()
        }
    }
    
    fun refreshDashboard() {
        loadDashboard()
    }
}

