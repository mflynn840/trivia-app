package com.example.co_opapp.Repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.co_opapp.RetrofitApi.ProfileApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileRepository(private val context: Context) {

    private val profileApi: ProfileApiService

    init {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val endpointLoggingInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request()
            Log.d("ProfileRepository", "Request endpoint: ${request.url}")
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(endpointLoggingInterceptor)
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.4.21:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        profileApi = retrofit.create(ProfileApiService::class.java)
    }

    suspend fun uploadProfilePicture(username: String, token: String, imageUri: Uri): Boolean {
        val part = imageUri.toMultipartBody(context, "file") ?: return false
        val response = profileApi.uploadProfilePicture(username, part, token)
        return response.isSuccessful
    }

    suspend fun getProfilePictureBytes(username: String, token: String): ByteArray? {
        val response = profileApi.getAvatar(username, token)
        return if (response.isSuccessful) response.body()?.bytes() else null
    }

    private fun Uri.toMultipartBody(context: Context, name: String): MultipartBody.Part? {
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
