package com.example.co_opapp.Repository

import com.example.co_opapp.RetrofitApi.QuestionApi
import com.example.co_opapp.data_model.AnswerListResponse
import com.example.co_opapp.data_model.AnswerRequest
import com.example.co_opapp.data_model.TriviaQuestion
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class TriviaRepository(baseUrl: String = "http://192.168.4.21:8080/") {

    private val api: QuestionApi

    init {
        // Create a logging interceptor
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY  // Log headers and body

        // Attach logging to OkHttpClient
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client) // Set the client with logging
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(QuestionApi::class.java)
    }

    suspend fun getRandomQuestions(count: Int, category: String, difficulty: String, token: String): Response<List<TriviaQuestion>> {
        return api.getRandomQuestions(count, category, difficulty, token)
    }

    suspend fun checkAnswers(answersRequest: List<AnswerRequest>, token: String): Response<AnswerListResponse> {
        return api.checkAnswers(answersRequest, token)
    }
}
