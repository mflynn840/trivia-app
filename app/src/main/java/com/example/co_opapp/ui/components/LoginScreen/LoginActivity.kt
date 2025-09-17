package com.example.co_opapp.ui.components.LoginScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.ui.screens.LoginScreenWithMusic

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authService = AuthService(this) // pass context if needed

        setContent {
            // Launch the login screen with background music
            LoginScreenWithMusic(authService = authService)
        }
    }
}
