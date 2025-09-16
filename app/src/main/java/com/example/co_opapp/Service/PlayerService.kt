package com.example.co_opapp.Service

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File

interface PlayerService {
    @Multipart
    @POST("players/{playerId}/upload-profile-picture")
    suspend fun uploadProfilePicture(
        @Path("playerId") playerId: String,
        @Part file: MultipartBody.Part
    ): Response<Unit>
}

class PlayerRepository(private val service: PlayerService) {
    suspend fun uploadProfilePicture(playerId: String, file: File): Boolean {
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val response = service.uploadProfilePicture(playerId, body)
        return response.isSuccessful
    }
}
