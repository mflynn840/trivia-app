package com.example.co_opapp.data_model


// --- User/Auth ---
data class UserCredentials(val username: String, val password: String)
data class LoginResponse(
    val token: String,
    val user: PlayerDTO
)










