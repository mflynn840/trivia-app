package com.example.co_opapp.Service

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.co_opapp.Interface.GameDriver
import kotlinx.coroutines.launch


//handle the logic for storing/updating the game state data model
class QuizViewService(private val quizService: GameDriver) : ViewModel() {

    // State variables to hold the quiz data
    val score = mutableStateOf(0)
    val questionIndex = mutableStateOf(0)
    val totalQuestions = mutableStateOf(0)
    val error = mutableStateOf<String?>(null)
    val currentQuestion = mutableStateOf<Question?>(null)
    val selectedAnswer = mutableStateOf<String?>(null)

    init {
        // Fetch the first question when the ViewModel is created
        fetchNextQuestion()
    }

    fun fetchNextQuestion() {
        viewModelScope.launch {
            try {
                quizService.fetchNextQuestion()
                val question = quizService.currentQuestion.value
                currentQuestion.value = question
                totalQuestions.value = quizService.totalQuestions.value
                questionIndex.value = quizService.questionIndex.value
            } catch (e: Exception) {
                error.value = "Error fetching question: ${e.message}"
            }
        }
    }

    fun submitAnswer(answer: String) {
        viewModelScope.launch {
            quizService.submitAnswer(answer)
            fetchNextQuestion()
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            quizService.resetGame()
            fetchNextQuestion()
        }
    }
}
