package com.app.shamba_bora.data.network

import com.app.shamba_bora.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ========== AUTHENTICATION ==========
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    // ========== USER MANAGEMENT ==========
    @GET("users/profile")
    suspend fun getCurrentUser(): Response<User>
    
    @PUT("users/profile")
    suspend fun updateUser(@Body request: UpdateUserRequest): Response<User>
    
    @DELETE("users/profile")
    suspend fun deleteUser(): Response<Unit>
    
    // ========== FARMER PROFILE ==========
    @GET("farmer-profile/me")
    suspend fun getMyFarmerProfile(): Response<FarmerProfile>
    
    @POST("farmer-profile")
    suspend fun createFarmerProfile(@Body request: FarmerProfileRequest): Response<FarmerProfile>
    
    @PUT("farmer-profile/me")
    suspend fun updateMyFarmerProfile(@Body request: FarmerProfileRequest): Response<FarmerProfile>
    
    // ========== FARM DASHBOARD ==========
    @GET("farm-dashboard")
    suspend fun getDashboard(): Response<Dashboard>
    
    // ========== FARM ACTIVITIES ==========
    @GET("farm-activities")
    suspend fun getActivities(
        @Query("activityType") activityType: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PageResponse<FarmActivity>>
    
    @GET("farm-activities/{id}")
    suspend fun getActivity(@Path("id") id: Long): Response<FarmActivity>
    
    @POST("farm-activities")
    suspend fun createActivity(@Body activity: FarmActivity): Response<FarmActivity>
    
    @PUT("farm-activities/{id}")
    suspend fun updateActivity(@Path("id") id: Long, @Body activity: FarmActivity): Response<FarmActivity>
    
    @DELETE("farm-activities/{id}")
    suspend fun deleteActivity(@Path("id") id: Long): Response<Unit>
    
    @GET("farm-activities/{id}/reminders")
    suspend fun getActivityReminders(@Path("id") id: Long): Response<List<ActivityReminder>>
    
    @POST("farm-activities/{id}/reminders")
    suspend fun addActivityReminder(
        @Path("id") id: Long,
        @Body request: ActivityReminderRequest
    ): Response<ActivityReminder>
    
    @GET("farm-activities/reminders/upcoming")
    suspend fun getUpcomingReminders(): Response<List<ActivityReminder>>
    
    @GET("farm-activities/{id}/calendar")
    suspend fun exportActivityToCalendar(@Path("id") id: Long): Response<String>
    
    // ========== FARM EXPENSES ==========
    @GET("farm-expenses")
    suspend fun getExpenses(
        @Query("cropType") cropType: String? = null,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PageResponse<FarmExpense>>
    
    @GET("farm-expenses/{id}")
    suspend fun getExpense(@Path("id") id: Long): Response<FarmExpense>
    
    @POST("farm-expenses")
    suspend fun createExpense(@Body expense: FarmExpense): Response<FarmExpense>
    
    @PUT("farm-expenses/{id}")
    suspend fun updateExpense(@Path("id") id: Long, @Body expense: FarmExpense): Response<FarmExpense>
    
    @DELETE("farm-expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Long): Response<Unit>
    
    @GET("farm-expenses/total")
    suspend fun getTotalExpenses(@Query("cropType") cropType: String? = null): Response<Double>
    
    @GET("farm-expenses/breakdown/category")
    suspend fun getExpensesByCategory(@Query("cropType") cropType: String): Response<Map<String, Double>>
    
    @GET("farm-expenses/breakdown/growth-stage")
    suspend fun getExpensesByGrowthStage(@Query("cropType") cropType: String): Response<Map<String, Double>>
    
    // ========== YIELD RECORDS ==========
    @GET("yield-records")
    suspend fun getYieldRecords(
        @Query("cropType") cropType: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PageResponse<YieldRecord>>
    
    @GET("yield-records/{id}")
    suspend fun getYieldRecord(@Path("id") id: Long): Response<YieldRecord>
    
    @POST("yield-records")
    suspend fun createYieldRecord(@Body yield: YieldRecord): Response<YieldRecord>
    
    @PUT("yield-records/{id}")
    suspend fun updateYieldRecord(@Path("id") id: Long, @Body yield: YieldRecord): Response<YieldRecord>
    
    @DELETE("yield-records/{id}")
    suspend fun deleteYieldRecord(@Path("id") id: Long): Response<Unit>
    
    @GET("yield-records/total")
    suspend fun getTotalYield(@Query("cropType") cropType: String? = null): Response<Double>
    
    @GET("yield-records/revenue")
    suspend fun getTotalRevenue(@Query("cropType") cropType: String? = null): Response<Double>
    
    @GET("yield-records/average")
    suspend fun getAverageYieldPerUnit(@Query("cropType") cropType: String): Response<Double>
    
    @GET("yield-records/best")
    suspend fun getBestYieldPerUnit(@Query("cropType") cropType: String): Response<Double>
    
    @GET("yield-records/trends")
    suspend fun getYieldTrends(
        @Query("cropType") cropType: String,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<YieldRecord>>
    
    // ========== WEATHER ==========
    @GET("weather/current")
    suspend fun getCurrentWeather(@Query("location") location: String): Response<Weather>
    
    @GET("weather/forecast")
    suspend fun getWeatherForecast(@Query("location") location: String): Response<Weather>
    
    @GET("weather/forecast/daily")
    suspend fun getDailyForecast(
        @Query("location") location: String,
        @Query("days") days: Int = 7
    ): Response<Weather>
    
    @GET("weather/forecast/monthly")
    suspend fun getMonthlyStats(
        @Query("location") location: String,
        @Query("month") month: Int
    ): Response<Weather>
    
    // ========== MARKETPLACE - PRODUCTS ==========
    @GET("marketplace/products")
    suspend fun getProducts(
        @Query("q") query: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PageResponse<Product>>>
    
    @GET("marketplace/products/{id}")
    suspend fun getProduct(@Path("id") id: Long): Response<ApiResponse<Product>>
    
    @POST("marketplace/products")
    suspend fun createProduct(@Body product: Product): Response<ApiResponse<Product>>
    
    @PUT("marketplace/products/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body product: Product): Response<ApiResponse<Product>>
    
    @PATCH("marketplace/products/{id}/availability")
    suspend fun setProductAvailability(
        @Path("id") id: Long,
        @Query("available") available: Boolean
    ): Response<ApiResponse<Unit>>
    
    // ========== MARKETPLACE - ORDERS ==========
    @POST("marketplace/orders")
    suspend fun placeOrder(@Body order: Order): Response<ApiResponse<Order>>
    
    @PATCH("marketplace/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Long,
        @Query("status") status: String
    ): Response<ApiResponse<Order>>
    
    @GET("marketplace/orders/buyer/{buyerId}")
    suspend fun getOrdersByBuyer(
        @Path("buyerId") buyerId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PageResponse<Order>>>
    
    @GET("marketplace/orders/seller/{sellerId}")
    suspend fun getOrdersBySeller(
        @Path("sellerId") sellerId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PageResponse<Order>>>
    
    // ========== COLLABORATION - POSTS ==========
    @GET("collaboration/posts/feed")
    suspend fun getFeed(
        @Header("X-User-Id") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<Post>>>
    
    @POST("collaboration/posts")
    suspend fun createPost(
        @Header("X-User-Id") userId: Long,
        @Body post: Post
    ): Response<ApiResponse<Post>>
    
    @GET("collaboration/posts/group/{groupId}")
    suspend fun getGroupPosts(
        @Header("X-User-Id") userId: Long,
        @Path("groupId") groupId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<Post>>>
    
    @POST("collaboration/posts/{postId}/like")
    suspend fun likePost(
        @Header("X-User-Id") userId: Long,
        @Path("postId") postId: Long
    ): Response<ApiResponse<Post>>
    
    @DELETE("collaboration/posts/{postId}/like")
    suspend fun unlikePost(
        @Header("X-User-Id") userId: Long,
        @Path("postId") postId: Long
    ): Response<ApiResponse<Post>>
    
    @POST("collaboration/posts/{postId}/comments")
    suspend fun addComment(
        @Header("X-User-Id") userId: Long,
        @Path("postId") postId: Long,
        @Body comment: PostComment
    ): Response<ApiResponse<PostComment>>
    
    @GET("collaboration/posts/{postId}/comments")
    suspend fun getPostComments(
        @Path("postId") postId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PageResponse<PostComment>>>
    
    @POST("collaboration/posts/{postId}/flag")
    suspend fun flagPost(
        @Header("X-User-Id") userId: Long,
        @Path("postId") postId: Long,
        @Query("reason") reason: String? = null
    ): Response<ApiResponse<Unit>>
    
    @GET("collaboration/posts/pending-moderation")
    suspend fun getPostsPendingModeration(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<Post>>>
    
    // ========== COLLABORATION - DIRECT MESSAGES ==========
    @POST("collaboration/direct-messages")
    suspend fun sendDirectMessage(
        @Header("X-User-Id") senderId: Long,
        @Body message: DirectMessage
    ): Response<ApiResponse<DirectMessage>>
    
    @GET("collaboration/direct-messages/conversation/{otherUserId}")
    suspend fun getConversation(
        @Header("X-User-Id") currentUserId: Long,
        @Path("otherUserId") otherUserId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<ApiResponse<PageResponse<DirectMessage>>>
    
    @GET("collaboration/direct-messages/conversations")
    suspend fun getRecentConversations(
        @Header("X-User-Id") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<DirectMessage>>>
    
    @POST("collaboration/direct-messages/read/{messageId}")
    suspend fun markMessageAsRead(
        @Header("X-User-Id") userId: Long,
        @Path("messageId") messageId: Long
    ): Response<ApiResponse<DirectMessage>>
    
    @GET("collaboration/direct-messages/conversation/{otherUserId}/after")
    suspend fun getMessagesAfter(
        @Header("X-User-Id") currentUserId: Long,
        @Path("otherUserId") otherUserId: Long,
        @Query("since") since: String
    ): Response<ApiResponse<List<DirectMessage>>>
    
    @GET("collaboration/direct-messages/partners")
    suspend fun getConversationPartners(@Header("X-User-Id") userId: Long): Response<ApiResponse<List<Long>>>
    
    // ========== COLLABORATION - GROUPS ==========
    @POST("collaboration/groups")
    suspend fun createGroup(
        @Header("X-User-Id") ownerId: Long,
        @Body group: Group
    ): Response<ApiResponse<Group>>
    
    @GET("collaboration/groups/{groupId}")
    suspend fun getGroup(
        @Header("X-User-Id") userId: Long,
        @Path("groupId") groupId: Long
    ): Response<ApiResponse<Group>>
    
    @DELETE("collaboration/groups/{groupId}")
    suspend fun deleteGroup(
        @Header("X-User-Id") requesterId: Long,
        @Path("groupId") groupId: Long
    ): Response<ApiResponse<String>>
    
    @GET("collaboration/groups/my-groups")
    suspend fun getMyGroups(@Header("X-User-Id") userId: Long): Response<ApiResponse<List<Group>>>
    
    @GET("collaboration/groups/browse")
    suspend fun browseGroups(
        @Header("X-User-Id") userId: Long,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<Group>>>
    
    @POST("collaboration/groups/{groupId}/join")
    suspend fun joinGroup(
        @Header("X-User-Id") userId: Long,
        @Path("groupId") groupId: Long
    ): Response<ApiResponse<GroupMembership>>
    
    @DELETE("collaboration/groups/{groupId}/leave")
    suspend fun leaveGroup(
        @Header("X-User-Id") userId: Long,
        @Path("groupId") groupId: Long
    ): Response<ApiResponse<String>>
    
    @GET("collaboration/groups/{groupId}/members")
    suspend fun getGroupMembers(
        @Header("X-User-Id") requesterId: Long,
        @Path("groupId") groupId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<GroupMembership>>>
    
    @POST("collaboration/groups/{groupId}/members")
    suspend fun addGroupMember(
        @Header("X-User-Id") inviterId: Long,
        @Path("groupId") groupId: Long,
        @Query("userId") userId: Long
    ): Response<ApiResponse<GroupMembership>>
    
    @DELETE("collaboration/groups/{groupId}/members/{userId}")
    suspend fun removeGroupMember(
        @Header("X-User-Id") removerId: Long,
        @Path("groupId") groupId: Long,
        @Path("userId") userId: Long
    ): Response<ApiResponse<GroupMembership>>
    
    @PUT("collaboration/groups/{groupId}/members/{userId}/role")
    suspend fun updateMemberRole(
        @Header("X-User-Id") updaterId: Long,
        @Path("groupId") groupId: Long,
        @Path("userId") userId: Long,
        @Query("role") role: String
    ): Response<ApiResponse<GroupMembership>>
    
    @POST("collaboration/groups/{groupId}/members/{userId}/suspend")
    suspend fun suspendMember(
        @Header("X-User-Id") suspenderId: Long,
        @Path("groupId") groupId: Long,
        @Path("userId") userId: Long
    ): Response<ApiResponse<GroupMembership>>
    
    // ========== COLLABORATION - GROUP MESSAGES ==========
    @POST("collaboration/messages")
    suspend fun sendGroupMessage(@Body message: Message): Response<ApiResponse<Message>>
    
    @GET("collaboration/messages")
    suspend fun getGroupMessages(
        @Query("groupId") groupId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<Message>>>
    
    // ========== CHATBOT / RAG SERVICE ==========
    @POST("query")
    suspend fun queryChatbot(@Body request: com.app.shamba_bora.data.model.ChatbotQueryRequest): Response<com.app.shamba_bora.data.model.ChatbotQueryResponse>
    
    @POST("conversations")
    suspend fun createConversation(@Body request: com.app.shamba_bora.data.model.CreateConversationRequest): Response<com.app.shamba_bora.data.model.ChatbotConversation>
    
    @GET("conversations/{conversation_id}")
    suspend fun getConversation(@Path("conversation_id") conversationId: String): Response<com.app.shamba_bora.data.model.ChatbotConversation>
    
    @GET("users/{user_id}/conversations")
    suspend fun getUserConversations(
        @Path("user_id") userId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<List<com.app.shamba_bora.data.model.ChatbotConversationSummary>>
    
    @PATCH("conversations/{conversation_id}")
    suspend fun updateConversation(
        @Path("conversation_id") conversationId: String,
        @Body request: com.app.shamba_bora.data.model.UpdateConversationRequest
    ): Response<com.app.shamba_bora.data.model.ChatbotConversation>
    
    @DELETE("conversations/{conversation_id}")
    suspend fun deleteConversation(
        @Path("conversation_id") conversationId: String,
        @Query("user_id") userId: String
    ): Response<Map<String, String>>
}

// ========== REQUEST/RESPONSE MODELS ==========
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String? = null,
    val role: String? = "FARMER"
)

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val username: String,
    val email: String,
    val roles: List<String>? = null,
    val userId: Long? = null,
    val message: String? = null
)

data class UpdateUserRequest(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null
)

// Generic API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val timestamp: String? = null
)

// Chatbot models
data class ChatbotQueryRequest(
    val question: String,
    val k: Int = 4
)

data class ChatbotQueryResponse(
    val answer: String,
    val contexts: List<ChatbotContext>? = null
)

data class ChatbotContext(
    val score: Double,
    val id: String,
    val text: String
)

// PageResponse is now defined in PageResponse.kt
