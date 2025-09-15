package com.example.co_opapp

import com.example.co_opapp.Service.AuthApiService
import com.example.co_opapp.Service.QuestionApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://172.24.160.1:8080"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
    
    val questionApi: QuestionApiService by lazy {
        retrofit.create(QuestionApiService::class.java)
    }
}