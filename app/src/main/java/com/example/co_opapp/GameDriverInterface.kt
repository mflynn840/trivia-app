package com.example.co_opapp

import com.example.co_opapp.data_model.TriviaQuestion
import kotlinx.coroutines.flow.StateFlow

//QuizServiceInterface is a common interface that abstracts the functionality both services provide:
// -Single player quiz service (host a singleplayer game)
// -multiplayer quiz service (host a coop game)

interface GameDriver {
    val currentQuestion: StateFlow<TriviaQuestion?>   // Current question
    val score: StateFlow<Int>                         // Current score
    val questionIndex: StateFlow<Int>                 // Current question number
    val totalQuestions: StateFlow<Int>               // Total questions attempted
    val error: StateFlow<String?>                    // Error state

    suspend fun fetchNextQuestion()
    suspend fun submitAnswer(answer: String): Boolean
    fun resetGame()
}
