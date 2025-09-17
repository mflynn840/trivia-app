package com.example.co_opapp.ui.components.LoginScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.Service.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Data class to parse backend error responses
data class ApiError(
    val error: String? = null,
    val message: String? = null
)

@Composable
fun LoginButtons(
    username: String,
    password: String,
    authService: AuthService,
    onNavigateToLobby: () -> Unit,
    messageState: MutableState<String>
) {
    Column {
        // LOGIN BUTTON
        AnimatedGradientButton(
            text = "Login",
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val success = authService.login(username, password)
                        withContext(Dispatchers.Main) {
                            if (success) {
                                messageState.value = "Login successful"
                                onNavigateToLobby()
                            } else {
                                messageState.value = "Login failed: Invalid credentials"
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            messageState.value = "Login failed: ${e.message}"
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // REGISTER BUTTON
        AnimatedGradientButton(
            text = "Register",
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val success = authService.register(username, password)
                        withContext(Dispatchers.Main) {
                            if (success) {
                                messageState.value = "Registration successful"
                            } else {
                                messageState.value = "Registration failed"
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            messageState.value = "Registration failed: ${e.message}"
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
        )
    }
}
