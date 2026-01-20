# Fix for Farm Advice 403 Forbidden Error

## Problem
The app is receiving a **403 Forbidden** error when trying to fetch farm advice from `/api/farm-analytics/advice` endpoint.

### Log Evidence
```
2025-11-13 17:20:00.416  okhttp.OkHttpClient  I  --> GET http://10.0.2.2:8080/api/farm-analytics/advice
2025-11-13 17:20:00.433  okhttp.OkHttpClient  I  <-- 403 http://10.0.2.2:8080/api/farm-analytics/advice (17ms)
```

## Root Causes (Most to Least Likely)

### 1. **Authentication Token Missing or Invalid** ⭐ MOST LIKELY
- **Problem**: The backend requires authentication but the token isn't being sent or is expired
- **Signs**: 403 error with empty response body
- **Solution**:
  ```kotlin
  // The AuthInterceptor should add: Authorization: Bearer <token>
  // Check that:
  // 1. User is logged in (token exists in SharedPreferences)
  // 2. Token is not expired on the backend
  // 3. Token has proper permissions for farm-analytics endpoint
  ```

### 2. **User Lacks Required Permissions**
- **Problem**: The user account doesn't have permission to access farm analytics
- **Signs**: 403 error for authenticated requests
- **Solution**: Check backend authorization rules for the endpoint

### 3. **Farm Profile Not Complete**
- **Problem**: Backend requires complete farm profile before granting analytics access
- **Signs**: 403 error even with valid token
- **Solution**: Ensure user has completed farm profile setup:
  ```kotlin
  // Call this first to check profile status:
  suspend fun getMyFarmerProfile(): Response<FarmerProfile>
  ```

### 4. **CORS Issues**
- **Problem**: Browser/client not allowed to access endpoint from this origin
- **Signs**: 403 with CORS headers in response (which we see)
- **Solution**: Backend needs to configure CORS properly

### 5. **Wrong Endpoint Path**
- **Problem**: Endpoint path is incorrect or doesn't exist
- **Signs**: 404 (Not Found) instead of 403
- **Solution**: Verify endpoint exists on backend

---

## Debugging Steps

### Step 1: Check if Token Exists
Add this to your app (temporarily for debugging):
```kotlin
fun checkAuthToken() {
    val token = PreferenceManager.getToken()
    Log.d("AUTH_DEBUG", "Token exists: ${token.isNotEmpty()}")
    if (token.isNotEmpty()) {
        Log.d("AUTH_DEBUG", "Token length: ${token.length}")
        Log.d("AUTH_DEBUG", "Token starts with: ${token.take(20)}...")
    } else {
        Log.d("AUTH_DEBUG", "NO TOKEN FOUND - User must log in")
    }
}
```

### Step 2: Verify Token is Sent in Request
Check logcat for Authorization header:
```
okhttp.OkHttpClient: Authorization: Bearer eyJhbGc...
```

If not present, the issue is in `AuthInterceptor` or token retrieval.

### Step 3: Test Backend Endpoint Directly
Use curl or Postman to test:
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://10.0.2.2:8080/api/farm-analytics/advice
```

### Step 4: Check User Permissions on Backend
Ensure the backend controller has proper security annotations:
```java
@GetMapping("/advice")
@Secured("ROLE_FARMER") // or similar
public ResponseEntity<FarmAdviceResponse> getFarmAdvice() {
    // implementation
}
```

### Step 5: Verify Farm Profile Exists
Before accessing analytics, backend might require:
```kotlin
// Add this call before getFarmAdvice()
val farmerProfile = mainApi.getMyFarmerProfile()
if (!farmerProfile.isSuccessful) {
    // User needs to complete farm profile first
}
```

---

## Recent Changes Made to Fix This

### 1. Enhanced Error Handling in ViewModel
**File**: `ChatbotViewModel.kt`
```kotlin
fun getFarmAdvice() {
    viewModelScope.launch {
        _farmAdvice.value = Resource.Loading()
        try {
            val token = PreferenceManager.getToken()
            Log.d("ChatbotViewModel", "Token available: ${token.isNotEmpty()}")
            
            if (token.isEmpty()) {
                _farmAdvice.value = Resource.Error("Authentication required. Please log in again.")
                return@launch
            }
            
            val response = mainApi.getFarmAdvice()
            
            if (response.isSuccessful && response.body() != null) {
                _farmAdvice.value = Resource.Success(response.body()!!)
            } else {
                val errorMsg = when (response.code()) {
                    403 -> "Access denied. Please ensure you're logged in with proper permissions."
                    401 -> "Your session has expired. Please log in again."
                    404 -> "Farm analytics data not found. Please ensure your farm profile is complete."
                    500 -> "Server error. Please try again later."
                    else -> "Failed to get farm advice: ${response.message()}"
                }
                _farmAdvice.value = Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            _farmAdvice.value = Resource.Error("Error: ${e.message ?: "Unable to connect"}")
        }
    }
}
```

### 2. Improved UI Error Dialog
**File**: `EnhancedChatbotScreen.kt`
- Shows helpful tips based on error message
- Provides retry option
- Suggests how to fix common issues

---

## Backend Requirements Checklist

- [ ] Endpoint `/api/farm-analytics/advice` exists
- [ ] Endpoint requires authentication (Authorization header)
- [ ] User role has permission to access endpoint
- [ ] User has completed farm profile
- [ ] CORS is configured correctly
- [ ] Token expiration is handled properly
- [ ] Response model matches `FarmAdviceResponse` class

---

## Next Steps

1. **Enable Debug Logging**: Run app and check logcat for:
   - "Token available: true/false"
   - "Authorization: Bearer" in OkHttp logs
   - Error response details

2. **Test Backend Endpoint**: Use Postman with your token

3. **Check Backend Logs**: Look for 403 error cause on server side

4. **Verify User Has Farm Profile**: Ensure profile is complete

5. **Check Token Expiration**: Re-login if token is old

---

## Security Notes
- Never log full token in production
- Token should be stored securely in SharedPreferences/EncryptedSharedPreferences
- Always validate token before sending to backend
- Implement proper token refresh mechanism if needed

