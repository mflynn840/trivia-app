package com.example.co_opapp.Interface

import com.example.co_opapp.Service.AnswerResponse
import com.example.co_opapp.data_model.AnswerRequest
import com.example.co_opapp.data_model.AnswersRequest
import com.example.co_opapp.data_model.AnswersResponse
import com.example.co_opapp.data_model.TriviaQuestion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BackendQuestionApi {

    @GET("api/questions/randoms/count")
    suspend fun getRandomQuestions(
        @Query("count") count: Int,
        @Header("Authorization") token: String
        ): Response<List<TriviaQuestion>>

    @POST("api/game/check-answers")
    suspend fun checkAnswers(
        @Body answersRequest: AnswersRequest,
        @Header("Authorization") token: String
    ): Response<AnswersResponse>

}