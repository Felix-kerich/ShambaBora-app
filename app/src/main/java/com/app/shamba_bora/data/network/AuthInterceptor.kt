package com.app.shamba_bora.data.network

import com.app.shamba_bora.utils.Constants
import com.app.shamba_bora.utils.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = PreferenceManager.getToken()
        
        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
        
        if (token.isNotEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        
        val response = chain.proceed(requestBuilder.build())
        
        // Check if response indicates token expiration (401 Unauthorized or 403 Forbidden)
        if (TokenExpirationManager.isTokenExpired(response.code)) {
            // Handle token expiration on a background coroutine
            CoroutineScope(Dispatchers.IO).launch {
                TokenExpirationManager.handleTokenExpired()
            }
        }
        
        return response
    }
}

