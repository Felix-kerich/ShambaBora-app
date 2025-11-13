package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.FarmerProfile
import com.app.shamba_bora.data.model.FarmerProfileRequest
import com.app.shamba_bora.data.model.User
import com.app.shamba_bora.data.network.UpdateUserRequest
import com.app.shamba_bora.data.repository.UserRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    
    private val _userState = MutableStateFlow<Resource<User>>(Resource.Loading())
    val userState: StateFlow<Resource<User>> = _userState.asStateFlow()
    
    private val _farmerProfileState = MutableStateFlow<Resource<FarmerProfile>>(Resource.Loading())
    val farmerProfileState: StateFlow<Resource<FarmerProfile>> = _farmerProfileState.asStateFlow()
    
    // Start with null (idle state) instead of Loading
    private val _updateUserState = MutableStateFlow<Resource<User>?>(null)
    val updateUserState: StateFlow<Resource<User>?> = _updateUserState.asStateFlow()
    
    // Start with null (idle state) instead of Loading
    private val _updateFarmerProfileState = MutableStateFlow<Resource<FarmerProfile>?>(null)
    val updateFarmerProfileState: StateFlow<Resource<FarmerProfile>?> = _updateFarmerProfileState.asStateFlow()
    
    fun loadUser() {
        viewModelScope.launch {
            _userState.value = Resource.Loading()
            _userState.value = repository.getCurrentUser()
        }
    }
    
    fun loadFarmerProfile() {
        viewModelScope.launch {
            _farmerProfileState.value = Resource.Loading()
            _farmerProfileState.value = repository.getMyFarmerProfile()
        }
    }
    
    fun updateUser(request: UpdateUserRequest) {
        viewModelScope.launch {
            _updateUserState.value = Resource.Loading()
            val result = repository.updateUser(request)
            _updateUserState.value = result
            // Only reload if success
            if (result is Resource.Success) {
                kotlinx.coroutines.delay(500)  // Small delay to ensure UI reacts before reload
                loadUser()
                // Reset to null (idle) after successful update
                _updateUserState.value = null
            }
        }
    }
    
    fun createFarmerProfile(request: FarmerProfileRequest) {
        viewModelScope.launch {
            _updateFarmerProfileState.value = Resource.Loading()
            val result = repository.createFarmerProfile(request)
            _updateFarmerProfileState.value = result
            // Only reload if success
            if (result is Resource.Success) {
                kotlinx.coroutines.delay(500)  // Small delay to ensure UI reacts before reload
                loadFarmerProfile()
                // Reset to null (idle) after successful creation
                _updateFarmerProfileState.value = null
            }
        }
    }
    
    fun updateFarmerProfile(request: FarmerProfileRequest) {
        viewModelScope.launch {
            _updateFarmerProfileState.value = Resource.Loading()
            val result = repository.updateMyFarmerProfile(request)
            _updateFarmerProfileState.value = result
            // Only reload if success
            if (result is Resource.Success) {
                kotlinx.coroutines.delay(500)  // Small delay to ensure UI reacts before reload
                loadFarmerProfile()
                // Reset to null (idle) after successful update
                _updateFarmerProfileState.value = null
            }
        }
    }
    
    fun refreshUser() {
        loadUser()
    }
    
    fun refreshFarmerProfile() {
        loadFarmerProfile()
    }
}

