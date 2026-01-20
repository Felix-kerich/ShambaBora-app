# Fix for Conversation Modal Closing Issue

## Problem
When clicking on a previous conversation in the sidebar, the modal would close immediately before the conversation content could be displayed. This created a poor user experience as users couldn't see the conversation data.

## Root Cause
The sidebar was closing immediately (`showHistory = false`) after clicking a conversation, before the `loadConversation()` network request completed. This was a timing issue.

## Solution Implemented

### 1. **Added Loading State Tracking**
```kotlin
var isLoadingConversation by remember { mutableStateOf(false) }
```
This state tracks when a conversation is actively being loaded.

### 2. **Smart Sidebar Auto-Closing**
Added a `LaunchedEffect` that monitors the conversation loading state:
```kotlin
LaunchedEffect(currentConversation) {
    if (isLoadingConversation && currentConversation is Resource.Success) {
        // Conversation loaded successfully, now close sidebar
        scope.launch {
            // Small delay to show the content before closing
            kotlinx.coroutines.delay(300)
            showHistory = false
            isLoadingConversation = false
        }
    } else if (isLoadingConversation && currentConversation is Resource.Error) {
        // Error loading conversation, keep sidebar open
        isLoadingConversation = false
    }
}
```

**Benefits:**
- ✅ Sidebar stays open while conversation is loading
- ✅ Automatically closes after content is successfully loaded
- ✅ Keeps sidebar open if there's an error (user can retry or select another)
- ✅ 300ms delay allows user to see the transition

### 3. **Visual Loading Indicator**
Updated `ConversationItem` to show a loading spinner when a conversation is being loaded:
```kotlin
if (isLoading) {
    Spacer(modifier = Modifier.width(8.dp))
    CircularProgressIndicator(
        modifier = Modifier.size(16.dp),
        strokeWidth = 2.dp,
        color = if (isSelected) 
            MaterialTheme.colorScheme.onPrimaryContainer 
        else 
            MaterialTheme.colorScheme.primary
    )
}
```

**Benefits:**
- ✅ Users see which conversation is loading
- ✅ Clear visual feedback during the load operation
- ✅ Disables interaction while loading (prevents multiple clicks)

### 4. **Updated Flow**
```
User clicks conversation
    ↓
isLoadingConversation = true
viewModel.loadConversation(conversationId)
    ↓
Show loading spinner in sidebar
    ↓
Network request completes
    ↓
currentConversation updated with data
    ↓
LaunchedEffect triggered
    ↓
300ms delay (user sees content appear)
    ↓
showHistory = false (sidebar closes)
    ↓
User sees conversation in main view
```

## Files Modified
1. **EnhancedChatbotScreen.kt**
   - Added `isLoadingConversation` state variable
   - Added `LaunchedEffect` for smart sidebar closing
   - Updated conversation click handler
   - Updated `ConversationHistorySidebar` function signature
   - Updated `ConversationItem` to show loading state

## Testing Checklist
- [ ] Click on a conversation - sidebar stays open during loading
- [ ] Loading spinner appears next to conversation title
- [ ] After conversation loads, sidebar closes automatically
- [ ] Content appears in main view
- [ ] If loading fails, sidebar stays open and error is shown
- [ ] Can retry clicking another conversation or refresh

## Improvements Made
| Before | After |
|--------|-------|
| Sidebar closes immediately | Sidebar waits for content to load |
| No loading feedback | Visual loading spinner appears |
| Bad UX when network is slow | Smooth transition with 300ms delay |
| No error handling | Keeps sidebar open on error |

## Performance Notes
- Loading state check is lightweight (boolean comparison)
- 300ms delay is imperceptible to users but allows smooth animation
- No additional network requests (just timing the existing one)
- Loading spinner uses Material3 CircularProgressIndicator (lightweight)

