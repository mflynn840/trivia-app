package com.example.co_opapp.Service

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()




interface CategorySelectorApi {

    // Returns counts grouped by category and difficulty
    @GET("/api/questions/counts_by_category")
    suspend fun getCountsByCategory( @Header("Authorization") token: String): Map<String, Map<String, Long>>
}


class CategorySelectorService(context: Context, val JWT_token: String) {

    private val api: CategorySelectorApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.4.21:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) //add a loggin client
            .build()

        api = retrofit.create(CategorySelectorApi::class.java)
    }

    suspend fun fetchCounts(): Map<String, Map<String, Long>>{
        return withContext(Dispatchers.IO) {
            api.getCountsByCategory("Bearer $JWT_token")
        }
    }
}
