package com.example.co_opapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson

// Data class to parse backend error responses
data class ApiError(
    val error: String? = null,
    val message: String? = null
)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToLobby: () -> Unit = {}
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // LOGIN BUTTON
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val gameNetworkService = GameNetworkService()
                try {
                    val response = gameNetworkService.authApi?.login(
                        UserCredentials(username, password)
                    )

                    withContext(Dispatchers.Main) {
                        if (response == null) {
                            message = "Error: Could not reach server"
                        } else if (response.isSuccessful) {
                            message = "Login successful"
                            onNavigateToLobby()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = try {
                                val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                                apiError.error ?: apiError.message ?: "Unknown error"
                            } catch (e: Exception) {
                                errorBody ?: "Unknown error"
                            }
                            message = "Login failed: $errorMessage"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        message = "Login failed: ${e.message}"
                    }
                }
            }
        }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // REGISTER BUTTON
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val gameNetworkService = GameNetworkService()
                try {
                    val response = gameNetworkService.authApi?.register(
                        UserCredentials(username, password)
                    )

                    withContext(Dispatchers.Main) {
                        if (response == null) {
                            message = "Error: Could not reach server"
                        } else if (response.isSuccessful) {
                            message = "Registration successful"
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = try {
                                val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                                apiError.error ?: apiError.message ?: "Unknown error"
                            } catch (e: Exception) {
                                errorBody ?: "Unknown error"
                            }
                            message = "Registration failed: $errorMessage"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        message = "Registration failed: ${e.message}"
                    }
                }
            }
        }) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skip login for testing
        Button(
            onClick = { onNavigateToLobby() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Skip Login (Testing)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = message)
    }
}
