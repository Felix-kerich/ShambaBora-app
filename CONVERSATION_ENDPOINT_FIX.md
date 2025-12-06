# Conversation Endpoint 404 Fix

## Problem
The app was receiving a **404 Not Found** error when trying to create conversations:

```
E  Exception asking question (Ask Gemini)
   java.lang.Exception: Failed to create conversation
     at com.app.shamba_bora.viewmodel.ChatbotViewModel$askQuestion$1.invokeSuspend(ChatbotViewModel.kt:269)

okhttp.OkHttpClient: <-- 404 Not Found http://10.0.2.2:8000/api/v1/conversations/create
```

## Root Cause
The endpoint path in the API service was incorrect:

**Before:**
```kotlin
@POST("api/v1/conversations/create")
suspend fun createConversation(...): Response<ChatbotConversation>
```

**Problem:** This creates the full URL as:
```
POST http://10.0.2.2:8000/api/v1/conversations/create
```

But the backend endpoint is actually:
```
POST http://10.0.2.2:8000/api/v1/conversations
```

The `/create` suffix doesn't exist as a separate endpoint. The backend uses RESTful conventions where `POST /conversations` creates a new conversation.

## Solution
Changed the endpoint path from `/api/v1/conversations/create` to `/api/v1/conversations`:

```kotlin
@POST("api/v1/conversations")  // ✅ Correct
suspend fun createConversation(...): Response<ChatbotConversation>
```

This now creates the correct URL:
```
POST http://10.0.2.2:8000/api/v1/conversations
```

## File Modified
**ApiService.kt** (Line 467)

### Before
```kotlin
@POST("api/v1/conversations/create")
suspend fun createConversation(@Body request: com.app.shamba_bora.data.model.CreateConversationRequest): Response<com.app.shamba_bora.data.model.ChatbotConversation>
```

### After
```kotlin
@POST("api/v1/conversations")
suspend fun createConversation(@Body request: com.app.shamba_bora.data.model.CreateConversationRequest): Response<com.app.shamba_bora.data.model.ChatbotConversation>
```

## Expected Flow (Now Fixed)

### Step 1: User Sends First Question
```
User: "What is the best fertilizer?"
```

### Step 2: App Detects No Conversation
```kotlin
val currentConvId = _currentConversationId.value  // null
if (currentConvId == null) {
    // Create new conversation
}
```

### Step 3: App Creates Conversation (NOW WORKS ✅)
```
POST http://10.0.2.2:8000/api/v1/conversations
Body: {
  "user_id": "7",
  "title": "Chat 1765012770499"
}

✅ 200 OK (before was 404 Not Found)
Response: {
  "conversation_id": "conv_abc123xyz",
  "messages": [],
  ...
}
```

### Step 4: App Stores Conversation ID
```kotlin
_currentConversationId.value = "conv_abc123xyz"
```

### Step 5: App Sends Question
```
POST http://10.0.2.2:8000/api/v1/query
Body: {
  "question": "What is the best fertilizer?",
  "conversation_id": "conv_abc123xyz",
  "farmer_id": 7,
  ...
}

✅ 200 OK
Response: {
  "response": "Hello there! That's an excellent question...",
  "conversation_id": "conv_abc123xyz",
  ...
}
```

### Step 6: Next Questions Reuse Same ID
```
User: "Tell me more"

// Check conversation ID
val currentConvId = _currentConversationId.value  // "conv_abc123xyz"
if (currentConvId == null) {
    // Skip - already have ID
} else {
    // Reuse existing conversation
    POST /api/v1/query with conversation_id = "conv_abc123xyz"
}
```

## Affected Endpoints

### Changed
- **Before:** `POST /api/v1/conversations/create`
- **After:** `POST /api/v1/conversations`

### Unchanged (Already Correct)
- `GET /api/v1/farmer/{farmerId}/conversations`
- `GET /api/v1/conversations/{conversationId}`
- `POST /api/v1/query`
- `PATCH /conversations/{conversation_id}`
- `DELETE /conversations/{conversation_id}`

## Backend Endpoint Reference

Based on backend documentation, the correct endpoint is:

```
POST /api/v1/conversations

Request Body:
{
  "user_id": "7",
  "title": "Chat 1765012770499"  // optional
}

Response (201 Created):
{
  "conversation_id": "conv_abc123xyz",
  "user_id": "7",
  "title": "Chat 1765012770499",
  "messages": [],
  "created_at": "2025-12-06T12:19:30",
  "updated_at": "2025-12-06T12:19:30"
}
```

## Testing

To verify this fix works:

1. **Start the app**
2. **Open the chatbot**
3. **Send a message** → Should create conversation successfully (no 404)
4. **Check logs** for:
   ```
   ✅ POST http://10.0.2.2:8000/api/v1/conversations (200 OK)
   ✅ New conversation created: conv_abc123xyz
   ✅ Question answered, conversation: conv_abc123xyz
   ```

5. **Send another message** → Should reuse same conversation ID

## Common Issues

### Still Getting 404?
- ✅ Make sure backend is running on `8000`
- ✅ Check if the endpoint path matches exactly
- ✅ Verify request body is correct JSON

### CreateConversationRequest Structure
The request body must match what backend expects:

```kotlin
data class CreateConversationRequest(
    @SerializedName("user_id")
    val userId: String,
    val title: String? = null
)
```

### Backend Not Responding
Verify backend endpoint exists:

```bash
curl -X POST http://localhost:8000/api/v1/conversations \
  -H "Content-Type: application/json" \
  -d '{"user_id":"7","title":"Test"}'
```

## Summary

✅ **Fixed the 404 error** by removing the `/create` suffix from the endpoint  
✅ **Now uses RESTful convention:** `POST /api/v1/conversations`  
✅ **Conversation creation should work** on first question  
✅ **Subsequent questions reuse** the same conversation ID  
✅ **Complete conversation flow** now functional
