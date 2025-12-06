# Mobile App Integration Examples

## Swift (iOS)

```swift
import Foundation

class ConversationManager {
    let baseURL = "http://your-server:8000/api/v1"
    var currentConversationId: String?
    let farmerId = 7
    
    // MARK: - Send Query
    func sendQuery(_ question: String) async {
        let url = URL(string: "\(baseURL)/query")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let payload: [String: Any] = [
            "question": question,
            "farmer_id": farmerId,
            "conversation_id": currentConversationId as Any
        ]
        
        request.httpBody = try? JSONSerialization.data(withJSONObject: payload)
        
        do {
            let (data, _) = try await URLSession.shared.data(for: request)
            let response = try JSONDecoder().decode(QueryResponse.self, from: data)
            
            // Save conversation ID on first query
            if currentConversationId == nil {
                currentConversationId = response.conversation_id
                UserDefaults.standard.set(response.conversation_id, 
                                         forKey: "current_conversation")
            }
            
            // Update UI with response
            handleResponse(response)
        } catch {
            print("Error: \(error)")
        }
    }
    
    // MARK: - Load Conversation History
    func loadConversation() async {
        guard let conversationId = currentConversationId else { return }
        
        let url = URL(string: "\(baseURL)/conversations/\(conversationId)")!
        
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let response = try JSONDecoder().decode(ConversationResponse.self, from: data)
            
            // Update UI with all messages
            updateUIWithMessages(response.data.messages)
        } catch {
            print("Error loading conversation: \(error)")
        }
    }
    
    // MARK: - List All Conversations
    func listConversations() async {
        let url = URL(string: "\(baseURL)/conversations/farmer/\(farmerId)/conversations")!
        
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let response = try JSONDecoder().decode(ListConversationsResponse.self, from: data)
            
            // Display list of conversations
            displayConversationsList(response.conversations)
        } catch {
            print("Error listing conversations: \(error)")
        }
    }
    
    // MARK: - Search Conversations
    func searchConversations(query: String) async {
        let encodedQuery = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        let url = URL(string: "\(baseURL)/conversations/farmer/\(farmerId)/search?query=\(encodedQuery)")!
        
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let response = try JSONDecoder().decode(SearchResponse.self, from: data)
            
            displaySearchResults(response.conversations)
        } catch {
            print("Error searching: \(error)")
        }
    }
    
    private func handleResponse(_ response: QueryResponse) {}
    private func updateUIWithMessages(_ messages: [Message]) {}
    private func displayConversationsList(_ conversations: [Conversation]) {}
    private func displaySearchResults(_ conversations: [Conversation]) {}
}

// Models
struct QueryResponse: Codable {
    let response: String
    let conversation_id: String
    let message_id: Int
    let category: String
}

struct ConversationResponse: Codable {
    let status: String
    let data: ConversationData
}

struct ConversationData: Codable {
    let session: ConversationSession
    let messages: [Message]
}

struct ConversationSession: Codable {
    let id: Int
    let conversation_id: String
    let title: String
    let message_count: Int
    let created_at: String
    let last_message_at: String?
}

struct Message: Codable {
    let id: Int
    let user_message: String
    let ai_response: String
    let category: String?
    let created_at: String
}

struct ListConversationsResponse: Codable {
    let status: String
    let farmer_id: Int
    let total: Int
    let conversations: [Conversation]
}

struct Conversation: Codable {
    let id: Int
    let conversation_id: String
    let title: String
    let message_count: Int
    let created_at: String
    let last_message_at: String?
}

struct SearchResponse: Codable {
    let status: String
    let query: String
    let conversations: [Conversation]
}
```

---

## Kotlin (Android)

```kotlin
import retrofit2.http.*
import kotlinx.serialization.Serializable
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.serialization.json.Json

// API Interface
interface ConversationAPI {
    
    @POST("query")
    suspend fun sendQuery(@Body request: QueryRequest): QueryResponse
    
    @GET("conversations/{conversationId}")
    suspend fun getConversation(
        @Path("conversationId") id: String
    ): ConversationResponse
    
    @GET("conversations/farmer/{farmerId}/conversations")
    suspend fun listConversations(
        @Path("farmerId") farmerId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): ListConversationsResponse
    
    @GET("conversations/farmer/{farmerId}/search")
    suspend fun searchConversations(
        @Path("farmerId") farmerId: Int,
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): SearchResponse
    
    @PUT("conversations/{conversationId}")
    suspend fun updateConversation(
        @Path("conversationId") id: String,
        @Body request: UpdateConversationRequest
    ): UpdateResponse
    
    @DELETE("conversations/{conversationId}")
    suspend fun deleteConversation(
        @Path("conversationId") id: String
    ): DeleteResponse
}

// Data Classes
@Serializable
data class QueryRequest(
    val question: String,
    val farmer_id: Int? = null,
    val conversation_id: String? = null,
    val include_farmer_data: Boolean = true
)

@Serializable
data class QueryResponse(
    val response: String,
    val conversation_id: String,
    val message_id: Int,
    val category: String,
    val metadata: Metadata
)

@Serializable
data class Metadata(
    val tokens_used: Int?,
    val response_time: Float,
    val documents_retrieved: Int,
    val farmer_context_included: Boolean
)

@Serializable
data class ConversationResponse(
    val status: String,
    val data: ConversationData
)

@Serializable
data class ConversationData(
    val session: ConversationSession,
    val messages: List<Message>
)

@Serializable
data class ConversationSession(
    val id: Int,
    val conversation_id: String,
    val title: String,
    val message_count: Int,
    val created_at: String,
    val last_message_at: String? = null
)

@Serializable
data class Message(
    val id: Int,
    val user_message: String,
    val ai_response: String,
    val category: String? = null,
    val created_at: String,
    val response_time: Float? = null
)

@Serializable
data class ListConversationsResponse(
    val status: String,
    val farmer_id: Int,
    val total: Int,
    val conversations: List<Conversation>
)

@Serializable
data class Conversation(
    val id: Int,
    val conversation_id: String,
    val title: String,
    val message_count: Int,
    val created_at: String,
    val last_message_at: String? = null
)

@Serializable
data class SearchResponse(
    val status: String,
    val query: String,
    val conversations: List<Conversation>
)

@Serializable
data class UpdateConversationRequest(
    val title: String? = null,
    val description: String? = null,
    val is_archived: Boolean? = null
)

@Serializable
data class UpdateResponse(
    val status: String,
    val conversation_id: String,
    val message: String
)

@Serializable
data class DeleteResponse(
    val status: String,
    val conversation_id: String,
    val message: String
)

// Repository
class ConversationRepository(private val api: ConversationAPI) {
    
    suspend fun sendQuery(question: String, farmerId: Int, conversationId: String? = null): Result<QueryResponse> {
        return try {
            val request = QueryRequest(
                question = question,
                farmer_id = farmerId,
                conversation_id = conversationId
            )
            Result.success(api.sendQuery(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getConversation(conversationId: String): Result<ConversationResponse> {
        return try {
            Result.success(api.getConversation(conversationId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun listConversations(farmerId: Int, limit: Int = 50): Result<ListConversationsResponse> {
        return try {
            Result.success(api.listConversations(farmerId, limit = limit))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchConversations(farmerId: Int, query: String): Result<SearchResponse> {
        return try {
            Result.success(api.searchConversations(farmerId, query))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Setup Retrofit
fun createConversationAPI(baseUrl: String): ConversationAPI {
    val contentType = "application/json".toMediaType()
    
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(
            Json.asConverterFactory(contentType)
        )
        .build()
        .create(ConversationAPI::class.java)
}
```

---

## React Native / JavaScript

```javascript
import axios from 'axios';

class ConversationService {
  constructor(baseURL = 'http://localhost:8000/api/v1') {
    this.api = axios.create({ baseURL });
    this.farmerId = 7;
    this.currentConversationId = null;
  }

  // Send Query
  async sendQuery(question) {
    try {
      const response = await this.api.post('/query', {
        question,
        farmer_id: this.farmerId,
        conversation_id: this.currentConversationId
      });

      // Save conversation ID on first query
      if (!this.currentConversationId) {
        this.currentConversationId = response.data.conversation_id;
        localStorage.setItem('currentConversation', this.currentConversationId);
      }

      return response.data;
    } catch (error) {
      console.error('Error sending query:', error);
      throw error;
    }
  }

  // Load Full Conversation
  async getConversation() {
    if (!this.currentConversationId) {
      throw new Error('No conversation selected');
    }

    try {
      const response = await this.api.get(
        `/conversations/${this.currentConversationId}`
      );
      return response.data;
    } catch (error) {
      console.error('Error loading conversation:', error);
      throw error;
    }
  }

  // Get Paginated Messages
  async getMessages(limit = 100, offset = 0) {
    if (!this.currentConversationId) {
      throw new Error('No conversation selected');
    }

    try {
      const response = await this.api.get(
        `/conversations/${this.currentConversationId}/messages`,
        { params: { limit, offset } }
      );
      return response.data;
    } catch (error) {
      console.error('Error fetching messages:', error);
      throw error;
    }
  }

  // List Conversations
  async listConversations(limit = 50, offset = 0) {
    try {
      const response = await this.api.get(
        `/conversations/farmer/${this.farmerId}/conversations`,
        { params: { limit, offset } }
      );
      return response.data;
    } catch (error) {
      console.error('Error listing conversations:', error);
      throw error;
    }
  }

  // Search Conversations
  async searchConversations(query, limit = 20) {
    try {
      const response = await this.api.get(
        `/conversations/farmer/${this.farmerId}/search`,
        { params: { query, limit } }
      );
      return response.data;
    } catch (error) {
      console.error('Error searching conversations:', error);
      throw error;
    }
  }

  // Update Conversation
  async updateConversation(title, description = null, isArchived = null) {
    if (!this.currentConversationId) {
      throw new Error('No conversation selected');
    }

    try {
      const response = await this.api.put(
        `/conversations/${this.currentConversationId}`,
        { title, description, is_archived: isArchived }
      );
      return response.data;
    } catch (error) {
      console.error('Error updating conversation:', error);
      throw error;
    }
  }

  // Delete Conversation
  async deleteConversation() {
    if (!this.currentConversationId) {
      throw new Error('No conversation selected');
    }

    try {
      const response = await this.api.delete(
        `/conversations/${this.currentConversationId}`
      );
      this.currentConversationId = null;
      localStorage.removeItem('currentConversation');
      return response.data;
    } catch (error) {
      console.error('Error deleting conversation:', error);
      throw error;
    }
  }

  // Set Active Conversation
  setConversation(conversationId) {
    this.currentConversationId = conversationId;
    localStorage.setItem('currentConversation', conversationId);
  }

  // Get Current Conversation
  getCurrentConversation() {
    return this.currentConversationId;
  }
}

// Usage Example
const conversationService = new ConversationService();

// Send first query
const response = await conversationService.sendQuery(
  'How do I treat maize fall armyworm?'
);
console.log('Got conversation:', response.conversation_id);

// Send follow-up
const followUp = await conversationService.sendQuery(
  'What are the signs?'
);

// Load history
const history = await conversationService.getConversation();
console.log('Messages:', history.data.messages);

// List all conversations
const allConversations = await conversationService.listConversations();
console.log('Total conversations:', allConversations.total);

export default ConversationService;
```

---

## Flutter (Dart)

```dart
import 'package:http/http.dart' as http;
import 'dart:convert';

class ConversationAPI {
  final String baseURL;
  final int farmerId;
  String? currentConversationId;

  ConversationAPI({
    this.baseURL = 'http://localhost:8000/api/v1',
    required this.farmerId,
  }) {
    currentConversationId = null;
  }

  // Send Query
  Future<QueryResponse> sendQuery(String question) async {
    final url = Uri.parse('$baseURL/query');
    
    try {
      final response = await http.post(
        url,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'question': question,
          'farmer_id': farmerId,
          if (currentConversationId != null)
            'conversation_id': currentConversationId,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as Map<String, dynamic>;
        
        // Save conversation ID on first query
        if (currentConversationId == null) {
          currentConversationId = data['conversation_id'];
        }
        
        return QueryResponse.fromJson(data);
      } else {
        throw Exception('Failed to send query: ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }

  // Get Conversation
  Future<ConversationData> getConversation() async {
    if (currentConversationId == null) {
      throw Exception('No conversation selected');
    }

    final url = Uri.parse('$baseURL/conversations/$currentConversationId');

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as Map<String, dynamic>;
        return ConversationData.fromJson(data['data']);
      } else {
        throw Exception('Failed to get conversation: ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }

  // List Conversations
  Future<ListConversationsResponse> listConversations({
    int limit = 50,
    int offset = 0,
  }) async {
    final url = Uri.parse(
      '$baseURL/conversations/farmer/$farmerId/conversations'
      '?limit=$limit&offset=$offset',
    );

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as Map<String, dynamic>;
        return ListConversationsResponse.fromJson(data);
      } else {
        throw Exception('Failed to list conversations: ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }

  // Search Conversations
  Future<List<Conversation>> searchConversations(
    String query, {
    int limit = 20,
  }) async {
    final encodedQuery = Uri.encodeQueryComponent(query);
    final url = Uri.parse(
      '$baseURL/conversations/farmer/$farmerId/search'
      '?query=$encodedQuery&limit=$limit',
    );

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as Map<String, dynamic>;
        final conversations = (data['conversations'] as List)
            .map((c) => Conversation.fromJson(c))
            .toList();
        return conversations;
      } else {
        throw Exception('Failed to search conversations: ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }

  // Update Conversation
  Future<void> updateConversation({
    String? title,
    String? description,
    bool? isArchived,
  }) async {
    if (currentConversationId == null) {
      throw Exception('No conversation selected');
    }

    final url = Uri.parse('$baseURL/conversations/$currentConversationId');

    try {
      final response = await http.put(
        url,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          if (title != null) 'title': title,
          if (description != null) 'description': description,
          if (isArchived != null) 'is_archived': isArchived,
        }),
      );

      if (response.statusCode != 200) {
        throw Exception('Failed to update conversation: ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }

  // Delete Conversation
  Future<void> deleteConversation() async {
    if (currentConversationId == null) {
      throw Exception('No conversation selected');
    }

    final url = Uri.parse('$baseURL/conversations/$currentConversationId');

    try {
      final response = await http.delete(url);

      if (response.statusCode == 200) {
        currentConversationId = null;
      } else {
        throw Exception('Failed to delete conversation: ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }
}

// Models
class QueryResponse {
  final String response;
  final String conversationId;
  final int messageId;
  final String category;

  QueryResponse({
    required this.response,
    required this.conversationId,
    required this.messageId,
    required this.category,
  });

  factory QueryResponse.fromJson(Map<String, dynamic> json) {
    return QueryResponse(
      response: json['response'] ?? '',
      conversationId: json['conversation_id'] ?? '',
      messageId: json['message_id'] ?? 0,
      category: json['category'] ?? 'general',
    );
  }
}

class ConversationData {
  final ConversationSession session;
  final List<Message> messages;

  ConversationData({
    required this.session,
    required this.messages,
  });

  factory ConversationData.fromJson(Map<String, dynamic> json) {
    return ConversationData(
      session: ConversationSession.fromJson(json['session']),
      messages: (json['messages'] as List)
          .map((m) => Message.fromJson(m))
          .toList(),
    );
  }
}

class ConversationSession {
  final int id;
  final String conversationId;
  final String title;
  final int messageCount;
  final String createdAt;
  final String? lastMessageAt;

  ConversationSession({
    required this.id,
    required this.conversationId,
    required this.title,
    required this.messageCount,
    required this.createdAt,
    this.lastMessageAt,
  });

  factory ConversationSession.fromJson(Map<String, dynamic> json) {
    return ConversationSession(
      id: json['id'] ?? 0,
      conversationId: json['conversation_id'] ?? '',
      title: json['title'] ?? '',
      messageCount: json['message_count'] ?? 0,
      createdAt: json['created_at'] ?? '',
      lastMessageAt: json['last_message_at'],
    );
  }
}

class Message {
  final int id;
  final String userMessage;
  final String aiResponse;
  final String? category;
  final String createdAt;
  final double? responseTime;

  Message({
    required this.id,
    required this.userMessage,
    required this.aiResponse,
    this.category,
    required this.createdAt,
    this.responseTime,
  });

  factory Message.fromJson(Map<String, dynamic> json) {
    return Message(
      id: json['id'] ?? 0,
      userMessage: json['user_message'] ?? '',
      aiResponse: json['ai_response'] ?? '',
      category: json['category'],
      createdAt: json['created_at'] ?? '',
      responseTime: json['response_time']?.toDouble(),
    );
  }
}

class Conversation {
  final int id;
  final String conversationId;
  final String title;
  final int messageCount;
  final String createdAt;
  final String? lastMessageAt;

  Conversation({
    required this.id,
    required this.conversationId,
    required this.title,
    required this.messageCount,
    required this.createdAt,
    this.lastMessageAt,
  });

  factory Conversation.fromJson(Map<String, dynamic> json) {
    return Conversation(
      id: json['id'] ?? 0,
      conversationId: json['conversation_id'] ?? '',
      title: json['title'] ?? '',
      messageCount: json['message_count'] ?? 0,
      createdAt: json['created_at'] ?? '',
      lastMessageAt: json['last_message_at'],
    );
  }
}

class ListConversationsResponse {
  final String status;
  final int farmerId;
  final int total;
  final List<Conversation> conversations;

  ListConversationsResponse({
    required this.status,
    required this.farmerId,
    required this.total,
    required this.conversations,
  });

  factory ListConversationsResponse.fromJson(Map<String, dynamic> json) {
    return ListConversationsResponse(
      status: json['status'] ?? '',
      farmerId: json['farmer_id'] ?? 0,
      total: json['total'] ?? 0,
      conversations: (json['conversations'] as List)
          .map((c) => Conversation.fromJson(c))
          .toList(),
    );
  }
}
```

---

## Common Integration Pattern

All platforms follow this pattern:

1. **Initialize** with farmer ID and base URL
2. **Send Query** without `conversation_id` on first message
3. **Save** the returned `conversation_id`
4. **Include** `conversation_id` in subsequent queries
5. **Load** conversation history with the ID
6. **List** conversations for user to switch between them

---

## Error Handling

All examples include basic error handling. You should add:
- Network error handling
- Timeout handling
- Retry logic
- User-friendly error messages
- Logging for debugging
