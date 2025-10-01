package com.example.co_opapp.ui.layouts

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.co_opapp.Interfaces.GameDriver
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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) { quizService.fetchNextQuestions() }

    QuizBackground(onBack = onNavigateBack, modifier = modifier) {
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

