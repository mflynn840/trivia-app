package com.example.co_opapp.Repository

import android.util.Log
import com.example.co_opapp.Service.api.AuthApiService
import com.example.co_opapp.data_model.LoginResponse
import com.example.co_opapp.data_model.UserCredentials
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class AuthRepository(private val api: AuthApiService) {

    suspend fun register(username: String, password: String): Boolean {
        return try {
            api.register(UserCredentials(username, password)).isSuccessful
        } catch (e: Exception) {
            Log.e("AuthRepository", "Register failed", e)
            false
        }
    }

    suspend fun login(username: String, password: String): LoginResponse? {
        return try {
            val response = api.login(UserCredentials(username, password))
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed", e)
            null
        }
    }

    suspend fun validateToken(token: String): Boolean {
        return try {
            api.validateToken(token).isSuccessful
        } catch (e: Exception) {
            Log.e("AuthRepository", "Token validation failed", e)
            false
        }
    }

    suspend fun uploadProfilePicture(
        username: String,
        image: MultipartBody.Part,
        token: String
    ): Response<ResponseBody>? {
        return try {
            api.uploadProfilePicture(username, image, token)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Upload failed", e)
            null
        }
    }

    suspend fun getAvatar(username: String, token: String): Response<ResponseBody>? {
        return try {
            api.getAvatar(username, token)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Fetch avatar failed", e)
            null
        }
    }
}
