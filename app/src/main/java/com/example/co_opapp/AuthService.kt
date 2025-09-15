package com.example.co_opapp

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body credentials: UserCredentials): retrofit2.Response<Map<String, String>>

    @POST("api/auth/login")
    suspend fun login(@Body credentials: UserCredentials): retrofit2.Response<LoginResponse>

    @POST("api/auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): retrofit2.Response<Map<String, Any>>
}

class AuthService {
    var authApi: AuthApiService? = null
    private var authToken: String? = null

    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayerFlow: StateFlow<Player?> = _currentPlayer.asStateFlow()

    init { initializeApi() }

    private fun initializeApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.4.21:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        authApi = retrofit.create(AuthApiService::class.java)
    }

    fun getMyPlayer(): Player? = _currentPlayer.value

    suspend fun register(username: String, password: String): Boolean {
        return try { authApi?.register(UserCredentials(username, password))?.isSuccessful == true }
        catch (e: Exception) { Log.e("AuthService", "Registration failed", e); false }
    }

    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = authApi?.login(UserCredentials(username, password))
            if (response?.isSuccessful == true) {
                val loginResponse = response.body()
                _currentPlayer.value = Player(username = username)
                authToken = loginResponse?.token
                true
            } else false
        } catch (e: Exception) {
            Log.e("AuthService", "Login failed", e)
            false
        }
    }

    suspend fun validateToken(): Boolean {
        val token = authToken ?: return false
        return try {
            authApi?.validateToken(token)?.isSuccessful == true
        } catch (e: Exception) {
            Log.e("AuthService", "Token validation failed", e)
            false
        }
    }
}
