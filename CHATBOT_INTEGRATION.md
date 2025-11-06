# ShambaBora AI Chatbot Integration

## Overview

The ShambaBora mobile app now features a fully integrated AI-powered chatbot with conversation history management, powered by the RAG (Retrieval-Augmented Generation) service.

## Features Implemented

### ✅ Core Features
- **Intelligent Q&A**: AI-powered answers using RAG architecture
- **Conversation History**: Full conversation tracking and management
- **Multi-Conversation Support**: Create and manage multiple chat sessions
- **Context-Aware Responses**: Retrieves relevant farming documents for accurate answers
- **Beautiful UI**: Modern Material Design 3 interface optimized for mobile

### ✅ User Interface Components

#### 1. Main Chat Screen
- Clean, modern chat interface with message bubbles
- Real-time message display with auto-scroll
- User and AI avatars for easy identification
- Timestamp for each message
- Loading indicators during API calls

#### 2. Conversation History Sidebar
- Slide-in sidebar showing all conversations
- Conversation summaries with:
  - Title
  - Last message preview
  - Message count
  - Last updated time
- Quick access to create new conversations
- Delete conversations with confirmation dialog
- Refresh button to reload conversations

#### 3. Welcome Screen
- Friendly welcome message
- Suggested questions to get started
- AI assistant branding

#### 4. Input Section
- Multi-line text input with auto-expand
- Clear button for quick text removal
- Send button with loading state
- Disabled during API calls to prevent duplicate requests

## Technical Architecture

### Data Models (`ChatbotModels.kt`)
```kotlin
- ChatbotMessage: Individual messages with role, content, timestamp, contexts
- ChatbotContext: Retrieved document contexts with relevance scores
- ChatbotConversation: Full conversation with all messages
- ChatbotConversationSummary: Lightweight conversation info for listing
- ChatbotQueryRequest: Request to ask questions
- ChatbotQueryResponse: Response with answer and contexts
- CreateConversationRequest: Create new conversation
- UpdateConversationRequest: Update conversation metadata
```

### ViewModel (`ChatbotViewModel.kt`)
- **State Management**: Uses Kotlin StateFlow for reactive UI updates
- **API Integration**: Dedicated Retrofit instance for RAG service
- **Operations**:
  - `loadConversations()`: Fetch all user conversations
  - `createConversation()`: Create new chat session
  - `loadConversation()`: Load specific conversation with messages
  - `askQuestion()`: Send question and get AI response
  - `updateConversationTitle()`: Rename conversations
  - `deleteConversation()`: Remove conversations

### API Service Integration
All RAG service endpoints integrated:
- `POST /query`: Ask questions with conversation context
- `POST /conversations`: Create new conversations
- `GET /conversations/{id}`: Get conversation details
- `GET /users/{id}/conversations`: List user conversations
- `PATCH /conversations/{id}`: Update conversation
- `DELETE /conversations/{id}`: Delete conversation

## Setup Instructions

### 1. Start the RAG Service

```bash
cd rag-service

# Make sure Ollama is running
ollama serve

# Pull the embedding model
ollama pull nomic-embed-text

# Start the RAG service
uvicorn app.main:app --host 0.0.0.0 --port 8088 --reload
```

### 2. Configure the Mobile App

The app is already configured to connect to the RAG service:

**For Android Emulator:**
- URL: `http://10.0.2.2:8088` (already set in Constants.kt)

**For Physical Device:**
- Find your computer's IP address
- Update `Constants.kt`:
  ```kotlin
  const val CHATBOT_BASE_URL = "http://YOUR_IP:8088"
  ```

### 3. Build and Run

```bash
# In the app directory
./gradlew assembleDebug

# Or run from Android Studio
```

## Usage Guide

### Starting a Conversation
1. Open the app and navigate to the Chatbot tab
2. You'll see a welcome screen with suggested questions
3. Type your question in the input field
4. Tap the send button
5. The AI will respond with relevant information

### Managing Conversations
1. Tap the menu icon (☰) in the top-left to open history
2. See all your previous conversations
3. Tap any conversation to continue it
4. Tap "+" to create a new conversation
5. Long-press or tap "⋮" to delete a conversation

### Features in Action
- **Auto-scroll**: Messages automatically scroll into view
- **Loading States**: Visual feedback during API calls
- **Error Handling**: Graceful error messages with retry options
- **Offline Support**: Error messages when service is unavailable

## API Configuration

### User ID Management
The chatbot uses the logged-in user's ID from `PreferenceManager`:
```kotlin
val userId = PreferenceManager.getUserId().toString()
```

### Request/Response Flow
1. User types question → `ChatbotQueryRequest` created
2. API call to `/query` endpoint
3. RAG service retrieves relevant contexts
4. Gemini generates answer
5. Response saved to conversation history
6. UI updates with new message

## UI/UX Highlights

### Material Design 3
- **Color Scheme**: Uses app's primary colors
- **Typography**: Clear, readable text hierarchy
- **Spacing**: Consistent padding and margins
- **Elevation**: Subtle shadows for depth

### Animations
- Slide-in/out sidebar with fade effects
- Smooth scroll animations
- Loading indicators
- Transition animations

### Responsive Design
- Adapts to different screen sizes
- Landscape mode support
- Keyboard handling
- Touch-friendly tap targets

## Error Handling

### Network Errors
- Connection timeout: 30 seconds
- Retry mechanism with user feedback
- Graceful degradation

### API Errors
- 404: Conversation not found
- 500: Server error with retry option
- Validation errors: Clear error messages

### User Feedback
- Loading spinners during operations
- Success/error toast messages
- Inline error displays
- Retry buttons where appropriate

## Performance Considerations

### Optimization
- Lazy loading of conversations
- Efficient state management with StateFlow
- Image caching for avatars
- Debounced API calls

### Memory Management
- ViewModel lifecycle awareness
- Proper coroutine cancellation
- Resource cleanup on navigation

## Future Enhancements

### Potential Features
- [ ] Voice input for questions
- [ ] Export conversation as PDF
- [ ] Share conversations
- [ ] Search within conversations
- [ ] Conversation tags/categories
- [ ] Offline message queue
- [ ] Push notifications for responses
- [ ] Multi-language support
- [ ] Image upload for context
- [ ] Conversation analytics

### RAG Service Improvements
- [ ] WebSocket for real-time updates
- [ ] Streaming responses
- [ ] Custom knowledge base per user
- [ ] Feedback mechanism (thumbs up/down)
- [ ] Citation links to source documents

## Troubleshooting

### Common Issues

**1. Cannot connect to chatbot service**
- Ensure RAG service is running on port 8088
- Check firewall settings
- Verify IP address in Constants.kt
- Check Ollama is running

**2. Slow responses**
- First query may take longer (model loading)
- Check internet connection
- Verify Gemini API key is valid
- Monitor RAG service logs

**3. Empty conversation list**
- Create a new conversation first
- Check user ID is valid
- Verify RAG service database is accessible

**4. Build errors**
- Run `./gradlew clean`
- Sync Gradle files
- Check all dependencies are installed
- Verify Kotlin version compatibility

## Testing

### Manual Testing Checklist
- [ ] Create new conversation
- [ ] Send message and receive response
- [ ] Switch between conversations
- [ ] Delete conversation
- [ ] Rename conversation
- [ ] Test with long messages
- [ ] Test with special characters
- [ ] Test offline behavior
- [ ] Test error scenarios
- [ ] Test on different screen sizes

### API Testing
Use the RAG service documentation at `http://localhost:8088/docs` to test endpoints directly.

## Credits

- **RAG Service**: FastAPI + Ollama + Google Gemini
- **Mobile App**: Jetpack Compose + Material Design 3
- **Architecture**: MVVM with Clean Architecture principles

## Support

For issues or questions:
1. Check the RAG service logs
2. Review API documentation
3. Check mobile app logs (Logcat)
4. Verify all services are running

---

**Built with ❤️ for ShambaBora farmers**
