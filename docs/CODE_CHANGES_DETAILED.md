# Code Changes Summary

## File: EnhancedChatbotScreen.kt

### 1. Added Import for Color
```kotlin
import androidx.compose.ui.graphics.Color
```

---

### 2. Changed Main Layout from Row to Box (Lines ~70-100)

**Before:**
```kotlin
Row(modifier = Modifier.fillMaxSize()) {
    // History Sidebar
    AnimatedVisibility(
        visible = showHistory,
        enter = slideInHorizontally() + fadeIn(),
        exit = slideOutHorizontally() + fadeOut()
    ) {
        ConversationHistorySidebar(...)
    }
    
    // Main Chat Area
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)  // ← SQUEEZES CONTENT
    ) {
```

**After:**
```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    // Main Chat Area
    Column(
        modifier = Modifier.fillMaxSize()  // ← NO WEIGHT SQUEEZING
    ) {
```

---

### 3. Moved Sidebar to Floating Overlay (Lines ~150-195)

**Before:**
```kotlin
// Sidebar was inside Row before Column
AnimatedVisibility(visible = showHistory) {
    ConversationHistorySidebar(...)
}
```

**After:**
```kotlin
// Sidebar now floating over content
AnimatedVisibility(
    visible = showHistory,
    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
    modifier = Modifier
        .align(Alignment.CenterStart)
        .fillMaxHeight()
) {
    Box(modifier = Modifier.fillMaxHeight()) {
        ConversationHistorySidebar(...)
        
        // Close button for sidebar
        IconButton(
            onClick = { showHistory = false },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close sidebar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Scrim when sidebar is visible
if (showHistory) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.32f))
            .clickable(enabled = showHistory) { showHistory = false }
    )
}
```

---

### 4. Updated Message Sending Logic (Lines ~140-155)

**Before:**
```kotlin
onSend = {
    if (messageText.isNotBlank()) {
        viewModel.askQuestion(messageText, currentConversationId)
        messageText = ""
    }
}
```

**After:**
```kotlin
onSend = {
    if (messageText.isNotBlank()) {
        // Only create conversation if it doesn't exist
        if (currentConversationId == null) {
            viewModel.createConversation(title = messageText.take(50)) { newId ->
                currentConversationId = newId
                viewModel.askQuestion(messageText, newId)
            }
        } else {
            viewModel.askQuestion(messageText, currentConversationId)
        }
        messageText = ""
    }
}
```

---

### 5. Updated MessageList Function Signature (Lines ~580-610)

**Before:**
```kotlin
@Composable
fun MessageList(
    messages: List<ChatbotMessage>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    pendingMessage: String? = null
) {
    LazyColumn(...) {
        items(messages) { message ->
            MessageBubble(message = message)
        }
        
        pendingMessage?.let { pending ->
            item {
                PendingMessageBubble(message = pending)
            }
        }
    }
}
```

**After:**
```kotlin
@Composable
fun MessageList(
    messages: List<ChatbotMessage>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    pendingMessage: String? = null,
    isLoading: Boolean = false  // ← NEW PARAMETER
) {
    LazyColumn(...) {
        items(messages) { message ->
            MessageBubble(message = message)
        }
        
        // Show pending user message if exists
        pendingMessage?.let { pending ->
            item {
                PendingMessageBubble(message = pending)
            }
        }
        
        // ← NEW: Show AI loading indicator when waiting for response
        if (isLoading && pendingMessage != null) {
            item {
                LoadingResponseBubble()
            }
        }
    }
}
```

---

### 6. Updated MessageList Call in Screen (Lines ~125-135)

**Before:**
```kotlin
MessageList(
    messages = conv.data!!.messages,
    listState = listState,
    pendingMessage = pendingMessage
)
```

**After:**
```kotlin
MessageList(
    messages = conv.data!!.messages,
    listState = listState,
    pendingMessage = pendingMessage,
    isLoading = queryResponse is Resource.Loading  // ← NEW
)
```

---

### 7. New LoadingResponseBubble Composable (Lines ~756-812)

```kotlin
@Composable
fun LoadingResponseBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "AI",
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 16.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "AI is thinking...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Generating response",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
```

---

## Summary of Changes

| Feature | Changes | Lines |
|---------|---------|-------|
| Layout | Row → Box (overlay) | ~70-100 |
| Sidebar | Added floating overlay, scrim, close button | ~150-195 |
| Message Logic | Added conditional conversation creation | ~140-155 |
| MessageList | Added isLoading parameter | ~580-610 |
| Loading Indicator | New LoadingResponseBubble composable | ~756-812 |
| Imports | Added Color import | ~18 |

---

## No ViewModel Changes Required

The `ChatbotViewModel` already has the correct logic:
- `askQuestion()` only creates a new conversation if `conversationId` is null
- The UI now properly manages this by only calling `createConversation()` once
- All subsequent messages pass the existing `conversationId`
