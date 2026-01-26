package com.app.shamba_bora.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

object CloudinaryUploader {
    private const val CLOUD_NAME = "dus3zrc9x"
    private const val API_KEY = "463749245471393"
    private const val API_SECRET = "HzNELXNTibE6QKaPGbPdWwWP-QY"
    private const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    
    /**
     * Upload an image to Cloudinary
     * @param context Android context
     * @param imageUri URI of the image to upload
     * @param folder Optional folder name in Cloudinary (e.g., "community", "marketplace", "groups")
     * @return URL of the uploaded image or null if upload failed
     */
    suspend fun uploadImage(
        context: Context,
        imageUri: Uri,
        folder: String = "shamba_bora"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Convert URI to File
            val file = getFileFromUri(context, imageUri)
                ?: return@withContext Result.failure(Exception("Failed to get file from URI"))
            
            // Generate timestamp for unique filename
            val timestamp = System.currentTimeMillis() / 1000
            
            // Generate signature for secure upload
            val signature = generateSignature(timestamp, folder)
            
            // Create multipart request
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("image/*".toMediaType())
                )
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("timestamp", timestamp.toString())
                .addFormDataPart("signature", signature)
                .addFormDataPart("folder", folder)
                .build()
            
            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()
            
            // Execute request
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "{}")
                val secureUrl = jsonResponse.optString("secure_url")
                
                if (secureUrl.isNotEmpty()) {
                    // Clean up temp file
                    file.delete()
                    Result.success(secureUrl)
                } else {
                    Result.failure(Exception("No URL in response"))
                }
            } else {
                val errorBody = response.body?.string()
                Result.failure(Exception("Upload failed: ${response.code} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate SHA-1 signature for Cloudinary authentication
     */
    private fun generateSignature(timestamp: Long, folder: String): String {
        val stringToSign = "folder=$folder&timestamp=$timestamp$API_SECRET"
        val digest = MessageDigest.getInstance("SHA-1")
        val hash = digest.digest(stringToSign.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Convert URI to File
     */
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Delete an image from Cloudinary (optional utility)
     */
    suspend fun deleteImage(publicId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis() / 1000
            val stringToSign = "public_id=$publicId&timestamp=$timestamp$API_SECRET"
            val digest = MessageDigest.getInstance("SHA-1")
            val signature = digest.digest(stringToSign.toByteArray())
                .joinToString("") { "%02x".format(it) }
            
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("public_id", publicId)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("timestamp", timestamp.toString())
                .addFormDataPart("signature", signature)
                .build()
            
            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/destroy")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
