package com.example.co_opapp.Service.Backend

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.co_opapp.Repository.AuthRepository
import com.example.co_opapp.Service.api.AuthApi
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.LoginResponse
import com.example.co_opapp.data_model.Player
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import com.google.gson.*
import java.lang.reflect.Type
import android.util.Base64

class AuthService(context: Context) {


    val gson = GsonBuilder()
        .registerTypeAdapter(ByteArray::class.java, Base64ByteArrayAdapter())
        .create()
    private val api: AuthApi = Retrofit.Builder()
        .baseUrl("http://192.168.4.21:8080/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(AuthApi::class.java)

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

        Log.d("AuthService", "Login response: $loginResp")

        if (loginResp == null) {
            Log.e("AuthService", "Login failed: login response is null")
            return false
        }

        val userDTO = loginResp.user

        Log.d("AuthService", "UserDTO received: $userDTO")

        try {
            // Defensive check for profilePicture (if it's nullable or empty)
            //val profilePicture = userDTO.profilePicture ?: ByteArray(0)


            val player = Player(
                id = userDTO.id,
                username = userDTO.username,
                score = userDTO.score,
                ready = userDTO.isReady,
                colorPallete = userDTO.colorPallete
            )
            _currentPlayer.value = player

            SessionManager.currentPlayer = player
            authToken = loginResp.token
            SessionManager.jwtToken = authToken!!

            // Save token and username
            saveJwtToken(authToken!!, username)

            Log.d("AuthService", "Login successful for user: $username")

            return true
        } catch (e: Exception) {
            Log.e("AuthService", "Error mapping PlayerDTO to Player", e)
            return false
        }


    }
    private fun saveJwtToken(token: String, username: String) {
        with(sharedPrefs.edit()) {
            putString("jwt_token", token)
            putString("username", username)
            apply()
        }
    }
}



class Base64ByteArrayAdapter : JsonDeserializer<ByteArray> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ByteArray {
        return if (json != null && json.isJsonPrimitive && json.asJsonPrimitive.isString) {
            Base64.decode(json.asString, Base64.DEFAULT)
        } else {
            ByteArray(0)
        }
    }
}
