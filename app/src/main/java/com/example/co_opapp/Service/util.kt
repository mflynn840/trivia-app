package com.example.co_opapp.Service

import android.content.Context

fun getJwtToken(context: Context): String? {
    val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("jwt_token", null)
}

fun saveJwtToken(context: Context, token: String) {
    val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("jwt_token", token)
        apply()
    }
}