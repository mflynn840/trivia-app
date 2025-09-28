package com.example.co_opapp.RetrofitApi

import com.example.co_opapp.data_model.CreateLobbyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface LobbyListApi {
    @GET("/api/lobbies")
    suspend fun getAllLobbies(): Response<List<String>>

    @POST("/api/lobbies/create")
    suspend fun createLobby(@Body createLobbyRequest: CreateLobbyRequest)

}