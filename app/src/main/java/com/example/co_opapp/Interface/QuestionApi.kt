package com.example.co_opapp.Interface

import com.example.co_opapp.Service.AnswerResponse
import com.example.co_opapp.data_model.AnswerRequest
import com.example.co_opapp.data_model.AnswersRequest
import com.example.co_opapp.data_model.AnswersResponse
import com.example.co_opapp.data_model.TriviaQuestion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BackendQuestionApi {
    @GET("api/questions/random")
    suspend fun getRandomQuestion(): Response<TriviaQuestion>

    @GET("api/questions/randoms/count")
    suspend fun getRandomQuestions(@Query("count") count: Int): Response<List<TriviaQuestion>>


    @POST("api/game/check-answer")
    suspend fun checkAnswer(@Body answerRequest: AnswerRequest): Response<AnswerResponse>

    @POST("api/game/check-answers")
    suspend fun checkAnswers(@Body answersRequest: AnswersRequest): Response<AnswersResponse>

}