package com.example.co_opapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.co_opapp.ui.components.CharacterImageCircle
import com.example.co_opapp.ui.components.GameModeScreen.GameModeCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import com.example.co_opapp.R
import com.example.co_opapp.Service.AuthService


@Composable
fun GameModeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSinglePlayer: () -> Unit = {},
    onNavigateToCoOp: () -> Unit = {},
    onNavigateToCharacterMode: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    authService : AuthService
) {
    Box(modifier = modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(id = R.drawable.forest_lobby),
            contentDescription = "Lobby Background",
            contentScale = ContentScale.Crop, // Fill the entire screen
            modifier = Modifier.fillMaxSize()
        )

        // Character circle in top-right
        CharacterImageCircle(
            modifier = Modifier.align(Alignment.TopEnd),
            authService = authService
        )

        // Main column with game mode cards
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .padding(48.dp)
                .align(Alignment.Center)
        ) {

            Spacer(modifier = Modifier.height(38.dp))

            Text(
                text = "Choose Game Mode",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black // use white if image is dark
            )

            GameModeCard(
                icon = "🎮",
                title = "Single Player",
                description = "Play alone and test your knowledge",
                buttonText = "Start Single Player",
                buttonColor = Color(0xFF006400),
                onClick = onNavigateToSinglePlayer
            )

            GameModeCard(
                icon = "👥",
                title = "Co-op Mode",
                description = "Play with friends on the same network",
                buttonText = "Start Co-op Game",
                buttonColor = Color(0xFF03A9F4),
                onClick = onNavigateToCoOp
            )

            Button(
                onClick = onNavigateToCharacterMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
            ) {
                Text("Character Customization",
                    fontSize = 22.sp,

                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
                .height(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
            ) {
                Text("Back to Login",
                    fontSize = 18.sp,

                    fontWeight = FontWeight.Bold,

                )
            }
        }
    }
}
