# AI Service Quick Reference - Android Implementation

## Quick Start: Using the New AI Service

### 1. Ask a Farming Question
```kotlin
// In your Composable or ViewModel
val viewModel: ChatbotViewModel = hiltViewModel()

// Ask a question - that's it!
viewModel.askQuestion(
    question = "What is the best fertilizer for my maize crop?"
)

// The response is automatically handled and displayed
val queryResponse by viewModel.queryResponse.collectAsState()
when (queryResponse) {
    is Resource.Loading -> ShowLoadingSpinner()
    is Resource.Success -> {
        val response = (queryResponse as Resource.Success).data
        Text(response.getResponseText())
        Text("Category: ${response.category}")
        Text("Used farmer data: ${response.farmerDataUsed}")
    }
    is Resource.Error -> ShowError((queryResponse as Resource.Error).message)
    null -> {} // No response yet
}
```

### 2. Get Farm Analytics & Recommendations
```kotlin
// Request farm advice (includes AI-generated recommendations)
viewModel.getFarmAdvice()

// Display the advice
val farmAdvice by viewModel.farmAdvice.collectAsState()
when (farmAdvice) {
    is Resource.Success -> {
        val advice = (farmAdvice as Resource.Success).data
        EnhancedFarmAdviceDialog(
            advice = advice,
            onDismiss = { viewModel.clearFarmAdvice() }
        )
    }
    // Handle Loading and Error states
}
```

### 3. Get Chat History
```kotlin
// Load farmer's chat history
viewModel.loadChatHistory()

// Display history
val history by viewModel.chatHistory.collectAsState()
when (history) {
    is Resource.Success -> {
        val messages = (history as Resource.Success).data
        messages.forEach { entry ->
            DisplayChatEntry(
                userMessage = entry.userMessage,
                aiResponse = entry.aiResponse,
                timestamp = entry.createdAt,
                responseTime = entry.responseTime
            )
        }
    }
}
```

## API Reference

### POST /api/v1/query
Ask the AI a farming question with farmer context

**Request:**
```kotlin
data class ChatbotQueryRequest(
    val question: String,                    // Required: The question to ask
    val farmerId: Long? = null,              // Optional: Farmer ID
    val includeFarmerData: Boolean = true,   // Include farmer's farm data in context
    val sessionId: String? = null,           // Session ID for tracking
    val k: Int = 4                           // Number of context documents to retrieve
)
```

**Response:**
```kotlin
data class ChatbotQueryResponse(
    val response: String? = null,            // The AI's response
    val category: String? = null,            // Question category (e.g., "fertilizer", "pest")
    val sessionId: String? = null,           // Session ID used
    val farmerDataUsed: Boolean = false,     // Was farmer data included?
    val metadata: Map<String, Any>? = null   // Metadata: tokens_used, response_time, etc.
)
```

**Usage:**
```kotlin
val request = ChatbotQueryRequest(
    question = "What's the ideal planting density for maize?",
    includeFarmerData = true
)
val response = chatbotApi.queryFarmingQuestion(request)
```

### GET /api/v1/farmer/{farmerId}/history
Get the chat history for a farmer

**Response:**
```kotlin
List<ChatHistory> where ChatHistory is:
data class ChatHistory(
    val id: Long? = null,
    val sessionId: String,
    val farmerId: Long,
    val userMessage: String,
    val aiResponse: String,
    val createdAt: String,
    val responseTime: Double? = null
)
```

### GET /api/farm-analytics/ai-recommendations
Get comprehensive farm analytics and AI-generated recommendations

**Response:**
```kotlin
data class FarmAdviceResponse(
    val overallAssessment: String?,                    // Summary assessment
    val strengths: List<String>,                       // Farm strengths
    val weaknesses: List<String>,                      // Areas for improvement
    val recommendations: List<FarmRecommendation>,     // Prioritized recommendations
    val bestPractices: List<BestPractice>,            // Best practices to follow
    val cropOptimizationAdvice: String?,              // Specific crop tips
    val investmentAdvice: String?                      // Investment strategy
)
```

## ViewModel State Management

The `ChatbotViewModel` exposes these StateFlows:

```kotlin
// Query responses
val queryResponse: StateFlow<Resource<ChatbotQueryResponse>?>

// Farm advice
val farmAdvice: StateFlow<Resource<FarmAdviceResponse>?>

// Chat history
val chatHistory: StateFlow<Resource<List<ChatHistory>>>

// Pending message (showing while awaiting response)
val pendingMessage: StateFlow<String?>

// Background loading state (for long operations)
val farmAdviceBackgroundLoading: StateFlow<Boolean>
```

## UI Components

### 1. EnhancedFarmAdviceDialog
Beautiful dialog showing all farm advice data

```kotlin
EnhancedFarmAdviceDialog(
    advice = farmAdviceResponse,
    onDismiss = { viewModel.clearFarmAdvice() }
)
```

Features:
- Overall assessment card
- Color-coded strengths/weaknesses
- Priority-based recommendations (🔴🟠🟡🟢)
- Best practices with reasoning
- Crop optimization tips
- Investment strategy

### 2. RecommendationCardEnhanced
Individual recommendation with priority coloring

```kotlin
RecommendationCardEnhanced(
    recommendation = farmRecommendation,
    priority = priority  // 1-5
)
```

Shows:
- Priority level with emoji
- Recommendation text
- Expected benefits
- Category badge

### 3. AdviceSectionEnhanced
Section for grouped items (strengths, weaknesses)

```kotlin
AdviceSectionEnhanced(
    title = "Strengths",
    items = listOf("...", "..."),
    icon = Icons.Default.CheckCircle,
    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
    titleColor = MaterialTheme.colorScheme.onTertiaryContainer
)
```

## Error Handling

The app automatically handles:

1. **Timeouts** - Long-running requests start background polling
2. **Network errors** - Clear error messages displayed
3. **Auth failures** - Prompts user to log in
4. **Missing data** - Graceful fallbacks

```kotlin
// Check for errors
val error = (queryResponse as? Resource.Error)?.message
if (error != null) {
    Snackbar(message = error)
}

// Check loading state
val isLoading = queryResponse is Resource.Loading
if (isLoading) ShowProgressBar()
```

## Session Management

The ViewModel automatically:
1. Creates a unique session ID on startup
2. Includes it in all requests
3. Uses it to track conversation history
4. Can be reset: `viewModel.initializeSession()`

## Performance Tips

1. **Use LazyColumn** for long advice lists
2. **Cache responses** using remember blocks
3. **Debounce rapid questions** with timers
4. **Show skeleton loading** during metadata retrieval
5. **Handle timeouts gracefully** with background polling

## Example: Complete Chat Screen

```kotlin
@Composable
fun ChatScreenExample() {
    val viewModel: ChatbotViewModel = hiltViewModel()
    var userInput by remember { mutableStateOf("") }
    
    val queryResponse by viewModel.queryResponse.collectAsState()
    val pendingMessage by viewModel.pendingMessage.collectAsState()
    val farmAdvice by viewModel.farmAdvice.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Chat messages
        LazyColumn(modifier = Modifier.weight(1f)) {
            // Show pending message
            pendingMessage?.let {
                item { UserMessageBubble(text = it) }
            }
            
            // Show AI response
            if (queryResponse is Resource.Success) {
                val response = (queryResponse as Resource.Success).data
                item { 
                    AIMessageBubble(
                        text = response.getResponseText(),
                        category = response.category
                    )
                }
            }
        }
        
        // Input field
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    viewModel.askQuestion(userInput)
                    userInput = ""
                },
                enabled = userInput.isNotBlank() && queryResponse !is Resource.Loading
            ) {
                Text("Send")
            }
        }
    }
    
    // Show farm advice if available
    if (farmAdvice is Resource.Success) {
        EnhancedFarmAdviceDialog(
            advice = (farmAdvice as Resource.Success).data,
            onDismiss = { viewModel.clearFarmAdvice() }
        )
    }
}
```

## Troubleshooting

### Q: Response is null?
A: Check that `queryResponse` is not null and is `Resource.Success`

### Q: Farmer data not being used?
A: Set `includeFarmerData = true` in request (default)

### Q: Timeout on slow network?
A: Background polling will continue - check `farmAdviceBackgroundLoading`

### Q: Response text empty?
A: Use `response.getResponseText()` instead of directly accessing `response.response`

## Constants & Configuration

Edit these in `Constants.kt`:
```kotlin
CHATBOT_BASE_URL = "http://localhost:8000/"  // AI service URL
BASE_URL = "http://localhost:8080/"          // Main backend URL
API_PREFIX = "api"                           // API path prefix
```

## Dependencies

```gradle
// Already included in project
implementation 'com.squareup.retrofit2:retrofit:2.x'
implementation 'com.squareup.retrofit2:converter-gson:2.x'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.x'
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.x'
```

---

**Last Updated**: December 4, 2025
**Status**: ✅ Production Ready
