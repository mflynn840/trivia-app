package com.example.co_opapp.Service.Hooks

import android.util.Log
import com.example.co_opapp.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = SessionManager.jwtToken
        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            Log.d("AuthInterceptor", "Adding Authorization header: Bearer $token")
            requestBuilder.addHeader("Authorization", "Bearer $token")
        } else {
            Log.w("AuthInterceptor", "No auth token found; skipping Authorization header")
        }

        return chain.proceed(requestBuilder.build())
    }
}
