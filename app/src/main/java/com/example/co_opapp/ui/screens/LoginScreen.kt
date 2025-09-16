package com.example.co_opapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.ui.components.LoginButtons
import com.example.co_opapp.R


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToLobby: () -> Unit = {} // Callback when login succeeds
) {
    // --- Local UI state ---
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }

    // Create a single instance of AuthService
    val authService = remember { AuthService() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // --- Background image ---
        Image(
            painter = painterResource(id = R.drawable.login), // make sure login.jpg is in res/drawable
            contentDescription = "Login background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- Foreground content ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White // Make sure text is visible
            )
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

            LoginButtons(
                username = username,
                password = password,
                authService = authService,
                onNavigateToLobby = onNavigateToLobby,
                messageState = message,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message.value,
                color = Color.White
            )
        }
    }
}
