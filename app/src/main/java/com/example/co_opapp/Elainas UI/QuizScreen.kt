package com.example.trivia_game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trivia_game.AnswerButton
import com.example.trivia_game.QuestionCard
import com.example.trivia_game.Question

@Composable
fun QuizScreen(modifier: Modifier = Modifier) {
    val questions = listOf(
        Question("What is 2 + 2?", listOf("2", "3", "4", "5"), "4"),
        Question("Capital of France?", listOf("Berlin", "Madrid", "Paris", "Rome"), "Paris"),
        Question("Which planet is the Red Planet?", listOf("Venus", "Mars", "Jupiter", "Saturn"), "Mars")
    )

    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var submittedAnswers by remember { mutableStateOf(mutableListOf<String>()) }

    // Gradient background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (currentIndex < questions.size) {
            val currentQuestion = questions[currentIndex]

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Question card with soft color
                QuestionCard(
                    question = currentQuestion.text,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )

                // Answer buttons
                currentQuestion.answers.forEach { answer ->
                    AnswerButton(
                        text = answer,
                        isSelected = (answer == selectedAnswer),
                        onClick = { selectedAnswer = answer }
                    )
                }

                // Submit button
                Button(
                    onClick = {
                        submittedAnswers.add(selectedAnswer ?: "")
                        selectedAnswer = null
                        currentIndex++
                    },
                    enabled = selectedAnswer != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Submit", fontSize = 18.sp)
                }
            }
        } else {
            // End of quiz: show answers and correct answers
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Quiz Completed! 🎉", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                questions.forEachIndexed { index, question ->
                    val userAnswer = submittedAnswers.getOrNull(index) ?: ""
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text("Q: ${question.text}", fontSize = 18.sp)
                        Text("Your answer: $userAnswer", fontSize = 16.sp)
                        Text("Correct answer: ${question.correctAnswer}", fontSize = 16.sp, color = Color(0xFF4CAF50))
                    }
                }
            }
        }
    }
}
