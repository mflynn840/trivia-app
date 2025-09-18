package com.example.co_opapp.RetrofitApi

import com.example.co_opapp.data_model.TriviaQuestion
import com.example.co_opapp.data_model.AnswersRequest
import com.example.co_opapp.data_model.AnswersResponse
import retrofit2.Response
import retrofit2.http.*

interface QuestionApi {
    @GET("api/questions/randoms/count/category/difficulty")
    suspend fun getRandomQuestions(
        @Query("count") count: Int,
        @Query("category") category: String,
        @Query("difficulty") difficulty: String,
        @Header("Authorization") token: String
    ): Response<List<TriviaQuestion>>

    @POST("api/game/check-answers")
    suspend fun checkAnswers(
        @Body answersRequest: AnswersRequest,
        @Header("Authorization") token: String
    ): Response<AnswersResponse>
}
