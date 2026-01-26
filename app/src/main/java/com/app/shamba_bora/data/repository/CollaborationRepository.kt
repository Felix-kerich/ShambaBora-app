package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.*
import com.app.shamba_bora.data.network.ApiResponse
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.utils.PreferenceManager
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class CollaborationRepository @Inject constructor(
    private val apiService: ApiService
) {
    private fun getUserId(): Long = PreferenceManager.getUserId()
    
    // ========== POSTS ==========
    suspend fun getFeed(page: Int = 0, size: Int = 20): Resource<PageResponse<Post>> {
        return try {
            val response = apiService.getFeed(getUserId(), page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get feed")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get feed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun createPost(post: Post): Resource<Post> {
        return try {
            val response = apiService.createPost(getUserId(), post)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to create post")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to create post")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun createGroupPost(groupId: Long, post: Post): Resource<Post> {
        return try {
            val response = apiService.createGroupPost(getUserId(), groupId, post)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to create group post")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to create group post")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getGroupPosts(
        groupId: Long,
        page: Int = 0,
        size: Int = 20
    ): Resource<PageResponse<Post>> {
        return try {
            val response = apiService.getGroupPosts(getUserId(), groupId, page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get group posts")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get group posts")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun likePost(postId: Long): Resource<Post> {
        return try {
            val response = apiService.likePost(getUserId(), postId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to like post")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to like post")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun unlikePost(postId: Long): Resource<Post> {
        return try {
            val response = apiService.unlikePost(getUserId(), postId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to unlike post")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to unlike post")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun addComment(postId: Long, comment: PostComment): Resource<PostComment> {
        return try {
            val response = apiService.addComment(getUserId(), postId, comment)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to add comment")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to add comment")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getPostComments(
        postId: Long,
        page: Int = 0,
        size: Int = 10
    ): Resource<PageResponse<PostComment>> {
        return try {
            val response = apiService.getPostComments(postId, page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get comments")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get comments")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    // ========== DIRECT MESSAGES ==========
    suspend fun sendDirectMessage(message: DirectMessage): Resource<DirectMessage> {
        return try {
            val response = apiService.sendDirectMessage(getUserId(), message)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to send message")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to send message")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getConversation(
        otherUserId: Long,
        page: Int = 0,
        size: Int = 50
    ): Resource<PageResponse<DirectMessage>> {
        return try {
            val response = apiService.getConversation(getUserId(), otherUserId, page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get conversation")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get conversation")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getRecentConversations(
        page: Int = 0,
        size: Int = 20
    ): Resource<PageResponse<DirectMessage>> {
        return try {
            val response = apiService.getRecentConversations(getUserId(), page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get conversations")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get conversations")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun markMessageAsRead(messageId: Long): Resource<DirectMessage> {
        return try {
            val response = apiService.markMessageAsRead(getUserId(), messageId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to mark message as read")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to mark message as read")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    // ========== GROUPS ==========
    suspend fun getMyGroups(): Resource<List<Group>> {
        return try {
            val response = apiService.getMyGroups(getUserId())
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get groups")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get groups")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun browseGroups(
        search: String? = null,
        page: Int = 0,
        size: Int = 20
    ): Resource<PageResponse<Group>> {
        return try {
            val response = apiService.browseGroups(getUserId(), search, page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to browse groups")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to browse groups")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getGroup(groupId: Long): Resource<Group> {
        return try {
            val response = apiService.getGroup(getUserId(), groupId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get group")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get group")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun createGroup(group: Group): Resource<Group> {
        return try {
            val response = apiService.createGroup(getUserId(), group)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to create group")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to create group")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun joinGroup(groupId: Long): Resource<GroupMembership> {
        return try {
            val response = apiService.joinGroup(getUserId(), groupId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to join group")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to join group")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun leaveGroup(groupId: Long): Resource<String> {
        return try {
            val response = apiService.leaveGroup(getUserId(), groupId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to leave group")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to leave group")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getGroupMembers(
        groupId: Long,
        page: Int = 0,
        size: Int = 20
    ): Resource<PageResponse<GroupMembership>> {
        return try {
            val response = apiService.getGroupMembers(getUserId(), groupId, page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get group members")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get group members")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    // ========== GROUP MESSAGES ==========
    suspend fun sendGroupMessage(message: Message): Resource<Message> {
        return try {
            val response = apiService.sendGroupMessage(message)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to send message")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to send message")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getGroupMessages(
        groupId: Long,
        page: Int = 0,
        size: Int = 20
    ): Resource<PageResponse<Message>> {
        return try {
            val response = apiService.getGroupMessages(groupId, page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get messages")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get messages")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

