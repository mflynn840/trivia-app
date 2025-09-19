package com.example.co_opapp.Service.Backend

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.co_opapp.Repository.AuthRepository
import com.example.co_opapp.Service.api.AuthApiService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.LoginResponse
import com.example.co_opapp.data_model.Player
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthService(context: Context) {

    private val api: AuthApiService = Retrofit.Builder()
        .baseUrl("http://192.168.4.21:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApiService::class.java)

    private val repository = AuthRepository(api)

    private val _currentPlayer = mutableStateOf<Player?>(null)
    val currentPlayer: State<Player?> get() = _currentPlayer

    private var authToken: String? = null
    private val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun getJwtToken(): String? = sharedPrefs.getString("jwt_token", null)
    fun getUsername(): String? = _currentPlayer.value?.username

    suspend fun register(username: String, password: String): Boolean {
        return repository.register(username, password)
    }

    suspend fun login(username: String, password: String): Boolean {
        val loginResp: LoginResponse? = repository.login(username, password)
        return if (loginResp != null) {
            authToken = loginResp.token
            saveJwtToken(authToken!!, username)

            // Update _currentPlayer state
            val player = Player(
                username = username,
                id = loginResp.id,
                score = 0,
                ready = false,
                sessionId = ""
            )
            _currentPlayer.value = player

            // Sync the SessionManager with the updated player data
            SessionManager.currentPlayer = player  // Set the player in the SessionManager

            true
        } else {
            false
        }
    }

    suspend fun validateToken(): Boolean {
        val token = authToken ?: return false
        return repository.validateToken(token)
    }

    private fun saveJwtToken(token: String, username: String) {
        with(sharedPrefs.edit()) {
            putString("jwt_token", token)
            putString("username", username)
            apply()
        }
    }
}