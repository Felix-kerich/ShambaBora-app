package com.app.shamba_bora.data.network

import android.content.Context
import com.app.shamba_bora.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Manages token expiration events and triggers automatic logout
 * Detects 401 Unauthorized responses indicating token has expired
 */
object TokenExpirationManager {
    private val _tokenExpiredEvent = MutableSharedFlow<Unit>(replay = 1)
    val tokenExpiredEvent: SharedFlow<Unit> = _tokenExpiredEvent.asSharedFlow()
    
    /**
     * Called when token expiration is detected (e.g., 401 response)
     */
    suspend fun handleTokenExpired() {
        // Clear all stored user data
        PreferenceManager.clear()
        PreferenceManager.setLoggedIn(false)
        
        // Emit event to trigger navigation to login
        _tokenExpiredEvent.emit(Unit)
    }
    
    /**
     * Check if response indicates token expiration
     */
    fun isTokenExpired(responseCode: Int): Boolean {
        // 401 Unauthorized means token is invalid or expired
        // 403 Forbidden could also indicate token issues
        return responseCode == 401 || responseCode == 403
    }
}
