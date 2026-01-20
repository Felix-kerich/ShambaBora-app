# JSON Response Parsing Fix - Wrapped Response Issue

## Problem Identified

The API returns a **wrapped response object**, not a plain array:

```json
{
  "status": "success",
  "data": [
    {...},
    {...}
  ]
}
```

But the app was expecting a plain array `[...]`, causing a JSON parsing error:

```
java.lang.IllegalStateException: Expected BEGIN_ARRAY but was BEGIN_OBJECT at line 1 column 2 path $
```

## Root Cause

The endpoint `getFarmerConversations` was defined to return `Response<List<ChatbotConversationSummary>>` but the backend actually returns the list wrapped in an object with `status` and `data` fields.

## Solution

### 1. Created Response Wrapper Class (ChatbotModels.kt)

Added a new data class to handle the wrapped response:

```kotlin
/**
 * Wrapper response for farmer conversations list
 */
data class FarmerConversationsResponse(
    val status: String,
    val data: List<ChatbotConversationSummary>? = null,
    val conversations: List<ChatbotConversationSummary>? = null
) {
    fun getConversations(): List<ChatbotConversationSummary> {
        return data ?: conversations ?: emptyList()
    }
}
```

### 2. Updated Endpoint Path (ApiService.kt)

**Before:**
```kotlin
@GET("api/v1/conversations/conversations/farmer/{farmerId}/conversations")
suspend fun getFarmerConversations(
    ...
): Response<List<ChatbotConversationSummary>>
```

**After:**
```kotlin
@GET("api/v1/farmer/{farmerId}/conversations")
suspend fun getFarmerConversations(
    ...
): Response<FarmerConversationsResponse>
```

Also updated `getConversation` endpoint:
- Before: `api/v1/conversations/conversations/{conversationId}`
- After: `api/v1/conversations/{conversationId}`

### 3. Updated ViewModel to Unwrap Response (ChatbotViewModel.kt)

**Before:**
```kotlin
val convList = response.body()!!
_conversations.value = Resource.Success(convList)
Log.d("ChatbotViewModel", "Loaded ${convList.size} conversations")
```

**After:**
```kotlin
val wrapper = response.body()!!
val convList = wrapper.getConversations()
_conversations.value = Resource.Success(convList)
Log.d("ChatbotViewModel", "Loaded ${convList.size} conversations")
```

## Files Modified

1. **app/src/main/java/com/app/shamba_bora/data/model/ChatbotModels.kt**
   - Added `FarmerConversationsResponse` wrapper class

2. **app/src/main/java/com/app/shamba_bora/data/network/ApiService.kt**
   - Line 460: Changed endpoint path to `api/v1/farmer/{farmerId}/conversations`
   - Line 460: Changed return type to `FarmerConversationsResponse`
   - Line 468: Updated `getConversation` endpoint to `api/v1/conversations/{conversationId}`

3. **app/src/main/java/com/app/shamba_bora/viewmodel/ChatbotViewModel.kt**
   - Lines 138-155: Updated `loadConversations()` to unwrap response

## Expected API Response Format

The backend now correctly returns responses in this format:

### List Conversations
```json
{
  "status": "success",
  "data": [
    {
      "conversation_id": "conv_abc123",
      "user_id": "7",
      "title": "Chat Title",
      "message_count": 5,
      "created_at": "2025-12-06T10:00:00",
      "updated_at": "2025-12-06T10:00:00",
      "last_message": "Last message text"
    }
  ]
}
```

### Create Conversation
```json
{
  "status": "success",
  "conversation_id": "conv_c429ebe97135",
  "message": "Conversation created successfully"
}
```

### Get Conversation
```json
{
  "conversation_id": "conv_abc123",
  "user_id": "7",
  "title": "Chat Title",
  "messages": [...],
  "created_at": "2025-12-06T10:00:00",
  "updated_at": "2025-12-06T10:00:00"
}
```

## Data Flow (Now Fixed)

```
┌─────────────────────────────────────────┐
│ App Launches / Open Chatbot             │
├─────────────────────────────────────────┤
│ Call: getFarmerConversations(farmerId)  │
└────────────┬────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────┐
│ Backend Returns Wrapped Response        │
├─────────────────────────────────────────┤
│ {                                        │
│   "status": "success",                   │
│   "data": [                              │
│     {...conversation1...},               │
│     {...conversation2...}                │
│   ]                                      │
│ }                                        │
└────────────┬────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────┐
│ Unwrap Response                         │
├─────────────────────────────────────────┤
│ val wrapper = response.body()!!          │
│ val convList = wrapper.getConversations()
│                                          │
│ ✅ Now it's a List<...> not Object      │
└────────────┬────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────┐
│ Update UI with Conversations            │
├─────────────────────────────────────────┤
│ _conversations.value =                   │
│   Resource.Success(convList)             │
│                                          │
│ ✅ Display conversations in sidebar      │
└─────────────────────────────────────────┘
```

## Testing

### 1. Clean and Rebuild
```bash
cd /home/kerich/AndroidStudioProjects/Shamba_Bora
./gradlew clean build
./gradlew installDebug
```

### 2. Reopen App
- Close the app on emulator
- Reopen it

### 3. Expected Logs (Success)
```
D  ChatbotViewModel: Loaded 2 conversations
I  okhttp.OkHttpClient: <-- 200 OK http://10.0.2.2:8000/api/v1/farmer/7/conversations
I  okhttp.OkHttpClient: {"status":"success","data":[...]}
```

### 4. No More JSON Parsing Errors
✅ Error should be gone:
```
❌ Before: java.lang.IllegalStateException: Expected BEGIN_ARRAY but was BEGIN_OBJECT
✅ After: Conversations load successfully
```

## API Endpoint Reference

| Endpoint | Method | Path | Purpose |
|----------|--------|------|---------|
| **List Conversations** | GET | `/api/v1/farmer/{farmerId}/conversations` | Get all conversations for a farmer |
| **Create Conversation** | POST | `/api/v1/conversations/conversations/create` | Create a new conversation |
| **Get Conversation** | GET | `/api/v1/conversations/{conversationId}` | Get specific conversation with messages |
| **Ask Question** | POST | `/api/v1/query` | Send a question to AI |

## Error Handling

The wrapper class handles both possible response formats:

```kotlin
data class FarmerConversationsResponse(
    val status: String,
    val data: List<ChatbotConversationSummary>? = null,
    val conversations: List<ChatbotConversationSummary>? = null
) {
    fun getConversations(): List<ChatbotConversationSummary> {
        return data ?: conversations ?: emptyList()
    }
}
```

- If backend returns `data` field → uses that
- If backend returns `conversations` field → uses that
- Otherwise → returns empty list

## Status

✅ **Code Changes Complete**
- JSON parsing issue fixed
- Response wrapper added
- Endpoints updated
- ViewModel updated to unwrap response
- All code compiles without errors

📋 **Next Steps**
1. Run: `./gradlew clean installDebug`
2. Reopen app
3. Check that conversations load without JSON parsing errors
4. Verify conversation list displays in sidebar
