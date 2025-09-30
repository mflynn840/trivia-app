package com.example.co_opapp.ui.layouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.Interfaces.GameDriver
import com.example.co_opapp.R
import com.example.co_opapp.data_model.TriviaQuestion
import com.example.co_opapp.ui.components.QuizScreen.AnswerButton
import com.example.co_opapp.ui.components.QuizScreen.QuestionCard
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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) { quizService.fetchNextQuestions() }

    QuizBackground(onBack = onNavigateBack, modifier = modifier) {
        when {
            currentQuestion != null -> QuestionScreen(
                question = currentQuestion!!,
                questionIndex = questionIndex,
                totalQuestions = totalQuestions,
                selectedAnswer = selectedAnswer,
                onAnswerSelected = { selectedAnswer = it },
                onSubmit = {
                    selectedAnswer?.let { answer ->
                        coroutineScope.launch { quizService.submitAnswer(answer) }
                        selectedAnswer = null
                    }
                }
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
}

@Composable
fun QuestionScreen(
    question: TriviaQuestion,
    questionIndex: Int,
    totalQuestions: Int,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val options = listOf(question.optionA, question.optionB, question.optionC, question.optionD)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        QuestionCard(
            question = question.body,
            questionIndex = questionIndex,
            totalQuestions = totalQuestions
        )

        options.forEach { answer ->
            AnswerButton(
                text = answer,
                isSelected = answer == selectedAnswer,
                onClick = { onAnswerSelected(answer) }
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = onSubmit,
            enabled = selectedAnswer != null,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            )
        ) {
            Text("Submit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ErrorScreen(error: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Error: $error", color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun GameCompleteScreen(score: Int, totalQuestions: Int, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f))
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Quiz Complete!\nScore: $score / $totalQuestions",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Retry", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuizBackground(onBack: () -> Unit, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.city_background),
            contentDescription = "Quiz Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        content()
    }
}
