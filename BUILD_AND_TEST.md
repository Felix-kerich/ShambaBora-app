# Build & Rebuild Instructions

## Issue: Changes Not Reflected in Running App

When you make source code changes in Android development, the app must be **recompiled and reinstalled** to see the changes.

### Why the Error Persists

1. **Source code was modified** ✅
   - `ApiService.kt`: Changed endpoint from `/api/v1/conversations/create` to `/api/v1/conversations`
   - `ChatbotViewModel.kt`: Added better error logging

2. **App still running with old compiled code** ❌
   - The app in the emulator is running the previous build
   - Changes are in source files but not in the running binary

## How to Rebuild and Test

### Option 1: Full Rebuild (Recommended)

```bash
# 1. Clean the build
./gradlew clean

# 2. Rebuild the app
./gradlew build

# 3. Run on emulator
./gradlew installDebug

# Or in Android Studio:
# - Build menu → Clean Project
# - Build menu → Rebuild Project
# - Run → Run 'app'
```

### Option 2: Quick Rebuild (If Already Connected to Emulator)

```bash
./gradlew installDebug
```

### Option 3: Using Android Studio UI

1. **Close the running app** on emulator
2. **Build → Clean Project**
3. **Build → Rebuild Project**
4. **Run → Run 'app'** (or press Shift+F10)

## What to Watch For After Rebuild

### ✅ Expected Success Logs

```
2025-12-06 12:25:30.000  xxxx-xxxx  ChatbotViewModel  com.app.shamba_bora  D  askQuestion() - Current conversation ID: null
2025-12-06 12:25:30.001  xxxx-xxxx  ChatbotViewModel  com.app.shamba_bora  D  No active conversation - creating new one
2025-12-06 12:25:30.100  xxxx-xxxx  okhttp.OkHttpClient  com.app.shamba_bora  I  --> POST http://10.0.2.2:8000/api/v1/conversations
2025-12-06 12:25:30.150  xxxx-xxxx  okhttp.OkHttpClient  com.app.shamba_bora  I  <-- 200 OK http://10.0.2.2:8000/api/v1/conversations
2025-12-06 12:25:30.200  xxxx-xxxx  ChatbotViewModel  com.app.shamba_bora  D  New conversation created: conv_abc123xyz
2025-12-06 12:25:30.300  xxxx-xxxx  okhttp.OkHttpClient  com.app.shamba_bora  I  --> POST http://10.0.2.2:8000/api/v1/query
2025-12-06 12:25:32.500  xxxx-xxxx  okhttp.OkHttpClient  com.app.shamba_bora  I  <-- 200 OK http://10.0.2.2:8000/api/v1/query
2025-12-06 12:25:32.600  xxxx-xxxx  ChatbotViewModel  com.app.shamba_bora  D  Question answered, conversation: conv_abc123xyz
```

### ❌ If Still Getting 404

If you still see:
```
<-- 404 Not Found http://10.0.2.2:8000/api/v1/conversations/create
```

This means the rebuild didn't work. Check:
- Did you run `./gradlew clean`?
- Did you run `./gradlew installDebug`?
- Did you close and reopen the app on the emulator?
- Check Android Studio's Build Console for errors

### ❌ If Getting Different Error

If you see a different error in the logs like:
```
E  Create conversation failed: 400 - Bad Request
E  Error body: {"detail":"..."}
```

Check the error body message to see what the backend expects.

## Changes Made (Source Code)

### 1. ApiService.kt (Line 467)

**Before:**
```kotlin
@POST("api/v1/conversations/create")
suspend fun createConversation(...): Response<ChatbotConversation>
```

**After:**
```kotlin
@POST("api/v1/conversations")
suspend fun createConversation(...): Response<ChatbotConversation>
```

### 2. ChatbotViewModel.kt (Lines 262-268)

**Before:**
```kotlin
} else {
    throw Exception("Failed to create conversation")
}
```

**After:**
```kotlin
} else {
    val errorBody = createResponse.errorBody()?.string() ?: "Empty response"
    Log.e("ChatbotViewModel", "Create conversation failed: ${createResponse.code()} - ${createResponse.message()}")
    Log.e("ChatbotViewModel", "Error body: $errorBody")
    throw Exception("Failed to create conversation: ${createResponse.code()} - ${createResponse.message()}")
}
```

## Debugging Checklist

After rebuild, if the conversation endpoint still fails:

- [ ] Backend is running on `http://10.0.2.2:8000`
- [ ] Endpoint `/api/v1/conversations` exists on backend (not `/api/v1/conversations/create`)
- [ ] Request body matches backend expectations: `{"user_id": "7", "title": "..."}`
- [ ] Response returns valid `ChatbotConversation` object with `conversation_id` field
- [ ] App has been uninstalled and reinstalled (not just rebuilt)

## Test Curl Command

To verify the backend endpoint works before testing in the app:

```bash
curl -X POST http://localhost:8000/api/v1/conversations \
  -H "Content-Type: application/json" \
  -d '{"user_id":"7","title":"Test Chat"}'
```

Expected response (200 Created):
```json
{
  "conversation_id": "conv_abc123xyz",
  "user_id": "7",
  "title": "Test Chat",
  "messages": [],
  "created_at": "2025-12-06T12:30:00",
  "updated_at": "2025-12-06T12:30:00"
}
```

## Summary

The source code changes are ready but need to be **compiled and installed** into the running app:

1. Run: `./gradlew clean installDebug`
2. Close and reopen app on emulator
3. Check logs for successful endpoint calls to `/api/v1/conversations`
4. Test by sending a message in the chatbot
