package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.ActivityReminder
import com.app.shamba_bora.data.model.ActivityReminderRequest
import com.app.shamba_bora.data.model.FarmActivity
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.data.repository.FarmActivityRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmActivityViewModel @Inject constructor(
    private val repository: FarmActivityRepository
) : ViewModel() {
    
    private val _activitiesState = MutableStateFlow<Resource<PageResponse<FarmActivity>>>(Resource.Loading())
    val activitiesState: StateFlow<Resource<PageResponse<FarmActivity>>> = _activitiesState.asStateFlow()
    
    private val _activityState = MutableStateFlow<Resource<FarmActivity>>(Resource.Loading())
    val activityState: StateFlow<Resource<FarmActivity>> = _activityState.asStateFlow()
    
    private val _createState = MutableStateFlow<Resource<FarmActivity>>(Resource.Loading())
    val createState: StateFlow<Resource<FarmActivity>> = _createState.asStateFlow()
    
    private val _remindersState = MutableStateFlow<Resource<List<ActivityReminder>>>(Resource.Loading())
    val remindersState: StateFlow<Resource<List<ActivityReminder>>> = _remindersState.asStateFlow()
    
    init {
        loadActivities()
    }
    
    fun loadActivities(activityType: String? = null, page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _activitiesState.value = Resource.Loading()
            _activitiesState.value = repository.getActivities(activityType, page, size)
        }
    }
    
    fun loadActivity(id: Long) {
        viewModelScope.launch {
            _activityState.value = Resource.Loading()
            _activityState.value = repository.getActivity(id)
        }
    }
    
    fun createActivity(activity: FarmActivity) {
        viewModelScope.launch {
            _createState.value = Resource.Loading()
            _createState.value = repository.createActivity(activity)
            if (_createState.value is Resource.Success) {
                loadActivities()
            }
        }
    }
    
    fun updateActivity(id: Long, activity: FarmActivity) {
        viewModelScope.launch {
            repository.updateActivity(id, activity)
            loadActivities()
        }
    }
    
    fun deleteActivity(id: Long) {
        viewModelScope.launch {
            repository.deleteActivity(id)
            loadActivities()
        }
    }
    
    fun loadActivityReminders(activityId: Long) {
        viewModelScope.launch {
            _remindersState.value = Resource.Loading()
            _remindersState.value = repository.getActivityReminders(activityId)
        }
    }
    
    fun addReminder(activityId: Long, request: ActivityReminderRequest) {
        viewModelScope.launch {
            repository.addActivityReminder(activityId, request)
            loadActivityReminders(activityId)
        }
    }
    
    fun getUpcomingReminders() {
        viewModelScope.launch {
            _remindersState.value = Resource.Loading()
            _remindersState.value = repository.getUpcomingReminders()
        }
    }
}

