package com.app.shamba_bora.utils

object Constants {
    const val BASE_URL = "http://10.0.2.2:8080" // Android emulator localhost
    // For physical device, use: "http://<your-computer-ip>:8080"
    const val API_PREFIX = "/api"
    
    // SharedPreferences Keys
    const val PREF_NAME = "ShambaBoraPrefs"
    const val KEY_TOKEN = "auth_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USERNAME = "username"
    const val KEY_EMAIL = "email"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // Date Formats
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
    
    // Pagination
    const val DEFAULT_PAGE_SIZE = 10
    const val DEFAULT_PAGE = 0
    
    // Image Upload
    const val MAX_IMAGE_SIZE_MB = 10
}

