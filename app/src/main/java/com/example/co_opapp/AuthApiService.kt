package com.example.co_opapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body credentials: UserCredentials): Response<JwtResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body credentials: UserCredentials): Response<Void>
}
