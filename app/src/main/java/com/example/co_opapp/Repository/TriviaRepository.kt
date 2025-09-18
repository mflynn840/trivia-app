package com.example.co_opapp.Repository

import com.example.co_opapp.RetrofitApi.QuestionApi
import com.example.co_opapp.data_model.TriviaQuestion
import com.example.co_opapp.data_model.AnswersRequest
import com.example.co_opapp.data_model.AnswersResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response

class TriviaRepository(baseUrl: String = "http://192.168.4.21:8080/") {

    private val api: QuestionApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(QuestionApi::class.java)
    }

    suspend fun getRandomQuestions(count: Int, category: String, difficulty: String, token: String): Response<List<TriviaQuestion>> {
        return api.getRandomQuestions(count, category, difficulty, token)
    }

    suspend fun checkAnswers(answersRequest: AnswersRequest, token: String): Response<AnswersResponse> {
        return api.checkAnswers(answersRequest, token)
    }
}
