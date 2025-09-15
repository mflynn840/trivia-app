package com.example.co_opapp

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    // Network service
    val gameNetworkService = remember { CoOpGameService() }
    val gameState by gameNetworkService.gameState.collectAsState()
    val connectionStatus by gameNetworkService.connectionStatus.collectAsState()

    // Infinite light animation
    val infiniteTransition = rememberInfiniteTransition(label = "light_animation")
    val lightAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "light_alpha"
    )

    val isMyTurn = gameNetworkService.isCurrentPlayerTurn()
    val currentPlayer = gameNetworkService.getCurrentPlayer()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Co-op Game",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { onNavigateBack() }) {
                Text("Leave Game")
            }
        }

        // Game status card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Game Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current Turn: ${currentPlayer?.username ?: "Unknown"}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Players: ${gameState?.players?.size ?: 0}/${gameState?.maxPlayers ?: 4}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Main game area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Light indicator
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(
                            if (isMyTurn) Color.Green.copy(alpha = lightAlpha)
                            else Color.Red.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isMyTurn) "YOUR TURN" else "WAIT",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action button
                Button(
                    onClick = { gameNetworkService.completeTurn() },
                    modifier = Modifier.size(120.dp),
                    enabled = isMyTurn && gameState?.gameState == GameState.IN_PROGRESS,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMyTurn) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = if (isMyTurn) "CLICK!" else "WAIT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Instructions
                Text(
                    text = if (isMyTurn) {
                        "It's your turn! Click the button when the light is green."
                    } else {
                        "Wait for ${currentPlayer?.username ?: "the current player"} to click their button."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Players list
        gameState?.let { room ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Players",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(120.dp)) {
                        items(room.players) { player ->
                            val isCurrentPlayer = player.id == currentPlayer?.id
                            val isMyPlayer = player.id == gameNetworkService.getMyPlayer()?.id

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = buildString {
                                        append(player.username)
                                        if (player.isHost) append(" (Host)")
                                        if (isMyPlayer) append(" (You)")
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = if (isCurrentPlayer) "← Current Turn" else "Waiting",
                                    color = if (isCurrentPlayer)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
