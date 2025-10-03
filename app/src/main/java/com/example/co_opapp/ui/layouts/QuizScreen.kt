package com.example.co_opapp.ui.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Interfaces.GameDriver
import com.example.co_opapp.SessionManager
import com.example.co_opapp.ui.components.Popups.GradientColorPicker
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
                .padding(24.dp),
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

                        // 🔹 Question Card Background with Gradient Slider
                        Text("Question Card Background", fontWeight = FontWeight.SemiBold)
                        val questionCardGradientColors = listOf(
                            Color.White,
                            Color.Black,
                            Color.DarkGray,
                            Color(0xFF423737),
                            Color(0xFF009688),
                            Color(0xFFFFA500),
                            Color(0xFF800080),
                            Color(0xFF008080),
                            Color(0xFFFFC0CB),
                            Color(0xFFB0E0E6),
                            Color(0xFFFFE4B5),
                            Color(0xFFDC143C)
                        )
                        GradientColorPicker(
                            gradientColors = questionCardGradientColors,
                            selectedColor = SessionManager.QUESTION_PRIMARY_COLOR,
                            onColorSelected = { SessionManager.QUESTION_PRIMARY_COLOR = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        )

                        // 🔹 Answer Button Color with Gradient Slider
                        Text("Answer Button Color", fontWeight = FontWeight.SemiBold)
                        val answerButtonGradientColors = listOf(
                            Color(0xFF009688),
                            Color.Red,
                            Color.Blue,
                            Color.Green,
                            Color.Magenta,
                            Color.Yellow,
                            Color(0xFFFFA500),
                            Color(0xFF800080),
                            Color(0xFFFFC0CB),
                            Color(0xFFB0E0E6)
                        )
                        GradientColorPicker(
                            gradientColors = answerButtonGradientColors,
                            selectedColor = SessionManager.SUBMIT_BUTTON_PRIMARY_COLOR,
                            onColorSelected = { SessionManager.SUBMIT_BUTTON_PRIMARY_COLOR = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        )
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
