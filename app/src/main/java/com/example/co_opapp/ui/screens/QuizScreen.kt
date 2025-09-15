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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(
    // Service providing questions, answers, score, etc.
    quizService: QuizService,
    modifier: Modifier = Modifier,
    // Callback for "Back" navigation
    onNavigateBack: () -> Unit = {},
    // Callback when game ends
    onGameComplete: (score: Int, totalQuestions: Int) -> Unit = { _, _ -> }
) {

    // Observe state flows from the QuizService
    val score by quizService.score.collectAsState(initial = 0)
    val questionIndex by quizService.questionIndex.collectAsState(initial = 0)
    val totalQuestions by quizService.totalQuestions.collectAsState(initial = 0)
    val error by quizService.error.collectAsState(initial = null as String?)


    // Get the current question
    val currentQuestion by produceState<TriviaQuestion?>(initialValue = null, quizService) {
        // Whenever the service updates, fetch the next question if needed
        quizService.currentQuestion.collect { question ->
            if (question == null) {
                // Fetch next question from backend
                try {
                    quizService.fetchNextQuestion()
                } catch (e: Exception) {
                    // Optionally handle error
                }
            }
            value = question
        }
    }

    // Track which answer the user has currently selected
    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    // Fetch the first question if not already loaded
    LaunchedEffect(currentQuestion) {
        if (currentQuestion == null) {
            quizService.fetchNextQuestion()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ... background image and overlay ...

        when {
            // --- ERROR STATE ---
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

            // --- QUESTION DISPLAY ---
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

                    // Show question number
                    Text("Question ${questionIndex} of $totalQuestions", color = Color.White)
                    // Question card displaying question text
                    QuestionCard(question.questionText)

                    // Display answer options as buttons
                    options.forEach { answer ->
                        AnswerButton(
                            text = answer,
                            isSelected = (answer == selectedAnswer),
                            onClick = { selectedAnswer = answer },
                            backgroundColor = Color(0xCCB39DDB)
                        )
                    }

                    // Submit button
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

            // --- QUIZ COMPLETE STATE ---
            else -> {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Show final score
                    Text("Quiz Complete! Score: $score / $totalQuestions")

                    // Back button
                    Button(onClick = onNavigateBack) { Text("Back") }
                }
            }
        }
    }
}
