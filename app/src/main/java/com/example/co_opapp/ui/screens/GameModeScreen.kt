package com.example.co_opapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameModeScreen(
    modifier: Modifier = Modifier,
    // Callback for navigating to single-player mode
    onNavigateToSinglePlayer: () -> Unit = {},
    // Callback for navigating to co-op mode
    onNavigateToCoOp: () -> Unit = {},
    // Callback for navigating back (e.g., to login screen)
    onNavigateBack: () -> Unit = {}
) {

// Root container: fills entire screen with vertical gradient background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB)) // Light blue gradient
                )
            ),
        contentAlignment = Alignment.Center  // Center content vertically and horizontally
    ) {

// Main vertical column holding all UI elements
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp), // Space between items
            modifier = Modifier.padding(32.dp) // Padding around the column
        ) {
            // Title text
            Text(
                text = "Choose Game Mode",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp)) // Small spacing

            // -------------------- Single Player Card --------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Fixed height for consistency
                // Rounded corners
                shape = RoundedCornerShape(16.dp),
                // Card shadow
                elevation = CardDefaults.cardElevation(8.dp),
                // Light green background
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {

                // Content of the card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // Icon representing single player
                    Text(
                        text = "🎮",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Card title
                    Text(
                        text = "Single Player",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Card description
                    Text(
                        text = "Play alone and test your knowledge",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Start button for single player
                    Button(
                        onClick = onNavigateToSinglePlayer,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("Start Single Player")
                    }
                }
            }

            // -------------------- Co-op Card --------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Light blue background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // Icon representing co-op
                    Text(
                        text = "👥",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Card title
                    Text(
                        text = "Co-op Mode",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Card description
                    Text(
                        text = "Play with friends on the same network",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Start button for co-op
                    Button(
                        onClick = onNavigateToCoOp,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("Start Co-op Game")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp)) // Space before back button

            // -------------------- Back Button --------------------
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {

                Text("Back to Login")

            }
        }
    }
}
