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
import com.example.co_opapp.ui.screens.GameModeCard

@Composable
fun GameModeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSinglePlayer: () -> Unit = {},
    onNavigateToCoOp: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB)) // light blue gradient
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Title
            Text(
                text = "Choose Game Mode",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Single Player
            GameModeCard(
                icon = "🎮",
                title = "Single Player",
                description = "Play alone and test your knowledge",
                buttonText = "Start Single Player",
                buttonColor = Color(0xFF4CAF50), // green
                onClick = onNavigateToSinglePlayer
            )

            // Co-op
            GameModeCard(
                icon = "👥",
                title = "Co-op Mode",
                description = "Play with friends on the same network",
                buttonText = "Start Co-op Game",
                buttonColor = Color(0xFF2196F3), // blue
                onClick = onNavigateToCoOp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Back Button
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
