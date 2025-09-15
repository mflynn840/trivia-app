package com.example.co_opapp

import androidx.compose.foundation.layout.*              // For Column, Spacer, etc.
import androidx.compose.material3.*                    // For OutlinedTextField, Button, Text, etc.
import androidx.compose.runtime.*                      // For remember, mutableStateOf, etc.
import androidx.compose.ui.Alignment                  // For Alignment.CenterHorizontally
import androidx.compose.ui.Modifier                    // For Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation  // For password field masking
import androidx.compose.ui.unit.dp                      // For dp
import kotlinx.coroutines.CoroutineScope              // For CoroutineScope
import kotlinx.coroutines.Dispatchers                 // For Dispatchers.IO and Dispatchers.Main
import kotlinx.coroutines.launch                     // For launch coroutine
import kotlinx.coroutines.withContext                // For withContext to switch between threads
import androidx.compose.ui.platform.LocalContext     // For LocalContext


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
                            // Try to extract error message from backend response
                            val errorBody = response.errorBody()?.string()
                            message = "Login failed: ${errorBody ?: "Unknown error"}"
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
                            message = "Registration failed: ${errorBody ?: "Unknown error"}"
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
