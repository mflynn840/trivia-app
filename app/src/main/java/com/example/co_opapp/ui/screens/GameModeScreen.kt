package com.example.co_opapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.co_opapp.ui.components.CharacterImageCircle
import com.example.co_opapp.ui.components.GameModeCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.co_opapp.R
import com.example.co_opapp.ui.components.CharacterBubble

@Composable
fun GameModeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSinglePlayer: () -> Unit = {},
    onNavigateToCoOp: () -> Unit = {},
    onNavigateToCharacterMode: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val imageUri by viewModel.characterImageUri.collectAsState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background image (or gradient)
        Image(
            painter = painterResource(id = R.drawable.forest_lobby),
            contentDescription = "Game Mode Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top-right character bubble
        CharacterBubble(
            imageUri = imageUri,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Title
            Text(
                text = "Choose Game Mode",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            // Game Mode Cards...
            GameModeCard(
                icon = "🎮",
                title = "Single Player",
                description = "Play alone and test your knowledge",
                buttonText = "Start Single Player",
                buttonColor = Color(0xFF4CAF50),
                onClick = onNavigateToSinglePlayer
            )

            GameModeCard(
                icon = "👥",
                title = "Co-op Mode",
                description = "Play with friends on the same network",
                buttonText = "Start Co-op Game",
                buttonColor = Color(0xFF2196F3),
                onClick = onNavigateToCoOp
            )

            // Character customization button
            Button(
                onClick = onNavigateToCharacterMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("Character Customization")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back button
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("Back to Login")
            }
        }
    }
}
