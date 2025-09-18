package com.example.co_opapp.Service.Backend

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.co_opapp.Repository.ProfileRepository

/**
 * UI-facing service for uploading and fetching profile pictures.
 * Hides Retrofit/HTTP details from Compose screens.
 */
class ProfileService(private val authService: AuthService, context: Context) {

    private val repository = ProfileRepository(context)

    suspend fun uploadProfilePicture(imageUri: Uri): Boolean {
        val username = authService.getUsername() ?: return false
        val token = authService.getJwtToken() ?: return false

        return try {
            val success = repository.uploadProfilePicture(username, "Bearer $token", imageUri)
            Log.d("ProfilePictureService", "Upload success: $success")
            success
        } catch (e: Exception) {
            Log.e("ProfilePictureService", "Exception uploading avatar", e)
            false
        }
    }

    suspend fun getProfilePictureBytes(): ByteArray? {
        val username = authService.getUsername() ?: return null
        val token = authService.getJwtToken() ?: return null

        return try {
            repository.getProfilePictureBytes(username, "Bearer $token")
        } catch (e: Exception) {
            Log.e("ProfilePictureService", "Exception fetching avatar", e)
            null
        }
    }
}