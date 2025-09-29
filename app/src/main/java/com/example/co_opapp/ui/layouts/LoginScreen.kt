package com.example.co_opapp.ui.layouts

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Backend.AuthService
import com.example.co_opapp.ui.components.LoginScreen.LoginButtons
import com.example.co_opapp.R
import com.example.co_opapp.ui.components.LoginScreen.rememberLoginFormState


@Composable
fun LoginScreen(
    authService: AuthService,
    modifier: Modifier = Modifier,
    onNavigateToLobby:  () -> Unit = {}
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
                .padding(38.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(85.dp))

            // Logo circle
            // Pulsing neon animation
            val infiniteTransition = rememberInfiniteTransition()
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(4.dp, Color(0xFF00F0FF).copy(alpha = glowAlpha), CircleShape) // pulsing border
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        ambientColor = Color(0xFF00F0FF).copy(alpha = glowAlpha), // ambient glow
                        spotColor = Color(0xFF00FFFF).copy(alpha = glowAlpha)     // spot glow
                    ),
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

            // Username
            TextField(
                value = formState.username,
                onValueChange = formState.onUsernameChange,
                label = { Text("Username", color = Color.Black, fontFamily = FontFamily.SansSerif) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Password
            TextField(
                value = formState.password,
                onValueChange = formState.onPasswordChange,
                label = { Text("Password", color = Color.Black, fontFamily = FontFamily.SansSerif) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
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

            // Feedback
            Text(
                fontFamily = FontFamily.SansSerif,
                text = formState.message.value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
    }
}

