package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.ActivityReminder
import com.app.shamba_bora.data.model.ActivityReminderRequest
import com.app.shamba_bora.data.model.FarmActivity
import com.app.shamba_bora.data.model.FarmActivityRequest
import com.app.shamba_bora.data.model.FarmActivityResponse
import com.app.shamba_bora.data.model.MaizePatchDTO
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.data.repository.FarmActivityRepository
import com.app.shamba_bora.data.repository.PatchRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmActivityViewModel @Inject constructor(
    private val repository: FarmActivityRepository,
    private val patchRepository: PatchRepository
) : ViewModel() {
    
    private val _activitiesState = MutableStateFlow<Resource<PageResponse<FarmActivity>>>(Resource.Loading())
    val activitiesState: StateFlow<Resource<PageResponse<FarmActivity>>> = _activitiesState.asStateFlow()
    
    private val _activityState = MutableStateFlow<Resource<FarmActivity>>(Resource.Loading())
    val activityState: StateFlow<Resource<FarmActivity>> = _activityState.asStateFlow()
    
    private val _createState = MutableStateFlow<Resource<FarmActivity>>(Resource.Loading())
    val createState: StateFlow<Resource<FarmActivity>> = _createState.asStateFlow()
    
    private val _remindersState = MutableStateFlow<Resource<List<ActivityReminder>>>(Resource.Loading())
    val remindersState: StateFlow<Resource<List<ActivityReminder>>> = _remindersState.asStateFlow()
    
    // initialize as Error (empty) so forms don't show a loading spinner by default
    private val _createWithDTOState = MutableStateFlow<Resource<FarmActivityResponse>>(Resource.Error("", null))
    val createWithDTOState: StateFlow<Resource<FarmActivityResponse>> = _createWithDTOState.asStateFlow()
    
    private val _patchesState = MutableStateFlow<Resource<List<MaizePatchDTO>>>(Resource.Loading())
    val patchesState: StateFlow<Resource<List<MaizePatchDTO>>> = _patchesState.asStateFlow()
    
    init {
        loadActivities()
    }
    
    fun loadActivities(activityType: String? = null, page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _activitiesState.value = Resource.Loading()
            _activitiesState.value = repository.getActivities(activityType, patchId = null, page, size)
        }
    }
    
    fun loadActivitiesByPatch(patchId: Long, page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _activitiesState.value = Resource.Loading()
            _activitiesState.value = repository.getActivities(activityType = null, patchId = patchId, page, size)
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
    
    // ========== New DTO Methods ==========
    fun createActivityWithDTO(activity: FarmActivityRequest) {
        viewModelScope.launch {
            _createWithDTOState.value = Resource.Loading()
            _createWithDTOState.value = repository.createActivityWithDTO(activity)
            if (_createWithDTOState.value is Resource.Success) {
                loadActivities()
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

