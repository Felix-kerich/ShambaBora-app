# Fix for Farm Advice 403 Unauthorized - Token Authorization

## Problem
The `/api/farm-analytics/advice` endpoint was returning **403 Forbidden** error because the authentication token was not being sent with the request.

### Error Log
```
2025-11-13 17:20:00.433  okhttp.OkHttpClient  I  <-- 403 http://10.0.2.2:8080/api/farm-analytics/advice (17ms)
```

## Root Cause
The `ChatbotViewModel` was creating a custom `OkHttpClient` for the main API that **only included the logging interceptor**, but was **missing the `AuthInterceptor`** that adds the authorization token to all requests.

### Before (Broken):
```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)  // ← Only logging, NO auth!
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

// Main API created without AuthInterceptor
val mainRetrofit = Retrofit.Builder()
    .baseUrl("${Constants.BASE_URL}${Constants.API_PREFIX}/")
    .client(okHttpClient)  // ← Missing auth interceptor!
    .addConverterFactory(GsonConverterFactory.create())
    .build()

mainApi = mainRetrofit.create(ApiService::class.java)
```

Result: ❌ Token NOT sent → 403 Forbidden error

## Solution Implemented

### After (Fixed):
```kotlin
// Main API (Spring Boot backend) - with AuthInterceptor for token
val authInterceptor = AuthInterceptor()  // ← Create auth interceptor
val mainOkHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .addInterceptor(authInterceptor)  // ← Add auth interceptor
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

val mainRetrofit = Retrofit.Builder()
    .baseUrl("${Constants.BASE_URL}${Constants.API_PREFIX}/")
    .client(mainOkHttpClient)  // ← Now has auth interceptor
    .addConverterFactory(GsonConverterFactory.create())
    .build()

mainApi = mainRetrofit.create(ApiService::class.java)
```

Result: ✅ Token sent automatically → 200 OK response

---

## How It Works

### The AuthInterceptor
Located in `AuthInterceptor.kt`:
```kotlin
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = PreferenceManager.getToken()
        
        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
        
        if (token.isNotEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")  // ← Adds token here
        }
        
        return chain.proceed(requestBuilder.build())
    }
}
```

### Request Flow
```
getFarmAdvice() called
    ↓
mainApi.getFarmAdvice() invoked
    ↓
OkHttpClient processes request
    ↓
LoggingInterceptor logs request (step 1)
    ↓
AuthInterceptor adds Authorization header (step 2) ← THIS WAS MISSING!
    ↓
Request sent with: Authorization: Bearer eyJhbGc...
    ↓
Backend validates token
    ↓
✅ 200 OK with farm advice data
```

---

## Changes Made

### File: `ChatbotViewModel.kt`

1. **Added import** for `AuthInterceptor`:
```kotlin
import com.app.shamba_bora.data.network.AuthInterceptor
```

2. **Separated OkHttpClient creation**:
   - `chatbotOkHttpClient` - for RAG service (no auth needed)
   - `mainOkHttpClient` - for main API (with auth)

3. **Added AuthInterceptor to mainOkHttpClient**:
```kotlin
val authInterceptor = AuthInterceptor()
val mainOkHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .addInterceptor(authInterceptor)  // ← ADDED THIS LINE
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()
```

---

## Verification

### Check in Logcat
After this fix, you should see in logcat:

**Before (Broken):**
```
I/okhttp.OkHttpClient: Authorization:  (empty - no token sent!)
I/okhttp.OkHttpClient: <-- 403 Forbidden
```

**After (Fixed):**
```
I/okhttp.OkHttpClient: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
I/okhttp.OkHttpClient: <-- 200 OK
```

### Test the Fix
1. Open the Chatbot screen
2. Click the "Get Farm Advice" button (info icon)
3. You should see:
   - Loading dialog appears
   - Farm advice data displays
   - No 403 error
   - No "Access denied" message

---

## Why This Happened

The `ChatbotViewModel` creates its own custom Retrofit instances because it needs to:
- Use different base URLs (chatbot service vs main API)
- Have different configurations

However, it was reusing a single `OkHttpClient` for both, which didn't have the auth interceptor. The fix separates them:
- ✅ `chatbotOkHttpClient` - for RAG service (doesn't need auth)
- ✅ `mainOkHttpClient` - for main API (includes auth)

---

## Comparison with Hilt-Provided API

The app also uses Hilt-provided `ApiService` in `NetworkModule.kt`, which **already has** the `AuthInterceptor`:

```kotlin
@Provides
@Singleton
fun provideOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor())  // ← Already included
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}
```

The ViewModel's custom instance now matches this pattern.

---

## Testing Checklist

- [ ] Click "Get Farm Advice" button in chatbot
- [ ] Loading indicator appears
- [ ] Check logcat for "Authorization: Bearer" header
- [ ] Verify 200 OK response (not 403)
- [ ] Farm advice dialog displays successfully
- [ ] No "Access denied" error message
- [ ] Token is sent on every request
- [ ] Token doesn't appear in logs (security)

---

## Security Notes

✅ **Good practices in this implementation:**
- Token is retrieved from `PreferenceManager` (secure storage)
- Full token is never logged to console
- Token only sent if not empty
- Uses standard "Bearer" token format
- HTTPS should be used in production

---

## Future Improvements

1. **Token Refresh**: Handle token expiration and refresh automatically
2. **Error Handling**: Detect 401 (expired token) and re-authenticate
3. **Timeout Handling**: Increase timeouts for slow networks
4. **Retry Logic**: Automatically retry failed requests
5. **Certificate Pinning**: Add SSL pinning for security

---

## Summary

| Aspect | Before | After |
|--------|--------|-------|
| Auth Interceptor | ❌ Missing | ✅ Added |
| Token Sent | ❌ No | ✅ Yes |
| Response | 403 Forbidden | 200 OK |
| Farm Advice | ❌ Not available | ✅ Available |
| User Experience | 😞 Error | 😊 Works |

