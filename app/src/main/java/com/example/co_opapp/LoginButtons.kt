package com.example.co_opapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
fun LoginButtons(
    username: String,
    password: String,
    onNavigateToLobby: () -> Unit,
    messageState: MutableState<String>
) {
    Column {
        // LOGIN BUTTON
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val gameNetworkService = GameNetworkService()
                    try {
                        val response = gameNetworkService.authApi?.login(
                            UserCredentials(username, password)
                        )

                        withContext(Dispatchers.Main) {
                            if (response == null) {
                                messageState.value = "Error: Could not reach server"
                            } else if (response.isSuccessful) {
                                messageState.value = "Login successful"
                                onNavigateToLobby()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = try {
                                    val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                                    apiError.error ?: apiError.message ?: "Unknown error"
                                } catch (e: Exception) {
                                    errorBody ?: "Unknown error"
                                }
                                messageState.value = "Login failed: $errorMessage"
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            messageState.value = "Login failed: ${e.message}"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // REGISTER BUTTON
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val gameNetworkService = GameNetworkService()
                    try {
                        val response = gameNetworkService.authApi?.register(
                            UserCredentials(username, password)
                        )

                        withContext(Dispatchers.Main) {
                            if (response == null) {
                                messageState.value = "Error: Could not reach server"
                            } else if (response.isSuccessful) {
                                messageState.value = "Registration successful"
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = try {
                                    val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                                    apiError.error ?: apiError.message ?: "Unknown error"
                                } catch (e: Exception) {
                                    errorBody ?: "Unknown error"
                                }
                                messageState.value = "Registration failed: $errorMessage"
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            messageState.value = "Registration failed: ${e.message}"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}
