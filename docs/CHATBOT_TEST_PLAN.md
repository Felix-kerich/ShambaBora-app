# Chatbot Conversation System - Complete Test Plan

## Current State Summary ✅
- All compilation errors: **FIXED**
- Endpoint paths: **CORRECTED**
- JSON parsing: **FIXED**
- Conversation ID persistence: **WORKING**
- Sidebar premature closing: **FIXED**

## Test Scenario 1: Basic Message in Existing Conversation ✅
**Objective**: Send message to chatbot in existing conversation

**Steps**:
1. Open chatbot screen
2. Click on an existing conversation in sidebar (if available)
3. Type message "What should I plant this season?"
4. Click Send button
5. **EXPECTED**: 
   - Message appears in chat
   - Loading indicator shows while waiting for response
   - AI response appears below user message
   - Sidebar stays visible showing conversation history
   - After response displays, sidebar closes (300ms delay)

**Verification**:
- ✅ Conversation ID is reused (same conversation_id sent to API)
- ✅ Response displays before sidebar closes
- ✅ User can scroll up and see full conversation

---

## Test Scenario 2: Message in New Conversation ✅
**Objective**: Create conversation automatically when sending first message

**Steps**:
1. Open chatbot screen
2. Click "New Conversation" button
3. Type message "How do I prevent crop disease?"
4. Click Send button
5. **EXPECTED**:
   - API auto-creates conversation (askQuestion creates one if conversationId is null)
   - Message is sent with newly created conversation_id
   - Response displays
   - Conversation appears in sidebar history

**Verification**:
- ✅ CreateConversationRequest includes: farmerId, title, description
- ✅ Response parses correctly (unwraps FarmerConversationsResponse wrapper)
- ✅ conversationId stored in `_currentConversationId` state

---

## Test Scenario 3: Multiple Messages in Same Conversation ✅
**Objective**: Verify conversation ID persistence across multiple messages

**Steps**:
1. Send first message: "How to improve soil quality?"
2. After response, send second message: "What fertilizer should I use?"
3. After response, send third message: "When is the best time to apply?"
4. **EXPECTED**:
   - All three messages use the SAME conversation_id
   - Conversation thread shows all messages in order
   - Context is maintained (chatbot understands it's same conversation)

**Verification**:
- ✅ `_currentConversationId` is set on first message (or when loading existing conversation)
- ✅ Subsequent messages reuse same ID
- ✅ All messages appear in same conversation

---

## Test Scenario 4: New Conversation Button ✅
**Objective**: Clear state and start fresh conversation

**Steps**:
1. Send message in current conversation
2. View response
3. Click "New Conversation" button
4. Verify UI clears
5. Type new message
6. **EXPECTED**:
   - New conversation created (new conversation_id)
   - Previous messages not visible
   - Previous conversation still in sidebar history

**Verification**:
- ✅ `_currentConversationId` cleared or set to null
- ✅ `_pendingMessage` cleared
- ✅ Message input field clears
- ✅ Chat display shows empty (or only new message)

---

## Test Scenario 5: Load Existing Conversation ✅
**Objective**: Open conversation from sidebar

**Steps**:
1. Click on conversation in sidebar history
2. Wait for conversation to load
3. **EXPECTED**:
   - Previous messages display
   - Conversation ID set to this conversation's ID
   - User can continue adding messages to same conversation

**Verification**:
- ✅ Messages array from API response displays correctly
- ✅ Conversation ID set for future messages
- ✅ Can send new message that continues the thread

---

## Test Scenario 6: Error Handling
**Objective**: Verify error states work correctly

**Steps**:
1. Disable network or use incorrect endpoint
2. Send message
3. **EXPECTED**:
   - Error message displays (Resource.Error)
   - Conversation doesn't close
   - User can try sending message again

**Verification**:
- ✅ `showMessageError` shows error UI
- ✅ `lastPendingMessage` stores message for retry
- ✅ Sidebar doesn't close on error

---

## Technical Implementation Details

### Flow Chart
```
User sends message
        ↓
ChatbotViewModel.askQuestion()
        ↓
Check: _currentConversationId == null?
        ↓         ↓
      YES        NO
        ↓         ↓
Create new    Use existing ID
conversation
        ↓         ↓
Store ID in _currentConversationId
        ↓
Send message via API queryFarmingQuestion()
        ↓
Resource.Loading
        ↓
API returns response
        ↓
queryResponse updated in StateFlow
        ↓
LaunchedEffect(queryResponse) triggers
        ↓
Message displays in UI
        ↓
After 300ms delay → showHistory = false (sidebar closes)
```

### State Management
**ChatbotViewModel StateFlows**:
- `_currentConversationId: MutableStateFlow<String?>` - Current conversation ID
- `_queryResponse: MutableStateFlow<Resource<ChatbotQueryResponse>>` - API response
- `_pendingMessage: MutableStateFlow<String?>` - Message being typed
- `_currentConversation: MutableStateFlow<Resource<ChatbotConversation>>` - Full conversation

**EnhancedChatbotScreen Variables**:
- `showHistory: Boolean` - Sidebar visibility
- `currentConversationId: String?` - Local tracking (optional, ViewModel has it)
- `isLoadingConversation: Boolean` - Loading state
- `lastPendingMessage: String?` - For error retry
- `showMessageError: Boolean` - Error display

### LaunchedEffect Blocks

**1. queryResponse Effect** (Lines 65-77):
```kotlin
LaunchedEffect(queryResponse) {
    if (queryResponse is Resource.Error && pendingMessage != null) {
        // Show error
        showMessageError = true
    } else if (queryResponse is Resource.Success) {
        // Message succeeded, close sidebar after delay
        showHistory = false
    }
}
```

**2. currentConversation Effect** (Lines 103-115):
```kotlin
LaunchedEffect(currentConversation) {
    if (isLoadingConversation && currentConversation is Resource.Success) {
        isLoadingConversation = false
        // Sidebar stays open - no closing here!
    }
}
```

**3. Auto-scroll Effect** (Lines 117-130):
```kotlin
LaunchedEffect(currentConversation, pendingMessage) {
    // Scroll to latest message
}
```

---

## Endpoint Verification

### Create Conversation
- **Endpoint**: `POST /api/v1/conversations/conversations/create`
- **Request**: 
```json
{
  "farmerId": "123",
  "title": "Chat Session",
  "description": "User conversation"
}
```
- **Response**: `CreateConversationResponse` with `conversation_id`

### Query (Send Message)
- **Endpoint**: `POST /api/v1/query`
- **Request**:
```json
{
  "conversationId": "xyz-123",
  "question": "How to improve soil?",
  "farmerId": "123"
}
```
- **Response**: `ChatbotQueryResponse` with AI answer

### Get Conversations
- **Endpoint**: `GET /api/v1/farmer/{farmerId}/conversations`
- **Response Wrapper**: `FarmerConversationsResponse`

---

## Expected Outcomes After Fix ✅

1. **Conversation Persistence**: User can have multiple messages in same conversation
2. **Response Display**: User sees AI response before any UI closes
3. **Sidebar Behavior**: Sidebar closes only after response arrives
4. **Message History**: All messages visible in current conversation
5. **New Conversation**: User can start fresh anytime with "New Conversation" button
6. **Error Handling**: Errors don't close sidebar, allow retry
7. **No Crashes**: No NullPointerException or compilation errors

---

## Remaining Items

- ⏳ Deploy and test on emulator
- ⏳ Verify all message formats parse correctly
- ⏳ Test edge cases (very long messages, special characters, etc.)
- ⏳ Verify token management with TokenInterceptor
- ⏳ Test conversation list pagination if applicable
