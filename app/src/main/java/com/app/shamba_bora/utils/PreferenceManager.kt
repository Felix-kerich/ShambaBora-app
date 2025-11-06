package com.app.shamba_bora.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private lateinit var prefs: SharedPreferences
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun getToken(): String {
        return prefs.getString(Constants.KEY_TOKEN, "") ?: ""
    }
    
    fun saveToken(token: String) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply()
    }
    
    fun getUserId(): Long {
        return prefs.getLong(Constants.KEY_USER_ID, 0L)
    }
    
    fun saveUserId(userId: Long) {
        prefs.edit().putLong(Constants.KEY_USER_ID, userId).apply()
    }
    
    fun getUsername(): String {
        return prefs.getString(Constants.KEY_USERNAME, "") ?: ""
    }
    
    fun saveUsername(username: String) {
        prefs.edit().putString(Constants.KEY_USERNAME, username).apply()
    }
    
    fun getEmail(): String {
        return prefs.getString(Constants.KEY_EMAIL, "") ?: ""
    }
    
    fun saveEmail(email: String) {
        prefs.edit().putString(Constants.KEY_EMAIL, email).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
    }
    
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(Constants.KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    fun getUserRoles(): List<String> {
        val rolesString = prefs.getString(Constants.KEY_USER_ROLES, "") ?: ""
        return if (rolesString.isNotEmpty()) {
            rolesString.split(",")
        } else {
            emptyList()
        }
    }
    
    fun saveUserRoles(roles: List<String>) {
        prefs.edit().putString(Constants.KEY_USER_ROLES, roles.joinToString(",")).apply()
    }
    
    fun hasRole(role: String): Boolean {
        return getUserRoles().contains(role)
    }
    
    fun clear() {
        prefs.edit().clear().apply()
    }
}
