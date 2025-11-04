package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.DirectMessage
import com.app.shamba_bora.data.model.Group
import com.app.shamba_bora.data.model.Message
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.data.repository.CollaborationRepository
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val repository: CollaborationRepository
) : ViewModel() {
    
    // Conversations
    private val _conversationsState = MutableStateFlow<Resource<PageResponse<DirectMessage>>>(Resource.Loading())
    val conversationsState: StateFlow<Resource<PageResponse<DirectMessage>>> = _conversationsState.asStateFlow()
    
    // Direct Messages
    private val _messagesState = MutableStateFlow<Resource<PageResponse<DirectMessage>>>(Resource.Loading())
    val messagesState: StateFlow<Resource<PageResponse<DirectMessage>>> = _messagesState.asStateFlow()
    
    // Group Messages
    private val _groupMessagesState = MutableStateFlow<Resource<PageResponse<Message>>>(Resource.Loading())
    val groupMessagesState: StateFlow<Resource<PageResponse<Message>>> = _groupMessagesState.asStateFlow()
    
    // Groups
    private val _groupsState = MutableStateFlow<Resource<List<Group>>>(Resource.Loading())
    val groupsState: StateFlow<Resource<List<Group>>> = _groupsState.asStateFlow()
    
    // Current conversation partner
    private val _currentPartnerId = MutableStateFlow<Long?>(null)
    val currentPartnerId: StateFlow<Long?> = _currentPartnerId.asStateFlow()
    
    // Current group
    private val _currentGroupId = MutableStateFlow<Long?>(null)
    val currentGroupId: StateFlow<Long?> = _currentGroupId.asStateFlow()
    
    // Send message state
    private val _sendMessageState = MutableStateFlow<Resource<DirectMessage>>(Resource.Loading())
    val sendMessageState: StateFlow<Resource<DirectMessage>> = _sendMessageState.asStateFlow()
    
    // Send group message state
    private val _sendGroupMessageState = MutableStateFlow<Resource<Message>>(Resource.Loading())
    val sendGroupMessageState: StateFlow<Resource<Message>> = _sendGroupMessageState.asStateFlow()
    
    fun loadConversations(page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            _conversationsState.value = Resource.Loading()
            _conversationsState.value = repository.getRecentConversations(page, size)
        }
    }
    
    fun loadConversation(otherUserId: Long, page: Int = 0, size: Int = 50) {
        viewModelScope.launch {
            _currentPartnerId.value = otherUserId
            _messagesState.value = Resource.Loading()
            _messagesState.value = repository.getConversation(otherUserId, page, size)
        }
    }
    
    fun sendDirectMessage(message: DirectMessage) {
        viewModelScope.launch {
            _sendMessageState.value = Resource.Loading()
            _sendMessageState.value = repository.sendDirectMessage(message)
            // Reload conversation after sending
            if (_sendMessageState.value is Resource.Success) {
                _currentPartnerId.value?.let { partnerId ->
                    loadConversation(partnerId)
                }
            }
        }
    }
    
    fun markMessageAsRead(messageId: Long) {
        viewModelScope.launch {
            repository.markMessageAsRead(messageId)
        }
    }
    
    fun loadGroups() {
        viewModelScope.launch {
            _groupsState.value = Resource.Loading()
            _groupsState.value = repository.getMyGroups()
        }
    }
    
    fun loadGroupMessages(groupId: Long, page: Int = 0, size: Int = 50) {
        viewModelScope.launch {
            _currentGroupId.value = groupId
            _groupMessagesState.value = Resource.Loading()
            _groupMessagesState.value = repository.getGroupMessages(groupId, page, size)
        }
    }
    
    fun sendGroupMessage(message: Message) {
        viewModelScope.launch {
            _sendGroupMessageState.value = Resource.Loading()
            _sendGroupMessageState.value = repository.sendGroupMessage(message)
            // Reload messages after sending
            if (_sendGroupMessageState.value is Resource.Success) {
                _currentGroupId.value?.let { groupId ->
                    loadGroupMessages(groupId)
                }
            }
        }
    }
    
    fun refreshConversations() {
        loadConversations()
    }
    
    fun refreshMessages() {
        _currentPartnerId.value?.let { partnerId ->
            loadConversation(partnerId)
        }
    }
    
    fun refreshGroupMessages() {
        _currentGroupId.value?.let { groupId ->
            loadGroupMessages(groupId)
        }
    }
}

