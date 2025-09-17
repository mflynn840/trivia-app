package com.example.co_opapp.ui.components.LoginScreen

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.ui.screens.LoginScreen

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authService = AuthService(this)

        setContent {
            LoginScreen(authService = authService)
        }
    }
}
