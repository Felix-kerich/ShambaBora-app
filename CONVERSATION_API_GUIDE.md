# Mobile App Integration Guide - Conversation System

## Overview

The improved conversation system allows your mobile app to:
- Create new conversations automatically
- Save all user queries and AI responses in organized conversations
- Retrieve full conversation history
- List all conversations for a farmer
- Search and manage conversations

## Base URL
```
http://<server>:<port>/api/v1
```

---

## 1. Query with Automatic Conversation Management

### Endpoint
```
POST /query
```

### Request Body
```json
{
  "question": "What is the best time to plant maize?",
  "farmer_id": 7,
  "conversation_id": "conv_abc123xyz",
  "include_farmer_data": true
}
```

### Request Parameters
- **question** (string, required): The farmer's question - min 1 character, max 1000
- **farmer_id** (integer, optional): ID of the farmer for personalization
- **conversation_id** (string, optional): Existing conversation ID
  - If not provided, creates a new conversation automatically
  - If provided, adds message to existing conversation
- **include_farmer_data** (boolean, default: true): Include farmer's historical data in response

### Response
```json
{
  "response": "The best time to plant maize is...",
  "conversation_id": "conv_abc123xyz",
  "message_id": 2,
  "sources": [
    {
      "filename": "maize_guide.pdf",
      "document_id": "doc_123",
      "chunk_index": 5,
      "similarity_score": 0.89,
      "preview": "Early planting results in..."
    }
  ],
  "farmer_data_used": true,
  "category": "planting",
  "metadata": {
    "tokens_used": 450,
    "response_time": 2.3,
    "documents_retrieved": 5,
    "farmer_context_included": true
  }
}
```

### Example cURL
```bash
curl -X POST http://localhost:8000/api/v1/query \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is the best time to plant maize?",
    "farmer_id": 7,
    "include_farmer_data": true
  }'
```

---

## 2. Create New Conversation

### Endpoint
```
POST /conversations/create
```

### Request Body
```json
{
  "title": "My Maize Farming Q&A",
  "description": "Questions about my farm in Nakuru",
  "farmer_id": 7
}
```

### Request Parameters
- **title** (string, optional): Conversation title
  - If not provided, auto-generated as "Conversation 2024-12-06 10:30"
- **description** (string, optional): Conversation description
- **farmer_id** (integer, optional): Farmer ID

### Response
```json
{
  "status": "success",
  "conversation_id": "conv_abc123xyz",
  "message": "Conversation created successfully"
}
```

### Example cURL
```bash
curl -X POST http://localhost:8000/api/v1/conversations/create \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Maize Farming Q&A",
    "farmer_id": 7
  }'
```

---

## 3. Get Full Conversation

### Endpoint
```
GET /conversations/{conversation_id}
```

### URL Parameters
- **conversation_id** (string, required): The conversation ID

### Query Parameters
None

### Response
```json
{
  "status": "success",
  "data": {
    "session": {
      "id": 1,
      "conversation_id": "conv_abc123xyz",
      "title": "My Maize Farming Q&A",
      "description": "Questions about my farm",
      "farmer_id": 7,
      "message_count": 5,
      "is_archived": false,
      "created_at": "2024-12-06T10:30:00",
      "updated_at": "2024-12-06T10:45:00",
      "last_message_at": "2024-12-06T10:45:00"
    },
    "messages": [
      {
        "id": 1,
        "user_message": "What is the best time to plant maize?",
        "ai_response": "The best time to plant maize is...",
        "category": "planting",
        "created_at": "2024-12-06T10:30:00",
        "response_time": 2.3
      },
      {
        "id": 2,
        "user_message": "What about fertilizer?",
        "ai_response": "For fertilizer, you should use...",
        "category": "fertilizer",
        "created_at": "2024-12-06T10:35:00",
        "response_time": 1.8
      }
    ]
  }
}
```

### Example cURL
```bash
curl http://localhost:8000/api/v1/conversations/conv_abc123xyz
```

---

## 4. Get Conversation Messages (Paginated)

### Endpoint
```
GET /conversations/{conversation_id}/messages
```

### URL Parameters
- **conversation_id** (string, required): The conversation ID

### Query Parameters
- **limit** (integer, default: 100, max: 500): Maximum messages to retrieve
- **offset** (integer, default: 0): Pagination offset

### Response
```json
{
  "status": "success",
  "conversation_id": "conv_abc123xyz",
  "message_count": 2,
  "messages": [
    {
      "id": 1,
      "user_message": "What is the best time to plant maize?",
      "ai_response": "The best time to plant maize is...",
      "category": "planting",
      "created_at": "2024-12-06T10:30:00",
      "response_time": 2.3
    },
    {
      "id": 2,
      "user_message": "What about fertilizer?",
      "ai_response": "For fertilizer, you should use...",
      "category": "fertilizer",
      "created_at": "2024-12-06T10:35:00",
      "response_time": 1.8
    }
  ]
}
```

### Example cURL
```bash
curl "http://localhost:8000/api/v1/conversations/conv_abc123xyz/messages?limit=50&offset=0"
```

---

## 5. List Farmer's Conversations

### Endpoint
```
GET /conversations/farmer/{farmer_id}/conversations
```

### URL Parameters
- **farmer_id** (integer, required): The farmer's ID

### Query Parameters
- **include_archived** (boolean, default: false): Include archived conversations
- **limit** (integer, default: 50, max: 200): Maximum results
- **offset** (integer, default: 0): Pagination offset

### Response
```json
{
  "status": "success",
  "farmer_id": 7,
  "total": 5,
  "count": 2,
  "conversations": [
    {
      "id": 1,
      "conversation_id": "conv_abc123xyz",
      "title": "My Maize Farming Q&A",
      "description": "Questions about my farm",
      "message_count": 5,
      "is_archived": false,
      "created_at": "2024-12-06T10:30:00",
      "updated_at": "2024-12-06T10:45:00",
      "last_message_at": "2024-12-06T10:45:00"
    },
    {
      "id": 2,
      "conversation_id": "conv_def456xyz",
      "title": "Fertilizer Discussion",
      "description": null,
      "message_count": 3,
      "is_archived": false,
      "created_at": "2024-12-05T14:20:00",
      "updated_at": "2024-12-05T14:35:00",
      "last_message_at": "2024-12-05T14:35:00"
    }
  ]
}
```

### Example cURL
```bash
curl "http://localhost:8000/api/v1/conversations/farmer/7/conversations?limit=50&offset=0"
```

---

## 6. Update Conversation

### Endpoint
```
PUT /conversations/{conversation_id}
```

### URL Parameters
- **conversation_id** (string, required): The conversation ID

### Request Body
```json
{
  "title": "Updated Title",
  "description": "Updated description",
  "is_archived": false
}
```

### Request Parameters
- **title** (string, optional): New title
- **description** (string, optional): New description
- **is_archived** (boolean, optional): Archive/unarchive the conversation

### Response
```json
{
  "status": "success",
  "conversation_id": "conv_abc123xyz",
  "message": "Conversation updated successfully"
}
```

### Example cURL
```bash
curl -X PUT http://localhost:8000/api/v1/conversations/conv_abc123xyz \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "is_archived": false
  }'
```

---

## 7. Search Conversations

### Endpoint
```
GET /conversations/farmer/{farmer_id}/search
```

### URL Parameters
- **farmer_id** (integer, required): The farmer's ID

### Query Parameters
- **query** (string, required): Search term (min 1 character)
- **limit** (integer, default: 20, max: 100): Maximum results

### Response
```json
{
  "status": "success",
  "farmer_id": 7,
  "query": "fertilizer",
  "count": 2,
  "conversations": [
    {
      "id": 2,
      "conversation_id": "conv_def456xyz",
      "title": "Fertilizer Discussion",
      "message_count": 3,
      "created_at": "2024-12-05T14:20:00",
      "last_message_at": "2024-12-05T14:35:00"
    }
  ]
}
```

### Example cURL
```bash
curl "http://localhost:8000/api/v1/conversations/farmer/7/search?query=fertilizer&limit=20"
```

---

## 8. Delete Conversation

### Endpoint
```
DELETE /conversations/{conversation_id}
```

### URL Parameters
- **conversation_id** (string, required): The conversation ID

### Response
```json
{
  "status": "success",
  "conversation_id": "conv_abc123xyz",
  "message": "Conversation deleted successfully"
}
```

### Example cURL
```bash
curl -X DELETE http://localhost:8000/api/v1/conversations/conv_abc123xyz
```

---

## Mobile App Integration Flow

### Recommended Flow for Mobile App

#### 1. **First Query (No Conversation)**
```
POST /query
{
  "question": "How do I treat maize fall armyworm?",
  "farmer_id": 7
  // conversation_id is omitted
}
```
Response includes: `conversation_id: "conv_xyz123"`

#### 2. **Subsequent Queries (Same Conversation)**
```
POST /query
{
  "question": "What are the signs of fall armyworm?",
  "farmer_id": 7,
  "conversation_id": "conv_xyz123"  // Use the returned ID
}
```

#### 3. **Load Conversation History**
```
GET /conversations/conv_xyz123
```

#### 4. **List All Conversations**
```
GET /conversations/farmer/7/conversations
```

#### 5. **Search Conversations**
```
GET /conversations/farmer/7/search?query=fertilizer
```

---

## Response Status Codes

- **200 OK**: Request successful
- **400 Bad Request**: Validation error (e.g., invalid parameters)
- **404 Not Found**: Conversation or resource not found
- **422 Unprocessable Entity**: Invalid request format
- **500 Internal Server Error**: Server error

---

## Error Responses

### Example Error Response
```json
{
  "detail": "Error processing query: Database connection failed"
}
```

---

## Best Practices

1. **First Query**: Don't provide `conversation_id` to auto-create new conversation
2. **Subsequent Queries**: Always include `conversation_id` to maintain conversation
3. **Load History**: Call `/conversations/{id}` to get full conversation before displaying
4. **Pagination**: Use `limit` and `offset` for large conversation lists
5. **Archiving**: Archive old conversations instead of deleting for historical reference
6. **Search**: Use search endpoint to help users find previous conversations

---

## Notes

- Conversation IDs start with `conv_` prefix
- Message IDs are auto-incremented per database
- All timestamps are in ISO 8601 format
- Response times are in seconds
- Tokens used are from Gemini API
- Documents retrieved count is the number of relevant documents found
