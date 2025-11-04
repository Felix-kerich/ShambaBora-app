package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.User
import com.app.shamba_bora.data.network.AuthResponse
import com.app.shamba_bora.data.network.LoginRequest
import com.app.shamba_bora.data.network.RegisterRequest
import com.app.shamba_bora.data.repository.AuthRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<Resource<AuthResponse>>(Resource.Loading())
    val loginState: StateFlow<Resource<AuthResponse>> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<Resource<AuthResponse>>(Resource.Loading())
    val registerState: StateFlow<Resource<AuthResponse>> = _registerState.asStateFlow()
    
    private val _userState = MutableStateFlow<Resource<User>>(Resource.Loading())
    val userState: StateFlow<Resource<User>> = _userState.asStateFlow()
    
    fun login(usernameOrEmail: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _loginState.value = repository.login(LoginRequest(usernameOrEmail, password))
        }
    }
    
    fun register(
        username: String,
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String? = null
    ) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            _registerState.value = repository.register(
                RegisterRequest(username, email, password, fullName, phoneNumber)
            )
        }
    }
    
    fun getCurrentUser() {
        viewModelScope.launch {
            _userState.value = Resource.Loading()
            _userState.value = repository.getCurrentUser()
        }
    }
    
    fun logout() {
        repository.logout()
    }
    
    fun clearLoginState() {
        _loginState.value = Resource.Loading()
    }
    
    fun clearRegisterState() {
        _registerState.value = Resource.Loading()
    }
}

