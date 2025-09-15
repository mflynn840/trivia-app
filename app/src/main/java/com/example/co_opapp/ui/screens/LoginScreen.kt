package com.example.co_opapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.ui.components.LoginButtons


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToLobby: () -> Unit = {} // Callback when login succeeds
) {

    // --- Local UI state ---
    var username by remember { mutableStateOf("") } // Stores entered username
    var password by remember { mutableStateOf("") } // Stores entered password
    val message = remember { mutableStateOf("") } // Message for errors or status

    // Create a single instance of AuthService
    val authService = remember { AuthService() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- Screen title ---
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // --- Username input field ---
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Password input field ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- Login buttons composable ---
        // Pass the authService to the buttons
        LoginButtons(
            username = username,
            password = password,
            authService = authService,
            onNavigateToLobby = onNavigateToLobby,
            messageState = message,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Message display ---
        // Shows login errors or status messages
        Text(text = message.value)
    }
}
