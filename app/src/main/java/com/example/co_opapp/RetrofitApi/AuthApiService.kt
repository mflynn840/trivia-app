package com.example.co_opapp.Service.api

import com.example.co_opapp.data_model.LoginResponse
import com.example.co_opapp.data_model.UserCredentials
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body credentials: UserCredentials): Response<Map<String, String>>

    @POST("api/auth/login")
    suspend fun login(@Body credentials: UserCredentials): Response<LoginResponse>

    @POST("api/auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): Response<Map<String, Any>>

    @Multipart
    @POST("api/players/{username}/upload-profile-picture")
    suspend fun uploadProfilePicture(
        @Path("username") username: String,
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("api/players/{username}/get-profile-picture")
    suspend fun getAvatar(
        @Path("username") username: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>
}
