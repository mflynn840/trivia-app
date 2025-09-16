package com.example.co_opapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.ui.components.AnswerButton
import com.example.co_opapp.ui.components.QuestionCard
import kotlinx.coroutines.launch

import com.example.co_opapp.Interface.GameDriver

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
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val currentQuestion by quizService.currentQuestion.collectAsState(initial = null)

    LaunchedEffect(Unit) { quizService.fetchNextQuestions() }

    Box(modifier = modifier.fillMaxSize()) {

        when {
            // --- ERROR STATE ---
            error != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    Button(onClick = {
                        quizService.resetGame()
                        coroutineScope.launch { quizService.fetchNextQuestions() }
                    }) { Text("Retry") }
                    Button(onClick = onNavigateBack) { Text("Go Back") }
                }
            }

            // --- QUESTION DISPLAY ---
            currentQuestion != null -> {
                val question = currentQuestion!!

                // Make sure to pass the full question object to QuestionCard
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text("Question ${questionIndex + 1} of $totalQuestions", color = Color.White)
                    //HERE write a line to print out the entire questions set
                    Log.d("QuizScreen", "Current question: $currentQuestion")

                    QuestionCard(question = currentQuestion!!.body)

                    val options = listOf(
                        question.optionA,
                        question.optionB,
                        question.optionC,
                        question.optionD
                    )

                    options.forEach {answer ->
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
                    ) { Text("Submit") }
                }
            }

            // --- QUIZ COMPLETE STATE ---
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Quiz Complete! Score: $score / $totalQuestions")
                    Button(onClick = onNavigateBack) { Text("Back") }
                }
            }
        }
    }
}
