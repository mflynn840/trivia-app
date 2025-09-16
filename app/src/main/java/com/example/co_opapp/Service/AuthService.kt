package com.example.co_opapp.Service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.co_opapp.data_model.LoginResponse
import com.example.co_opapp.data_model.Player
import com.example.co_opapp.data_model.UserCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


// Retrofit interface defining API endpoints for authentication
interface AuthApiService {
    // Endpoint to register a new user
    @POST("api/auth/register")
    suspend fun register(@Body credentials: UserCredentials): Response<Map<String, String>>
    // Endpoint to log in an existing user
    @POST("api/auth/login")
    suspend fun login(@Body credentials: UserCredentials): Response<LoginResponse>
    // Endpoint to validate an existing token
    @POST("api/auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): Response<Map<String, Any>>

    @Multipart
    @POST("api/players/{username}/upload-avatar")
    suspend fun uploadAvatar(
        @Path("username") username: String,
        @Part image: MultipartBody.Part
    ): Response<Map<String, String>>

}

// Service class that wraps AuthApiService for easier use in app
class AuthService(private val context: Context) {
    var authApi: AuthApiService? = null // Retrofit API instance
    private var authToken: String? = null // Stores current auth token

    // StateFlow to track the currently logged-in player
    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayerFlow: StateFlow<Player?> = _currentPlayer.asStateFlow()

    // Initialize the API on service creation
    init { initializeApi() }

    private fun initializeApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.4.21:8080/") // Base URL of the backend
            .addConverterFactory(GsonConverterFactory.create()) // JSON converter
            .build()
        authApi = retrofit.create(AuthApiService::class.java)
    }

    // Returns the current Player object
    fun getMyPlayer(): Player? = _currentPlayer.value

    // Register a new user with username/password
    suspend fun register(username: String, password: String): Boolean {
        return try { authApi?.register(UserCredentials(username, password))?.isSuccessful == true }
        catch (e: Exception) { Log.e("AuthService", "Registration failed", e); false }
    }

    // Log in a user with username/password
    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = authApi?.login(UserCredentials(username, password))

            if (response?.isSuccessful == true) {
                val loginResponse = response.body()
                if (loginResponse != null){
                    _currentPlayer.value = Player(username = username, id = loginResponse.id)
                    authToken = loginResponse?.token  // Store auth token
                    saveJwtToken(context, authToken!!)

                    true
                }else{
                    Log.e("AuthService", "Login response is null")
                    false
                }
            } else {
                Log.e("AuthService", "Login failed: ${response?.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Login failed", e)
            false
        }
    }

    // Validate the stored token with backend
    suspend fun validateToken(): Boolean {
        val token = authToken ?: return false
        return try {
            authApi?.validateToken(token)?.isSuccessful == true
        } catch (e: Exception) {
            Log.e("AuthService", "Token validation failed", e)
            false
        }
    }

    fun getJwtToken(): String? {
        val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("jwt_token", null)
    }

    fun getUsername() : String? {
        return _currentPlayer.value?.username
    }

    fun saveJwtToken(context: Context, token: String) {
        val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("jwt_token", token)
            apply()
        }
    }

    suspend fun uploadAvatar(imageUri: Uri): Boolean {
        val part = imageUri.toMultipartBody(context, "avatar") ?: return false
        return try {
            val response = authApi?.uploadAvatar(getUsername()!!, part)
            response?.isSuccessful == true
        } catch (e: Exception) {
            Log.e("AuthService", "Failed to upload avatar", e)
            false
        }
    }


}

