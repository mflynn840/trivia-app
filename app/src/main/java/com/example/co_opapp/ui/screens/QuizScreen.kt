package com.example.co_opapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.ui.components.AnswerButton
import com.example.co_opapp.ui.components.QuestionCard
import kotlinx.coroutines.launch
import com.example.co_opapp.Interface.GameDriver
import com.example.co_opapp.R




/*
 Draw the main gameplay loops UI screen
 Update the game state using the passed in GameDriver
 */
/*
 Draw the main gameplay loop UI screen
 Update the game state using the passed in GameDriver
*/
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
    val error by quizService.error.collectAsState(initial = null as String?)
    val currentQuestion by quizService.currentQuestion.collectAsState(initial = null)

    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) { quizService.fetchNextQuestions() }

    Box(modifier = modifier.fillMaxSize()) {
        // --- BACKGROUND IMAGE ---
        Image(
            painter = painterResource(id = R.drawable.forest_background),
            contentDescription = "Quiz Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- BACK BUTTON AT TOP LEFT ---
        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xCCB39DDB))
        ) {
            Text("Back", color = Color.Black)
        }

        // --- MAIN CONTENT CENTERED ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                currentQuestion != null -> {
                    val question = currentQuestion!!
                    val options = listOf(
                        question.optionA,
                        question.optionB,
                        question.optionC,
                        question.optionD
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuestionCard(
                            question = question.body,
                            questionIndex = questionIndex,
                            totalQuestions = totalQuestions
                        )

                        options.forEach { answer ->
                            AnswerButton(
                                text = answer,
                                isSelected = (answer == selectedAnswer),
                                onClick = { selectedAnswer = answer },
                                backgroundColor = Color(0xCCB39DDB)
                            )
                        }

                        Button(
                            onClick = {
                                selectedAnswer?.let { answer ->
                                    coroutineScope.launch { quizService.submitAnswer(answer) }
                                    selectedAnswer = null
                                }
                            },
                            enabled = selectedAnswer != null
                        ) {
                            Text("Submit")
                        }
                    }
                }

                error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                        Button(onClick = {
                            quizService.resetGame()
                            coroutineScope.launch { quizService.fetchNextQuestions() }
                        }) { Text("Retry") }
                    }
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Quiz Complete! Score: $score / $totalQuestions")
                        Button(onClick = {
                            coroutineScope.launch {
                                quizService.resetGame()
                                quizService.fetchNextQuestions()
                            }
                        }) { Text("Retry") }
                    }
                }
            }
        }
    }
}
