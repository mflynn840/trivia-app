package com.example.co_opapp.Interface

import com.example.co_opapp.Service.AnswerResponse
import com.example.co_opapp.data_model.AnswerRequest
import com.example.co_opapp.data_model.TriviaQuestion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BackendQuestionApi {
    @GET("api/questions/random")
    suspend fun getRandomQuestion(): Response<TriviaQuestion>

    @POST("api/game/questions/check-answer")
    suspend fun checkAnswer(@Body answerRequest: AnswerRequest): Response<AnswerResponse>
}