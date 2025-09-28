package com.example.co_opapp.Service.Backend

import android.util.Log
import com.example.co_opapp.RetrofitApi.LobbyListApi
import com.example.co_opapp.Service.Hooks.AuthInterceptor
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.CreateLobbyRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AvailableLobbiesService {

    private val api: LobbyListApi

    init{
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log headers + body
        }

        // Add Authorization header automatically
        val authInterceptor = AuthInterceptor()
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.4.21:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(LobbyListApi::class.java)


    }

    suspend fun getAvailableLobbies(): List<String> {
        return try {
            val response = api.getAllLobbies()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    body
                } else {
                    Log.w("AvailableLobbiesService", "Body is null")
                    emptyList()
                }
            } else {
                Log.e("AvailableLobbiesService", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("AvailableLobbiesService", "Exception while fetching lobbies: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun createLobby(lobbyName: String): Boolean{
        return try {
            val request = CreateLobbyRequest(name = lobbyName)
            val response = api.createLobby(request)
            true  // Assuming success if no exception is thrown
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
