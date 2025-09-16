package com.example.co_opapp.Interface

import com.example.co_opapp.data_model.GameRoom
import com.example.co_opapp.data_model.Player
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface BackendLobbyApi {
    @POST("game/create")
    suspend fun createRoom(@Body player: Player): GameRoom

    @POST("game/join/{roomId}")
    suspend fun joinRoom(@Path("roomId") roomId: Long, @Body player: Player): GameRoom

    @POST("game/ready/{roomId}/{playerId}")
    suspend fun setReady(
        @Path("roomId") roomId: Long,
        @Path("playerId") playerId: Long,
        @Query("ready") ready: Boolean
    ): GameRoom

    @POST("game/start/{roomId}")
    suspend fun startGame(@Path("roomId") roomId: Long): GameRoom
}