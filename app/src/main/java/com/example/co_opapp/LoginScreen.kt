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
            // LOGIN
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val gameNetworkService = GameNetworkService()
                    val success = gameNetworkService.login(username, password)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            message = "Login successful"
                            onNavigateToLobby()
                        } else {
                            message = "Login failed: Invalid credentials"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        message = "Error: ${e.message}"
                    }
                }
            }
        }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            // REGISTER
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val gameNetworkService = GameNetworkService()
                    val success = gameNetworkService.register(username, password)
                    withContext(Dispatchers.Main) {
                        message = if (success) {
                            "Registration successful"
                        } else {
                            "Registration failed: Username may already exist"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        message = "Error: ${e.message}"
                    }
                }
            }
        }) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skip login button for testing
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
