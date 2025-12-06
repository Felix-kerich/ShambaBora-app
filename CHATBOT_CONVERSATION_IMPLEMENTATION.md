# Chatbot Conversation System Implementation

## Overview

The AI Chatbot has been enhanced with a complete **conversation management system** that enables users to:

✅ **Create New Conversations** - Start fresh conversations with custom titles  
✅ **View Conversation History** - Access all previous conversations in a sidebar  
✅ **Load Saved Conversations** - Click to view and continue previous conversations  
✅ **Persistent Messages** - All messages are saved to the backend  
✅ **Conversation Switching** - Seamlessly switch between multiple conversations  
✅ **Organized Display** - Conversations shown with titles, message counts, and timestamps  

---

## Architecture Overview

### Components Updated

#### 1. **ChatbotViewModel** (`viewmodel/ChatbotViewModel.kt`)
The ViewModel now provides:

**Conversation Management Methods:**
- `loadConversations()` - Load all conversations for the current farmer
- `createNewConversation(title)` - Create a new conversation
- `loadConversation(conversationId)` - Load a specific conversation with all messages
- `startNewConversation()` - Start a fresh conversation (clears current state)

**Messaging Methods:**
- `askQuestion(question)` - Ask a question (auto-creates conversation if needed)

**State Flows:**
- `conversations` - StateFlow<Resource<List<ChatbotConversationSummary>>> - All conversations
- `currentConversationId` - StateFlow<String?> - Current conversation ID
- `currentConversation` - StateFlow<Resource<ChatbotConversation>?> - Full conversation with messages
- `isLoading` - StateFlow<Boolean> - Loading state for UI

**Key Features:**
- Automatically creates a conversation if none exists
- Reloads conversation after each message
- Handles errors gracefully
- Supports background polling for long-running requests

#### 2. **ApiService** (`data/network/ApiService.kt`)
New API endpoints added:

```kotlin
// Get all conversations for a farmer
@GET("api/v1/farmer/{farmerId}/conversations")
suspend fun getFarmerConversations(
    @Path("farmerId") farmerId: Long,
    @Query("limit") limit: Int = 50,
    @Query("offset") offset: Int = 0
): Response<List<ChatbotConversationSummary>>

// Create a new conversation
@POST("api/v1/conversations/create")
suspend fun createConversation(
    @Body request: CreateConversationRequest
): Response<ChatbotConversation>

// Get a specific conversation with all messages
@GET("api/v1/conversations/{conversationId}")
suspend fun getConversation(
    @Path("conversationId") conversationId: String
): Response<ChatbotConversation>

// Ask a farming question (with conversation support)
@POST("api/v1/query")
suspend fun queryFarmingQuestion(
    @Body request: ChatbotQueryRequest
): Response<ChatbotQueryResponse>
```

#### 3. **EnhancedChatbotScreen** (`ui/screens/chatbot/EnhancedChatbotScreen.kt`)
UI enhancements:

**Header Updates:**
- "New Chat" button now calls `viewModel.startNewConversation()`
- Properly displays current conversation title

**Sidebar (ConversationHistorySidebar):**
- Shows list of all conversations
- "New Conversation" button to start fresh conversation
- Click to load and view previous conversations
- Shows message count and last message timestamp
- Delete button for each conversation
- Refresh button to reload conversation list

**Chat Flow:**
- Messages are sent with `viewModel.askQuestion(message)`
- Conversation auto-creates if needed
- Messages persist to backend
- Clicking sidebar conversation loads it automatically

---

## How It Works

### Scenario 1: Starting with No Conversation

```
1. User opens the app → ChatbotViewModel.init() calls loadConversations()
2. Conversation list loads from backend
3. User types a message
4. askQuestion() called
5. If no current conversation:
   - Creates new conversation via createConversation()
   - Sets currentConversationId
   - Stores ChatbotConversation in currentConversation state
6. Sends question to backend with conversation ID
7. Message persisted
8. Conversation reloaded with new message
```

### Scenario 2: Continuing Existing Conversation

```
1. User clicks conversation in sidebar
2. loadConversation(conversationId) called
3. Conversation with all messages loads
4. User can see full history
5. User types new message
6. askQuestion() sent with current conversationId
7. Message added to conversation
8. Updated conversation displayed
```

### Scenario 3: Starting New Conversation

```
1. User clicks "New Chat" or "New Conversation"
2. startNewConversation() called:
   - Clears currentConversationId
   - Clears currentConversation
   - Calls createNewConversation()
3. New conversation created
4. Empty chat view shown (WelcomeScreen)
5. User can start typing
6. First message triggers conversation creation flow
```

---

## Data Flow Diagram

```
┌─────────────────────────────────────────────────────┐
│                  ChatbotScreen                       │
├─────────────────────────────────────────────────────┤
│                                                       │
│  ┌──────────────────────┐    ┌─────────────────┐   │
│  │  ConversationSidebar │    │   ChatArea      │   │
│  ├──────────────────────┤    ├─────────────────┤   │
│  │ - List Conversations │    │ - Message List  │   │
│  │ - New Chat Button    │    │ - Input Field   │   │
│  │ - Delete Conversation│    │ - Send Button   │   │
│  └──────────────────────┘    └─────────────────┘   │
│         │                            │              │
│         └────────┬───────────────────┘              │
│                  │                                   │
│              ViewBinding                            │
│                  │                                   │
└──────────────────┼───────────────────────────────────┘
                   │
         ┌─────────▼──────────┐
         │  ChatbotViewModel  │
         ├────────────────────┤
         │ - conversations    │
         │ - currentConv      │
         │ - currentConvId    │
         └─────────┬──────────┘
                   │
      ┌────────────┼────────────┐
      │            │            │
      ▼            ▼            ▼
┌─────────────────────────────────────────┐
│         ChatbotApi (Retrofit)           │
├─────────────────────────────────────────┤
│ - getFarmerConversations()              │
│ - createConversation()                  │
│ - getConversation()                     │
│ - queryFarmingQuestion()                │
│ - getFarmAdvice()                       │
└──────────────┬──────────────────────────┘
               │
               ▼
    ┌──────────────────────┐
    │  Backend Server      │
    ├──────────────────────┤
    │ api/v1/query         │
    │ api/v1/conversations │
    │ api/v1/farmer/:id    │
    └──────────────────────┘
```

---

## Key State Management

### ViewModel State Flows

```kotlin
// All conversations for current farmer
val conversations: StateFlow<Resource<List<ChatbotConversationSummary>>>

// Current conversation ID being viewed
val currentConversationId: StateFlow<String?>

// Full current conversation with all messages
val currentConversation: StateFlow<Resource<ChatbotConversation>?>

// Query response (for error handling)
val queryResponse: StateFlow<Resource<ChatbotQueryResponse>?>

// Pending user message (shows before server response)
val pendingMessage: StateFlow<String?>

// Loading state
val isLoading: StateFlow<Boolean>

// Farm advice state
val farmAdvice: StateFlow<Resource<FarmAdviceResponse>?>
```

---

## User Journey

### 1. **First Time User**
```
Open App
  ↓
loadConversations() - loads empty list
  ↓
WelcomeScreen shown with suggested questions
  ↓
User types/clicks question
  ↓
askQuestion() called
  ↓
createNewConversation() - conversation created on backend
  ↓
Query sent with conversation ID
  ↓
Response displayed
  ↓
Conversation added to sidebar list
```

### 2. **Returning User**
```
Open App
  ↓
loadConversations() - loads list with previous conversations
  ↓
User clicks conversation in sidebar
  ↓
loadConversation(id)
  ↓
Full conversation with message history displayed
  ↓
User can continue conversation or start new one
```

### 3. **Switching Conversations**
```
User viewing Conversation A
  ↓
Clicks Conversation B in sidebar
  ↓
loadConversation(B) called
  ↓
Loading indicator shown
  ↓
Conversation B with all messages displayed
  ↓
User can continue in Conversation B
```

---

## Integration with Existing Features

### Farm Advice Integration
- **Get Advice Button** - Still available in top bar
- **Loads independently** - Doesn't affect current conversation
- **Shows same dialogs** - Loading, success, error states

### Chat History
- **Stored persistently** - Each conversation maintains full message history
- **Searchable** - Can search conversations by title (future enhancement)
- **Archivable** - Can archive/delete old conversations (future enhancement)

### Authentication
- **Uses existing tokens** - AuthInterceptor includes JWT token
- **Farmer-specific** - Each farmer sees only their conversations
- **Session-aware** - Maintains session ID for tracking

---

## API Integration Summary

### 1. Load Conversations
```kotlin
viewModel.loadConversations()
// Calls: GET /api/v1/farmer/{farmerId}/conversations?limit=50&offset=0
// Returns: List<ChatbotConversationSummary>
```

### 2. Create Conversation
```kotlin
viewModel.createNewConversation(title: String?)
// Calls: POST /api/v1/conversations/create
// Body: CreateConversationRequest(userId, title)
// Returns: ChatbotConversation
```

### 3. Load Conversation
```kotlin
viewModel.loadConversation(conversationId: String)
// Calls: GET /api/v1/conversations/{conversationId}
// Returns: ChatbotConversation with messages
```

### 4. Ask Question
```kotlin
viewModel.askQuestion(question: String)
// Calls: POST /api/v1/query
// Body: ChatbotQueryRequest(question, farmerId, conversationId, etc.)
// Returns: ChatbotQueryResponse
// Then: Reloads conversation to show new message
```

---

## Error Handling

### Conversation Loading Errors
```kotlin
when (val conv = currentConversation) {
    is Resource.Error -> {
        ErrorMessage(
            message = conv.message ?: "Failed to load conversation",
            onRetry = {
                currentConversationId?.let { viewModel.loadConversation(it) }
            }
        )
    }
}
```

### Query Errors
```kotlin
when (queryResponse) {
    is Resource.Error -> {
        // Show error in message
        // Provide retry button
        // Don't clear pending message
    }
}
```

### Network Timeouts
```kotlin
// Caught as IOException with "timeout" in message
// Shows message: "Connection timeout. Processing in background..."
// Starts background polling (for long-running operations)
```

---

## UI Components

### 1. **ConversationHistorySidebar**
- Shows all conversations in a list
- "New Conversation" button at top
- Refresh button to reload
- Current conversation highlighted
- Delete button for each conversation

### 2. **ConversationItem**
- Shows conversation title
- Shows message count
- Shows last message timestamp
- Shows loading state when selected
- Delete option

### 3. **ChatHeader**
- Displays current conversation title
- Menu button to toggle sidebar
- New Chat button
- Get Advice button

### 4. **ChatInput**
- Text field for message
- Send button (active only when text)
- Character count limit
- Multi-line support

### 5. **MessageList**
- Displays messages in order
- User messages right-aligned
- AI messages left-aligned
- Pending messages show with loading indicator
- Scrolls automatically to latest message

---

## Testing the Implementation

### Test 1: Create Conversation
```kotlin
// Open app
// Type a question
// Expected: New conversation created, message sent, response shown
```

### Test 2: View Conversation List
```kotlin
// Open sidebar (click menu icon)
// Expected: List of all conversations shown
```

### Test 3: Load Previous Conversation
```kotlin
// Click a conversation in sidebar
// Expected: Conversation loads with full message history
```

### Test 4: Switch Between Conversations
```kotlin
// Click Conversation A
// Then click Conversation B
// Expected: Sidebar closes, Conversation B shows, messages update
```

### Test 5: Continue Conversation
```kotlin
// Load previous conversation
// Type new question
// Expected: New message added to conversation
```

### Test 6: Start New Conversation
```kotlin
// Click "New Chat" in header
// Expected: Empty chat, welcome screen shown
// Type question
// Expected: New conversation created (not added to existing one)
```

---

## Known Limitations & Future Enhancements

### Current Limitations
- Conversation titles are auto-generated by timestamp if not provided
- No search functionality in conversation list
- No conversation archiving
- No bulk delete
- No sharing conversations

### Future Enhancements
1. **Smart Titles** - Auto-generate title from first message
2. **Search** - Search conversations by title and content
3. **Archive** - Archive old conversations
4. **Export** - Export conversations to PDF
5. **Share** - Share conversations with advisors
6. **Collaboration** - Multiple users in same conversation
7. **Tags** - Tag conversations by topic
8. **Pinned** - Pin important conversations
9. **Analytics** - Track conversation patterns
10. **Offline Mode** - Store conversations locally

---

## Files Modified

1. **viewmodel/ChatbotViewModel.kt**
   - Added conversation management methods
   - Added currentConversationId state
   - Updated askQuestion() to auto-create conversations
   - Added startNewConversation()
   - Improved error handling and logging

2. **data/network/ApiService.kt**
   - Added getFarmerConversations() endpoint
   - Added new conversation endpoints with correct paths
   - Maintained backward compatibility

3. **ui/screens/chatbot/EnhancedChatbotScreen.kt**
   - Updated onNewChat to call startNewConversation()
   - Updated onNewConversation handler
   - Updated message sending to use new askQuestion()
   - Improved state management

4. **data/model/ChatbotModels.kt**
   - Already had all necessary data classes

---

## Debugging

### Enable Logging
The ViewModel logs all important operations:
```
ChatbotViewModel: "Loaded ${convList.size} conversations"
ChatbotViewModel: "Created conversation: ${conversationId}"
ChatbotViewModel: "Loaded conversation with ${messages.size} messages"
ChatbotViewModel: "Question answered, conversation: $conversationId"
```

### Common Issues & Solutions

**Issue: Conversations not loading**
- Solution: Check network connectivity, verify token is valid

**Issue: New message not appearing**
- Solution: Conversation reload should be automatic, check logs for errors

**Issue: Sidebar showing old data**
- Solution: Click refresh button in sidebar header

**Issue: Conversation creating but messages not saving**
- Solution: Check conversationId is returned from create endpoint

---

## Summary

The AI Chatbot now features a **professional conversation management system** that allows users to:

1. ✅ Create multiple conversations
2. ✅ View conversation history
3. ✅ Load and continue previous conversations
4. ✅ Switch between conversations seamlessly
5. ✅ Persistent message storage
6. ✅ Organized UI with sidebar navigation

The implementation follows the documented API specifications and integrates seamlessly with the existing authentication, state management, and UI components.

Users can now have meaningful multi-turn conversations with proper history tracking and management!
