package com.example.co_opapp

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TriviaGameScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val gameNetworkService = remember { GameNetworkService() }
    val gameState by gameNetworkService.gameState.collectAsState()
    val connectionStatus by gameNetworkService.connectionStatus.collectAsState()
    
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    
    // Check if it's current player's turn
    val isMyTurn = gameNetworkService.isCurrentPlayerTurn()
    val currentPlayer = gameNetworkService.getCurrentPlayer()
    
    // Animation for the current player indicator
    val infiniteTransition = rememberInfiniteTransition(label = "indicator_animation")
    val indicatorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "indicator_alpha"
    )
    
    // Reset selection when new question arrives
    LaunchedEffect(gameState?.currentQuestion) {
        selectedAnswer = null
        showResult = false
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trivia Game",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onNavigateBack) {
                Text("Leave Game")
            }
        }
        
        // Game status
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
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
                    text = "Round: ${gameState?.currentRound ?: 1}/${gameState?.maxRounds ?: 10}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "Players: ${gameState?.players?.size ?: 0}/${gameState?.maxPlayers ?: 4}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Main game area
        gameState?.currentQuestion?.let { question ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Current player indicator
                    if (isMyTurn) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color.Green.copy(alpha = indicatorAlpha),
                                    RoundedCornerShape(8.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Text(
                                text = "YOUR TURN",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color.Red.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Text(
                                text = "Waiting for ${currentPlayer?.username ?: "current player"}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    
                    // Question
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Question ${gameState?.currentRound ?: 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = question.question,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Category: ${question.category} | Difficulty: ${question.difficulty}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    // Answer options
                    val allAnswers = question.getAllAnswers()
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allAnswers) { answer ->
                            val isSelected = selectedAnswer == answer
                            val isCorrectAnswer = answer == question.correctAnswer
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        showResult && isCorrectAnswer -> Color.Green.copy(alpha = 0.3f)
                                        showResult && isSelected && !isCorrectAnswer -> Color.Red.copy(alpha = 0.3f)
                                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                ),
                                onClick = {
                                    if (isMyTurn && !showResult) {
                                        selectedAnswer = answer
                                        showResult = true
                                        isCorrect = answer == question.correctAnswer
                                        
                                        // Submit answer after a short delay
                                        CoroutineScope(Dispatchers.IO).launch {
                                            delay(2000)
                                            gameNetworkService.submitAnswer(question.id, answer)
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    text = answer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    
                    // Result display
                    if (showResult) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect) 
                                    Color.Green.copy(alpha = 0.2f) 
                                else 
                                    Color.Red.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = if (isCorrect) "Correct! +1 point" else "Incorrect! The correct answer was: ${question.correctAnswer}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) Color.Green else Color.Red
                            )
                        }
                    }
                }
            }
        } ?: run {
            // No question available
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Waiting for question...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        
        // Players list with scores
        gameState?.let { room ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Players & Scores",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(room.players.sortedByDescending { it.score }) { player ->
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
                                    text = "${player.username}${if (player.isHost) " (Host)" else ""}${if (isMyPlayer) " (You)" else ""}",
                                    fontSize = 16.sp,
                                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Score: ${player.score}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    if (isCurrentPlayer) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "← Current",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
