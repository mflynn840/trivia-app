package com.example.co_opapp.RetrofitApi

import com.example.co_opapp.data_model.ColorPallete
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ProfileApiService {
    @Multipart
    @POST("/api/players/{username}/upload-profile-picture")
    suspend fun uploadProfilePicture(
        @Path("username") username: String,
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("/api/players/{username}/get-profile-picture")
    suspend fun getAvatar(
        @Path("username") username: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @POST("/api/players/{username}/set-color-pallete")
    suspend fun uploadColorPallete(
        @Path("username") username: String,
        @Header("Authorization") token: String,
        @Body colorPallete: ColorPallete,
    ): Response<ResponseBody>
}
