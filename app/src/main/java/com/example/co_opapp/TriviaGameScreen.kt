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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun TriviaGameScreen(
    modifier: Modifier = Modifier,
    gameService: CoOpGameService,
    onNavigateBack: () -> Unit
) {
    val gameState by gameService.gameState.collectAsState()
    val connectionStatus by gameService.connectionStatus.collectAsState()

    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    val currentPlayer = gameService.getCurrentPlayer()
    val isMyTurn = gameService.isCurrentPlayerTurn()

    val infiniteTransition = rememberInfiniteTransition()
    val indicatorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
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
        // HEADER
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
            Button(onClick = onNavigateBack) { Text("Leave Game") }
        }

        // GAME STATUS
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Game Status", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("Current Turn: ${currentPlayer?.username ?: "Unknown"}", style = MaterialTheme.typography.bodyLarge)
                Text("Round: ${gameState?.currentRound ?: 1}/${gameState?.maxRounds ?: 10}", style = MaterialTheme.typography.bodyMedium)
                Text("Players: ${gameState?.players?.size ?: 0}/${gameState?.maxPlayers ?: 4}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // MAIN GAME AREA
        gameState?.currentQuestion?.let { question ->
            val options = listOf(
                question.option1,
                question.option2,
                question.option3,
                question.option4
            )

            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Current player indicator
                    if (isMyTurn) {
                        Box(
                            modifier = Modifier.fillMaxWidth().background(Color.Green.copy(alpha = indicatorAlpha), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("YOUR TURN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().background(Color.Red.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Waiting for ${currentPlayer?.username ?: "current player"}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    // QUESTION
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Question ${gameState?.currentRound ?: 1}", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.height(8.dp))
                            Text(question.questionText, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }

                    // ANSWERS
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(options) { answer ->
                            val isSelected = selectedAnswer == answer
                            val isCorrectAnswer = gameState?.correctAnswer == answer
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
                                        // Submit answer to backend for correctness check
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val result = gameService.submitAnswer(question.id, answer)
                                            isCorrect = result
                                        }
                                    }
                                }
                            ) {
                                Text(answer, modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }

                    // RESULT
                    if (showResult) {
                        Card(colors = CardDefaults.cardColors(containerColor = if (isCorrect) Color.Green.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f))) {
                            Text(
                                text = if (isCorrect) "Correct! +1 point" else "Incorrect! The correct answer was: ${gameState?.correctAnswer}",
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
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
            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Waiting for question...", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // PLAYER LIST
        gameState?.let { room ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Players & Scores", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(120.dp)) {
                        items(room.players.sortedByDescending { it.score }) { player ->
                            val isCurrentPlayer = player.id == currentPlayer?.id
                            val isMyPlayer = player.id == gameService.getMyPlayer()?.id

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${player.username}${if (player.isHost) " (Host)" else ""}${if (isMyPlayer) " (You)" else ""}",
                                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Score: ${player.score}", fontWeight = FontWeight.Bold)
                                    if (isCurrentPlayer) {
                                        Spacer(Modifier.width(8.dp))
                                        Text("← Current", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
