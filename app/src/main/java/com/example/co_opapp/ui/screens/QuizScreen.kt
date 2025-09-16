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



/*
 Draw the main gameplay loops UI screen
 Update the game state using the passed in GameDriver
 */
@Composable
fun QuizScreen(
    quizService: GameDriver,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onGameComplete: (score: Int, totalQuestions: Int) -> Unit = { _, _ -> }
) {

    // use the quiz service to initilize and manage these game state variables
    val score by quizService.score.collectAsState(initial = 0)
    val questionIndex by quizService.questionIndex.collectAsState(initial = 0)
    val totalQuestions by quizService.totalQuestions.collectAsState(initial = 0)
    val error by quizService.error.collectAsState(initial = null as String?)
    val currentQuestion by quizService.currentQuestion.collectAsState(initial = null)


    //manage this widgets state using these variables in house
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    //start after first set of questions is fetched
    LaunchedEffect(Unit) { quizService.fetchNextQuestions() }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            // --- QUESTION DISPLAY ---
            currentQuestion != null -> {
                //find a non null current question
                val question = currentQuestion!!

                // Make sure to pass the full question object to QuestionCard
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text("Question ${questionIndex + 1} of $totalQuestions", color = Color.White)
                    QuestionCard(question = currentQuestion!!.body)

                    //create an answer button for each question option
                    val options = listOf(
                        question.optionA,
                        question.optionB,
                        question.optionC,
                        question.optionD
                    )
                    options.forEach { answer ->
                        AnswerButton(
                            text = answer,
                            isSelected = (answer == selectedAnswer),
                            onClick = { selectedAnswer = answer },
                            backgroundColor = Color(0xCCB39DDB)
                        )
                    }

                    //create a submit button
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

            // --- QUIZ COMPLETE STATE ---
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Quiz Complete! Score: $score / $totalQuestions")

                    //main menu button
                    Button(onClick = onNavigateBack) { Text("Back") }

                    //reset game button
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
