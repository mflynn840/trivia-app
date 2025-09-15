package com.example.co_opapp.ui.screens

import QuizService
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.ui.components.AnswerButton
import com.example.co_opapp.ui.components.QuestionCard
import com.example.co_opapp.data_model.TriviaQuestion




@Composable
fun QuizScreen(
    quizService: QuizService, // <-- Service-agnostic
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onGameComplete: (score: Int, totalQuestions: Int) -> Unit = { _, _ -> }
) {
    val score by quizService.score.collectAsState(initial = 0)
    val questionIndex by quizService.questionIndex.collectAsState(initial = 0)
    val totalQuestions by quizService.totalQuestions.collectAsState(initial = 0)
    val error by quizService.error.collectAsState(initial = null as String?)
    val currentQuestion by quizService.currentQuestion.collectAsState<TriviaQuestion?>(initial = null)


    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentQuestion) {
        if (currentQuestion == null) {
            quizService.fetchNextQuestion()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ... background image and overlay ...

        when {
            error != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { quizService.resetGame() }) { Text("Retry") }
                    Button(onClick = onNavigateBack) { Text("Go Back") }
                }
            }

            currentQuestion != null -> {
                val question = currentQuestion!!
                val options = listOf(
                    question.option1,
                    question.option2,
                    question.option3,
                    question.option4
                )
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text("Question ${questionIndex} of $totalQuestions", color = Color.White)

                    QuestionCard(question.text)

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
                                // Submit answer & fetch next question
                                CoroutineScope(Dispatchers.IO).launch {
                                    quizService.submitAnswer(answer)
                                    quizService.fetchNextQuestion()
                                }
                                selectedAnswer = null
                            }
                        },
                        enabled = selectedAnswer != null
                    ) { Text("Submit") }
                }
            }

            else -> {
                // End of quiz
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
