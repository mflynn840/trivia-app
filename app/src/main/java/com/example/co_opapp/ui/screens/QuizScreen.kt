package com.example.co_opapp.ui.screens

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
import com.example.co_opapp.ui.components.AnswerButton
import com.example.co_opapp.ui.components.Question
import com.example.co_opapp.ui.components.QuestionCard
import com.example.co_opapp.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    isSinglePlayer: Boolean = true,
    onNavigateBack: () -> Unit = {},
    onGameComplete: (score: Int, totalQuestions: Int) -> Unit = { _, _ -> }
) {
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var submittedAnswers by remember { mutableStateOf(mutableListOf<String>()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableStateOf(0) }
    var retryKey by remember { mutableStateOf(0) }

    // Load questions from backend
    LaunchedEffect(retryKey) {
        try {
            isLoading = true
            error = null
            val loadedQuestions = loadQuestionsFromBackend()
            questions = loadedQuestions
            isLoading = false
        } catch (e: Exception) {
            error = "Failed to load questions: ${e.message}"
            isLoading = false
        }
    }

    // Calculate score when game ends
    LaunchedEffect(submittedAnswers, questions) {
        if (currentIndex >= questions.size && questions.isNotEmpty()) {
            val correctAnswers = submittedAnswers.zip(questions).count { (userAnswer, question) ->
                userAnswer == question.correctAnswer
            }
            score = correctAnswers
            onGameComplete(score, questions.size)
        }
    }

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
        when {
            isLoading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading questions...", style = MaterialTheme.typography.bodyLarge)
                }
            }
            
            error != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = error!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { 
                        retryKey++
                        currentIndex = 0
                        submittedAnswers.clear()
                        selectedAnswer = null
                    }) {
                        Text("Retry")
                    }
                    Button(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            }
            
            currentIndex < questions.size -> {
                val currentQuestion = questions[currentIndex]

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header with back button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Back")
                        }
                        
                        Text(
                            text = "Question ${currentIndex + 1} of ${questions.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.width(80.dp)) // Balance the back button
                    }

                    // Question card
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
            }
            
            else -> {
                // End of quiz: show results
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(
                        "Quiz Completed! 🎉", 
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    
                    Text(
                        "Your Score: $score/${questions.size}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Show detailed results
                    questions.forEachIndexed { index, question ->
                        val userAnswer = submittedAnswers.getOrNull(index) ?: ""
                        val isCorrect = userAnswer == question.correctAnswer
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect) 
                                    Color(0xFFE8F5E8) 
                                else 
                                    Color(0xFFFFEBEE)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    "Q${index + 1}: ${question.text}", 
                                    fontSize = 16.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                                )
                                Text(
                                    "Your answer: $userAnswer", 
                                    fontSize = 14.sp,
                                    color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                                )
                                Text(
                                    "Correct answer: ${question.correctAnswer}", 
                                    fontSize = 14.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Menu")
                    }
                }
            }
        }
    }
}

// Function to load questions from backend
private suspend fun loadQuestionsFromBackend(): List<Question> = withContext(Dispatchers.IO) {
    try {
        // Try to load random questions from backend
        val response = RetrofitClient.questionApi.getRandomQuestions(5)
        if (response.isSuccessful) {
            val questionResponses = response.body()
            if (!questionResponses.isNullOrEmpty()) {
                return@withContext questionResponses.map { questionResponse ->
                    val answers = listOf(
                        questionResponse.optionA,
                        questionResponse.optionB,
                        questionResponse.optionC,
                        questionResponse.optionD
                    ).shuffled()
                    
                    Question(
                        text = questionResponse.question,
                        answers = answers,
                        correctAnswer = questionResponse.correctAnswer
                    )
                }
            }
        }
        
        // Fallback to sample questions if backend fails
        getSampleQuestions()
    } catch (e: Exception) {
        // Fallback to sample questions if backend is not available
        getSampleQuestions()
    }
}

private fun getSampleQuestions(): List<Question> {
    return listOf(
        Question("What is 2 + 2?", listOf("2", "3", "4", "5"), "4"),
        Question("Capital of France?", listOf("Berlin", "Madrid", "Paris", "Rome"), "Paris"),
        Question("Which planet is the Red Planet?", listOf("Venus", "Mars", "Jupiter", "Saturn"), "Mars"),
        Question("What is the largest mammal?", listOf("Elephant", "Blue Whale", "Giraffe", "Hippo"), "Blue Whale"),
        Question("Who painted the Mona Lisa?", listOf("Van Gogh", "Picasso", "Da Vinci", "Michelangelo"), "Da Vinci")
    )
}
