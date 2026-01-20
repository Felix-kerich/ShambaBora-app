# Token Expiration & Auto-Logout Implementation

## Overview
The app now automatically detects when a user's authentication token has expired and logs them out, redirecting them to the login screen.

## Components

### 1. **TokenExpirationManager** (`TokenExpirationManager.kt`)
A singleton object that manages token expiration events and triggers automatic logout.

**Key Features:**
- Detects when token is expired (401 Unauthorized or 403 Forbidden HTTP responses)
- Emits token expiration events through a coroutine Flow
- Clears all user data and session information when token expires

**Public Methods:**
```kotlin
// Check if HTTP response indicates token expiration
fun isTokenExpired(responseCode: Int): Boolean

// Handle token expiration - clears data and emits event
suspend fun handleTokenExpired()

// Flow that emits when token is expired
val tokenExpiredEvent: SharedFlow<Unit>
```

### 2. **AuthInterceptor** (Enhanced)
Updated to detect and handle token expiration responses.

**Changes:**
- Checks HTTP response code after each request
- When 401 or 403 is detected, calls `TokenExpirationManager.handleTokenExpired()`
- Automatically clears user session and triggers logout flow
- Happens transparently without requiring any UI intervention

**How it works:**
```kotlin
val response = chain.proceed(requestBuilder.build())

if (TokenExpirationManager.isTokenExpired(response.code)) {
    CoroutineScope(Dispatchers.IO).launch {
        TokenExpirationManager.handleTokenExpired()
    }
}
```

### 3. **MainActivity** (Enhanced)
Updated to listen for token expiration events and navigate users to login.

**Changes:**
- Collects token expiration events from `TokenExpirationManager`
- When token expires, navigates to login screen
- Clears the entire back stack to prevent returning to authenticated screens
- Provides seamless user experience

**How it works:**
```kotlin
LaunchedEffect(Unit) {
    TokenExpirationManager.tokenExpiredEvent.collect {
        // Navigate to login when token expires
        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
}
```

## Flow Diagram

```
User makes API Request
         ↓
  AuthInterceptor adds token to headers
         ↓
  Request is sent to backend
         ↓
  Backend responds with 401/403 (token expired)
         ↓
  AuthInterceptor detects expired token
         ↓
  TokenExpirationManager.handleTokenExpired()
    - Clears PreferenceManager (token, user data, roles)
    - Sets isLoggedIn = false
    - Emits tokenExpiredEvent
         ↓
  MainActivity receives tokenExpiredEvent
         ↓
  Navigate to Login Screen
    - Clear back stack
    - User sees login screen
```

## Data Cleared on Token Expiration

When token expires, the following user data is cleared:
- Authentication token
- User ID
- Username
- Email
- User roles
- Login status
- All other preferences

```kotlin
fun clear() {
    prefs.edit().clear().apply()
}
```

## Testing Token Expiration

### Manual Testing:
1. Log in to the app
2. Modify backend to return 401/403 for authenticated endpoints (or wait for actual token expiration)
3. Make any API request while logged in
4. App should:
   - Clear user data
   - Automatically redirect to login screen
   - Allow user to log in again

### Automated Testing:
```kotlin
// Test token expiration handling
@Test
fun testTokenExpirationLogout() {
    // Mock 401 response
    val isExpired = TokenExpirationManager.isTokenExpired(401)
    assertTrue(isExpired)
    
    // Verify handleTokenExpired clears session
    runTest {
        TokenExpirationManager.handleTokenExpired()
        assertEquals(false, PreferenceManager.isLoggedIn())
        assertEquals("", PreferenceManager.getToken())
    }
}
```

## HTTP Status Codes Handled

- **401 Unauthorized**: Token is invalid or expired
- **403 Forbidden**: Token doesn't have permission or has expired

Both are treated as token expiration scenarios.

## Error Handling

If a backend returns 401/403 for a reason other than token expiration:
- User will be logged out
- User will be redirected to login
- User can log in again

This is the safe default behavior for authentication failures.

## Performance Considerations

- Token expiration handling runs on `Dispatchers.IO` (background thread)
- Does not block the main thread
- Navigation happens on the main thread through Compose
- Minimal overhead (simple response code check)

## Security Notes

✅ **Secure Implementation:**
- All user data is cleared immediately when token expires
- Session cannot be restored with expired token
- User must re-authenticate
- No sensitive data remains in memory

## Future Enhancements

1. **Token Refresh**: Implement automatic token refresh before expiration
   ```kotlin
   fun refreshToken(): Result<String> { ... }
   ```

2. **User Notification**: Show toast/dialog when logout occurs
   ```kotlin
   suspend fun handleTokenExpired(showNotification: Boolean = true) { ... }
   ```

3. **Analytics**: Log token expiration events for monitoring
   ```kotlin
   Analytics.logTokenExpiration(timestamp, lastRoute)
   ```

4. **Retry Logic**: Optional automatic retry after token refresh
   ```kotlin
   fun retryFailedRequest(originalRequest: Request) { ... }
   ```

## Files Modified

1. **TokenExpirationManager.kt** (NEW)
   - Singleton object managing token expiration logic
   - Location: `app/src/main/java/com/app/shamba_bora/data/network/TokenExpirationManager.kt`

2. **AuthInterceptor.kt** (UPDATED)
   - Added token expiration detection
   - Location: `app/src/main/java/com/app/shamba_bora/data/network/AuthInterceptor.kt`

3. **MainActivity.kt** (UPDATED)
   - Added token expiration event listener
   - Location: `app/src/main/java/com/app/shamba_bora/MainActivity.kt`

## Configuration

No additional configuration needed. The implementation works automatically.

To adjust behavior, you can modify:
- HTTP status codes to treat as expiration (in `TokenExpirationManager.isTokenExpired()`)
- What data to clear on expiration (in `PreferenceManager.clear()`)
- Navigation behavior (in `MainActivity`)
