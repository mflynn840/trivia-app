package com.example.co_opapp.Service

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor


interface ProfileApiService {
    @Multipart
    @POST("/api/players/{username}/upload-profile-picture")
    suspend fun uploadProfilePicture(
        @Path("username") username: String,
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("/api/players/{username}/get-profile-picture")
    suspend fun getAvatar(
        @Path("username") username: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

}

class ProfilePictureService(
    val authService : AuthService,
    val context : Context) {

    var profileApi: ProfileApiService? = null // Retrofit API instance


    init {
        // Create logging interceptor
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY  // Logs request and response lines and their respective headers and bodies (if present)
        }

        // Custom interceptor to log the endpoint
        val endpointLoggingInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request()
            Log.d("ProfilePictureService", "Request endpoint: ${request.url}")
            chain.proceed(request)
        }


        val client = OkHttpClient.Builder()
            .addInterceptor(endpointLoggingInterceptor)  // Log endpoint
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.4.21:8080/") // Base URL of the backend
            .addConverterFactory(GsonConverterFactory.create()) // JSON converter
            .client(client)
            .build()
        profileApi = retrofit.create(ProfileApiService::class.java)
    }

    suspend fun uploadProfilePicture(imageUri: Uri): Boolean {
        val part = imageUri.toMultipartBody(context, "file") ?: return false

        return try {
            val response = profileApi?.uploadProfilePicture(
                authService.getUsername()!!,
                part,
                "Bearer ${authService.getJwtToken()!!}"
            )

            if (response == null) {
                Log.e("ProfilePictureService", "No response from backend")
                return false
            }

            Log.d("ProfilePictureService", "HTTP status: ${response.code()}")

            if (response.isSuccessful) {
                // Safe parsing of body
                val body = response.body()
                Log.d("AuthService", "Upload successful: $response")
                true
            } else {
                // Read error body as string (fallback)
                val errorString = response.errorBody()?.string()?.ifEmpty { "Empty error body" }
                    ?: "Unknown error body"
                Log.e("AuthService", "Upload failed: $errorString")
                false
            }

        } catch (e: Exception) {
            Log.e("AuthService", "Exception while uploading avatar", e)
            false
        }
    }


    suspend fun getProfilePictureBytes(): ByteArray? {
        return try {
            val response = profileApi?.getAvatar(authService.getUsername()!!, "Bearer ${authService.getJwtToken()!!}")
            if (response?.isSuccessful == true) {
                response.body()?.bytes() // convert ResponseBody to ByteArray
            } else {
                Log.e("AuthService", "Failed to fetch avatar: HTTP ${response?.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Exception fetching avatar", e)
            null
        }
    }

    //send images as multipart/form data (compress images to jpeg)
    fun Uri.toMultipartBody(context: Context, name: String): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(this) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(name, "avatar.jpg", requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
