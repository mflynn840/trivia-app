package com.example.co_opapp.RetrofitApi

import com.example.co_opapp.data_model.AnswerListResponse
import com.example.co_opapp.data_model.AnswerRequest
import com.example.co_opapp.data_model.AnswerResponse
import com.example.co_opapp.data_model.TriviaQuestion

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

    @POST("api/questions/check-answers")
    suspend fun checkAnswers(
        @Body answersRequest: List<AnswerRequest>,
        @Header("Authorization") token: String
    ): Response<AnswerListResponse>

    @POST("api/questions/check-answer")
    suspend fun checkAnswer(
        @Body answerRequest: AnswerRequest,
        @Header("Authorization") token: String
    ): Response<AnswerResponse>

}
