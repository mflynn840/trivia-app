package com.example.co_opapp.ui.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Interfaces.GameDriver
import com.example.co_opapp.SessionManager
import com.example.co_opapp.ui.components.QuizScreen.ErrorScreen
import com.example.co_opapp.ui.components.QuizScreen.GameCompleteScreen
import com.example.co_opapp.ui.components.QuizScreen.QuestionScreen
import com.example.co_opapp.ui.components.QuizScreen.QuizBackground
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(
    quizService: GameDriver,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onGameComplete: (score: Int, totalQuestions: Int) -> Unit = { _, _ -> }
) {
    val score by quizService.score.collectAsState(initial = 0)
    val questionIndex by quizService.questionIndex.collectAsState(initial = 0)
    val totalQuestions by quizService.totalQuestions.collectAsState(initial = 0)
    val error by quizService.error.collectAsState(initial = null)
    val currentQuestion by quizService.currentQuestion.collectAsState(initial = null)

    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) { quizService.fetchNextQuestions() }

    Box(modifier = modifier.fillMaxSize()) {
        QuizBackground(onBack = onNavigateBack) {
            when {
                currentQuestion != null -> QuestionScreen(
                    question = currentQuestion!!,
                    selectedAnswer = selectedAnswer,
                    onAnswerSelected = { selectedAnswer = it },
                    onSubmit = {
                        selectedAnswer?.let { answer ->
                            coroutineScope.launch { quizService.submitAnswer(answer) }
                            selectedAnswer = null
                        }
                    },
                    cardColor = SessionManager.QUESTION_PRIMARY_COLOR,
                    buttonColor = SessionManager.SUBMIT_BUTTON_PRIMARY_COLOR,
                    buttonTextColor = SessionManager.SUBMIT_BUTTON_TEXT_COLOR
                )
                error != null -> ErrorScreen(
                    error = error!!,
                    onRetry = {
                        quizService.resetGame()
                        coroutineScope.launch { quizService.fetchNextQuestions() }
                    }
                )
                else -> GameCompleteScreen(
                    score = score,
                    totalQuestions = totalQuestions,
                    onRetry = {
                        coroutineScope.launch {
                            quizService.resetGame()
                            quizService.fetchNextQuestions()
                        }
                    }
                )
            }
        }

        // Row for Back + Settings buttons (top-left)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            IconButton(onClick = { showSettings = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }

        // Settings Popup
        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("Customize Colors", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        // 🔹 Question Card Background
                        Text("Question Card Background", fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            val colors = listOf(Color.White, Color.Black, Color.DarkGray, Color(0xFF423737), Color(0xFF009688))
                            colors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (SessionManager.QUESTION_PRIMARY_COLOR == color) 3.dp else 1.dp,
                                            color = if (SessionManager.QUESTION_PRIMARY_COLOR == color) Color.Black else Color.Gray,
                                            shape = CircleShape
                                        )
                                        .clickable { SessionManager.QUESTION_PRIMARY_COLOR = color }
                                )
                            }
                        }

                        // 🔹 Answer Button Color
                        Text("Answer Button Color", fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            val buttonColors = listOf(Color(0xFF009688), Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Yellow)
                            buttonColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (SessionManager.SUBMIT_BUTTON_PRIMARY_COLOR == color) 3.dp else 1.dp,
                                            color = if (SessionManager.SUBMIT_BUTTON_PRIMARY_COLOR == color) Color.Black else Color.Gray,
                                            shape = CircleShape
                                        )
                                        .clickable { SessionManager.SUBMIT_BUTTON_PRIMARY_COLOR = color }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettings = false }) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

