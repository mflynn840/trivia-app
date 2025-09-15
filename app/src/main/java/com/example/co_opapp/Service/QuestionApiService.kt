package com.example.co_opapp.Service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QuestionApiService {
    @GET("/api/questions/{index}")
    suspend fun getQuestion(@Path("index") index: Int): Response<QuestionResponse>
    
    @GET("/api/questions/random")
    suspend fun getRandomQuestions(@Query("count") count: Int = 5): Response<List<QuestionResponse>>
}

data class QuestionResponse(
    val id: Long,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String
)
