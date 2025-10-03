package com.example.co_opapp.ui.components.QuizScreen

import CircularTimer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.CurrentLobbyService

@Composable
fun QuizHotbar(
    currentLobbyService: CurrentLobbyService,
    modifier: Modifier = Modifier
) {
    val gameState = currentLobbyService.gameState.value
    val lobby = currentLobbyService.lobby.value
    val timer = currentLobbyService.timer.value

    if (gameState != null && lobby != null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.LightGray)
        ) {
            // Question tracker on the left
            Text(
                text = "Question ${gameState.questionIdx.value + 1} of ${lobby.numQuestions}",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            // Timer in the center (simple version)
            timer?.let {
                CircularTimer(
                    timer = timer,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
