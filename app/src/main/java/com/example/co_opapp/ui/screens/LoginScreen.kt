package com.example.co_opapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.ui.components.LoginScreen.LoginButtons
import com.example.co_opapp.R
import com.example.co_opapp.ui.components.LoginScreen.AnimatedTriviaQuestLogo
import com.example.co_opapp.ui.components.LoginScreen.rememberLoginFormState

@Composable
fun LoginScreen(
    authService: AuthService,
    modifier: Modifier = Modifier,
    onNavigateToLobby: () -> Unit = {}
) {
    val formState = rememberLoginFormState()

    Box(modifier = modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Login background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top, // move content to top
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp)) // optional top spacing

            // --- Logo at top as circle ---
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

        Spacer(modifier = Modifier.height(24.dp))

            // Username field
            TextField(
                value = formState.username,
                onValueChange = formState.onUsernameChange,
                label = { Text("Username", color = Color.Black) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White.copy(alpha = 0.85f), shape = RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            TextField(
                value = formState.password,
                onValueChange = formState.onPasswordChange,
                label = { Text("Password", color = Color.Black) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White.copy(alpha = 0.85f), shape = RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login/Register buttons
            LoginButtons(
                username = formState.username,
                password = formState.password,
                authService = authService,
                onNavigateToLobby = onNavigateToLobby,
                messageState = formState.message
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Feedback message
            Text(
                text = formState.message.value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
    }
}

