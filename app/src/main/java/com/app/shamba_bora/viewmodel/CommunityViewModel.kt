package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.*
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
class CommunityViewModel @Inject constructor(
    private val repository: CollaborationRepository
) : ViewModel() {
    
    private val _feedState = MutableStateFlow<Resource<PageResponse<Post>>>(Resource.Loading())
    val feedState: StateFlow<Resource<PageResponse<Post>>> = _feedState.asStateFlow()
    
    private val _groupsState = MutableStateFlow<Resource<List<Group>>>(Resource.Loading())
    val groupsState: StateFlow<Resource<List<Group>>> = _groupsState.asStateFlow()
    
    private val _browseGroupsState = MutableStateFlow<Resource<PageResponse<Group>>>(Resource.Loading())
    val browseGroupsState: StateFlow<Resource<PageResponse<Group>>> = _browseGroupsState.asStateFlow()
    
    private val _conversationsState = MutableStateFlow<Resource<PageResponse<DirectMessage>>>(Resource.Loading())
    val conversationsState: StateFlow<Resource<PageResponse<DirectMessage>>> = _conversationsState.asStateFlow()
    
    private val _messagesState = MutableStateFlow<Resource<PageResponse<DirectMessage>>>(Resource.Loading())
    val messagesState: StateFlow<Resource<PageResponse<DirectMessage>>> = _messagesState.asStateFlow()
    
    private val _commentsState = MutableStateFlow<Resource<PageResponse<PostComment>>>(Resource.Loading())
    val commentsState: StateFlow<Resource<PageResponse<PostComment>>> = _commentsState.asStateFlow()
    
    init {
        loadFeed()
        loadMyGroups()
        loadRecentConversations()
    }
    
    // Posts
    fun loadFeed(page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            _feedState.value = Resource.Loading()
            _feedState.value = repository.getFeed(page, size)
        }
    }
    
    fun createPost(post: Post) {
        viewModelScope.launch {
            repository.createPost(post)
            loadFeed()
        }
    }
    
    fun likePost(postId: Long) {
        viewModelScope.launch {
            repository.likePost(postId)
            loadFeed()
        }
    }
    
    fun unlikePost(postId: Long) {
        viewModelScope.launch {
            repository.unlikePost(postId)
            loadFeed()
        }
    }
    
    fun addComment(postId: Long, comment: PostComment) {
        viewModelScope.launch {
            repository.addComment(postId, comment)
            loadFeed() // Refresh feed to show updated comment count
            loadComments(postId) // Refresh comments
        }
    }
    
    fun loadComments(postId: Long, page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            _commentsState.value = Resource.Loading()
            _commentsState.value = repository.getPostComments(postId, page, size)
        }
    }
    
    // Groups
    fun loadMyGroups() {
        viewModelScope.launch {
            _groupsState.value = Resource.Loading()
            _groupsState.value = repository.getMyGroups()
        }
    }
    
    fun browseGroups(search: String? = null, page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            _browseGroupsState.value = Resource.Loading()
            _browseGroupsState.value = repository.browseGroups(search, page, size)
        }
    }
    
    fun createGroup(group: Group) {
        viewModelScope.launch {
            repository.createGroup(group)
            loadMyGroups()
        }
    }
    
    fun joinGroup(groupId: Long) {
        viewModelScope.launch {
            repository.joinGroup(groupId)
            loadMyGroups()
        }
    }
    
    fun leaveGroup(groupId: Long) {
        viewModelScope.launch {
            repository.leaveGroup(groupId)
            loadMyGroups()
        }
    }
    
    // Messages
    fun loadRecentConversations(page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            _conversationsState.value = Resource.Loading()
            _conversationsState.value = repository.getRecentConversations(page, size)
        }
    }
    
    fun loadConversation(otherUserId: Long, page: Int = 0, size: Int = 50) {
        viewModelScope.launch {
            _messagesState.value = Resource.Loading()
            _messagesState.value = repository.getConversation(otherUserId, page, size)
        }
    }
    
    fun sendDirectMessage(message: DirectMessage) {
        viewModelScope.launch {
            repository.sendDirectMessage(message)
            loadConversation(message.recipientId)
        }
    }
    
    fun markMessageAsRead(messageId: Long) {
        viewModelScope.launch {
            repository.markMessageAsRead(messageId)
        }
    }
}

