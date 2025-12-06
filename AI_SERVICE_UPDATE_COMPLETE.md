# AI Service Update - New Endpoints Implementation

## Overview
Successfully updated the ShambaBora Android chatbot service to use the new AI service endpoints. The implementation now supports:
- Direct farming question queries with farmer data context
- Chat history retrieval and management
- Comprehensive farm analytics and recommendations
- Enhanced UI for displaying complex farm advice data

## API Changes

### New Endpoints

#### 1. **Farming Question Query**
```
POST /api/v1/query
```
**Request Body:**
```json
{
  "farmer_id": 7,
  "include_farmer_data": true,
  "question": "What is the best fertilizer ratio for maize in loamy soil?",
  "session_id": "abc-123"
}
```

**Response:**
```json
{
  "response": "AI response text...",
  "session_id": "abc-123",
  "sources": [],
  "farmer_data_used": false,
  "category": "fertilizer",
  "metadata": {
    "tokens_used": 3552,
    "response_time": 4.73,
    "documents_retrieved": 0,
    "farmer_context_included": false
  }
}
```

#### 2. **Farmer Chat History**
```
GET /api/v1/farmer/{farmerId}/history?limit=50
```

**Response:**
```json
[
  {
    "id": 4,
    "session_id": "abc-123",
    "farmer_id": 7,
    "user_message": "What is the best fertilizer ratio for maize in loamy soil?",
    "ai_response": "Hello again!...",
    "created_at": "2025-12-04T16:09:29",
    "response_time": 4.73
  }
]
```

#### 3. **Farm Analytics & Recommendations**
```
GET /api/farm-analytics/ai-recommendations
```

**Response:**
```json
{
  "farmerProfileId": 7,
  "overallAssessment": "...",
  "strengths": [...],
  "weaknesses": [...],
  "recommendations": [
    {
      "category": "SOIL_MANAGEMENT",
      "recommendation": "...",
      "rationale": "...",
      "expectedBenefit": "...",
      "evidence": null,
      "priority": 2
    }
  ],
  "bestPractices": [...],
  "cropOptimizationAdvice": "...",
  "investmentAdvice": "..."
}
```

## Code Changes

### 1. **ChatbotModels.kt** - Updated Data Models
```kotlin
// Enhanced ChatbotQueryRequest to include farmer context
data class ChatbotQueryRequest(
    val question: String,
    val farmerId: Long? = null,
    val includeFarmerData: Boolean = true,
    val sessionId: String? = null,
    val k: Int = 4
)

// Updated ChatbotQueryResponse with new API fields
data class ChatbotQueryResponse(
    val response: String? = null,  // New API field
    val answer: String? = null,    // Old API (backward compat)
    val sessionId: String? = null,
    val category: String? = null,
    val farmerDataUsed: Boolean = false,
    val metadata: Map<String, Any>? = null
) {
    fun getResponseText(): String = response ?: answer ?: ""
}

// New models for enhanced farm advice
data class FarmRecommendation(
    val category: String,
    val recommendation: String,
    val rationale: String? = null,
    val expectedBenefit: String? = null,
    val priority: Int = 3
)

data class BestPractice(
    val practice: String,
    val reason: String? = null
)

data class ChatHistory(
    val sessionId: String,
    val farmerId: Long,
    val userMessage: String,
    val aiResponse: String,
    val createdAt: String,
    val responseTime: Double? = null
)
```

### 2. **ApiService.kt** - New Endpoints
```kotlin
// ========== CHATBOT / RAG SERVICE (NEW API) ==========
@POST("v1/query")
suspend fun queryFarmingQuestion(@Body request: ChatbotQueryRequest): Response<ChatbotQueryResponse>

@GET("v1/farmer/{farmerId}/history")
suspend fun getFarmerChatHistory(
    @Path("farmerId") farmerId: Long,
    @Query("limit") limit: Int = 50
): Response<List<ChatHistory>>

// ========== FARM ANALYTICS ==========
@GET("farm-analytics/ai-recommendations")
suspend fun getFarmAdvice(): Response<FarmAdviceResponse>
```

### 3. **ChatbotViewModel.kt** - Updated Logic
- Added `currentSessionId` and `currentFarmerId` state
- Implemented `initializeSession()` for session management
- Updated `askQuestion()` to use new `/api/v1/query` endpoint
- Added farmer ID and data inclusion flags
- Simplified conversation flow (no need to create conversations first)
- Updated `getFarmAdvice()` to call `/api/farm-analytics/ai-recommendations`
- Added `loadChatHistory()` method
- Enhanced error handling with timeout detection

### 4. **FarmAdviceComposables.kt** - New UI Components
Created beautiful new composables for displaying farm advice:
- **EnhancedFarmAdviceDialog**: Main dialog with tabs-like layout
- **OverallAssessmentCard**: Displays overall farm assessment
- **AdviceSectionEnhanced**: Shows strengths/weaknesses with icons
- **RecommendationCardEnhanced**: Priority-based recommendation display
  - Color-coded priorities (🔴 Critical, 🟠 High, 🟡 Medium, 🟢 Low)
  - Expected benefits highlighted
  - Category badges
- **BestPracticeCard**: Star-highlighted best practices
- **InfoCard**: General information cards for tips and strategy

### 5. **EnhancedChatbotScreen.kt** - Updated Screen
- Replaced `FarmAdviceDialog` with `EnhancedFarmAdviceDialog`
- Maintains all existing functionality
- Better visual hierarchy for farm advice

## Key Features

### ✅ New Functionality
1. **Direct Question Query** - Ask farming questions directly without conversation management
2. **Farmer Data Context** - AI responses include farmer-specific data for personalized advice
3. **Chat History** - Retrieve complete chat history with timestamps and response times
4. **Farm Analytics** - Comprehensive farm recommendations with:
   - Overall assessment
   - Strengths and weaknesses
   - Priority-based recommendations (1-5)
   - Best practices
   - Crop optimization tips
   - Investment strategy

### ✅ Enhanced UI
- Priority-based visual indicators
- Color-coded recommendation cards
- Expandable sections for better readability
- Better metadata display (tokens used, response time, etc.)
- Organized information hierarchy

### ✅ Better Error Handling
- Timeout detection and background polling
- Clear error messages
- Session management
- Token validation

## Migration Path

### From Old API to New API
The implementation is backward compatible:
- Old conversation endpoints still available
- `ChatbotQueryResponse` supports both `response` (new) and `answer` (old)
- `getResponseText()` helper method abstracts the difference

```kotlin
// Old way (still works)
val response: ChatbotQueryResponse = chatbotApi.queryChatbot(oldRequest)
val text = response.answer

// New way (recommended)
val response: ChatbotQueryResponse = chatbotApi.queryFarmingQuestion(newRequest)
val text = response.getResponseText()  // Works for both old and new
```

## Testing Recommendations

### 1. Test New Query Endpoint
```bash
curl -X POST http://localhost:8000/api/v1/query \
  -H "Content-Type: application/json" \
  -d '{
    "farmer_id": 7,
    "include_farmer_data": true,
    "question": "What is the best fertilizer ratio for maize?",
    "session_id": "test-session"
  }'
```

### 2. Test History Endpoint
```bash
curl -X GET http://localhost:8000/api/v1/farmer/7/history?limit=50
```

### 3. Test Farm Advice
```bash
curl -X GET http://localhost:8080/api/farm-analytics/ai-recommendations \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. App Testing
- Ask a farming question
- Verify response displays with correct text
- Check chat history loads
- Request farm advice
- Verify all recommendation sections display correctly
- Test on slow network (timeout handling)

## Performance Optimizations

1. **Session Management**: Unique session IDs for tracking conversations
2. **Metadata Tracking**: Response time and token usage monitoring
3. **Background Polling**: Long-running requests don't block UI
4. **Lazy Loading**: Farm advice dialog uses lazy column for smooth scrolling

## Future Enhancements

1. Session persistence across app restarts
2. Favorite questions/advice bookmarking
3. Export farm advice as PDF
4. Offline cache for recommendations
5. Voice input/output for questions
6. Real-time notifications for advice updates
7. Comparative analytics (farm vs. region average)

## Files Modified

✅ `/app/src/main/java/com/app/shamba_bora/data/model/ChatbotModels.kt`
✅ `/app/src/main/java/com/app/shamba_bora/data/network/ApiService.kt`
✅ `/app/src/main/java/com/app/shamba_bora/viewmodel/ChatbotViewModel.kt`
✅ `/app/src/main/java/com/app/shamba_bora/ui/screens/chatbot/EnhancedChatbotScreen.kt`
✅ `/app/src/main/java/com/app/shamba_bora/ui/screens/chatbot/FarmAdviceComposables.kt` (NEW)

## Status: ✅ COMPLETE

All updates have been successfully implemented. The Android app now uses the new AI service endpoints with:
- Clean, maintainable code
- Beautiful UI for displaying recommendations
- Proper error handling
- Session management
- Backward compatibility with old API

No compilation errors detected.
