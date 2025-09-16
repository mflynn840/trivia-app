package com.example.co_opapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.ui.components.LoginScreen.LoginButtons
import com.example.co_opapp.R

import com.example.co_opapp.ui.components.LoginScreen.rememberLoginFormState

@Composable
fun LoginScreen(
    authService: AuthService,
    modifier: Modifier = Modifier,
    onNavigateToLobby: () -> Unit = {}
) {
    val formState = rememberLoginFormState()

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Login background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TriviaQuest",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = formState.username,
                onValueChange = formState.onUsernameChange,
                label = { Text("Username", color = Color.Black) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = formState.password,
                onValueChange = formState.onPasswordChange,
                label = { Text("Password", color = Color.Black) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            LoginButtons(
                username = formState.username,
                password = formState.password,
                authService = authService,
                onNavigateToLobby = onNavigateToLobby,
                messageState = formState.message,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = formState.message.value,
                color = Color.White
            )
        }
    }
}


