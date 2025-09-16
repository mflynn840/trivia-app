package com.example.co_opapp.Service

import android.util.Log
import com.example.co_opapp.data_model.AnswerRequest
import com.example.co_opapp.data_model.TriviaQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.co_opapp.Interface.BackendQuestionApi
import com.example.co_opapp.Interface.GameDriver
import com.example.co_opapp.data_model.AnswersRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job


// Response from backend for answer check
data class AnswerResponse(
    val correct: List<Boolean> //change this to a list
)


/* Handle the logic of running a solo trivia game
    1. SETUP: Ping the backend for 5 trivia questions
    2. yield each of the 5 trivia questions to the UI for answering questions
    3. store the results of each of the 5 selected answers
    4. when all 5 questions have been answered, send the responses to the backend to see how many were correct
    5. get the response and use it to update the game to show the ending screen
 */
class SoloGameService : GameDriver {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.4.21:8080/") // replace with your backend URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(BackendQuestionApi::class.java)

    // State variables
    val _allQuestions = MutableStateFlow<List<TriviaQuestion>>(emptyList())
    private val _selectedAnswers = MutableStateFlow<List<String>>(emptyList())
    private val _score = MutableStateFlow(0)
    private val _curQuestionIndex = MutableStateFlow(0)
    private val _numQuestions = MutableStateFlow(0)
    private val _error = MutableStateFlow<String?>(null)

    // Create a CoroutineScope for the service
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)


    // The current question is based on the current question index
    override val currentQuestion: StateFlow<TriviaQuestion?> = _allQuestions
        .combine(_curQuestionIndex) { allQuestions, index ->
            allQuestions.getOrNull(index)  // Get the question at the current index
        }.stateIn(scope, started = SharingStarted.Eagerly, initialValue = null)


    override val score: StateFlow<Int> = _score.asStateFlow()
    override val questionIndex: StateFlow<Int> = _curQuestionIndex.asStateFlow()
    override val totalQuestions: StateFlow<Int> = _numQuestions.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    // Fetch n questions from the backend
    override suspend fun fetchNextQuestions() {
        try {
            val response = api.getRandomQuestions(5)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isNotEmpty()) {

                    _allQuestions.value = body
                    _numQuestions.value = body.size
                    _error.value = null
                } else {
                    _error.value = "Received empty question from server"
                }
            } else {
                _error.value = "Failed to load question: ${response.code()} ${response.message()}"
            }
        } catch (e: Exception) {
            _error.value = "Exception: ${e.localizedMessage}"
        }
    }

    // Submit the selected answer and move to the next question
    override suspend fun submitAnswer(answer: String) {
        val question = _allQuestions.value.getOrNull(_curQuestionIndex.value)

        // If the question exists, store the answer
        if (question != null) {
            val updatedAnswers = _selectedAnswers.value + answer
            _selectedAnswers.value = updatedAnswers

            // Increment the question index to go to the next question
            _curQuestionIndex.value += 1

            // If all questions have been answered, submit them to the backend
            if (_selectedAnswers.value.size == _allQuestions.value.size) {
                submitAnswers(_selectedAnswers.value)
            }
        }
    }

    // Submit answers to the backend
    override suspend fun submitAnswers(answers: List<String>) : List<Boolean> {
        try {
            val questionIds = _allQuestions.value.map { it.id }

            val answersRequest = AnswersRequest(
                questionIds = questionIds,
                answers = answers
            )

            val response = api.checkAnswers(answersRequest)

            if (response.isSuccessful) {
                val answerResults = response.body()!!
                _score.value = answerResults.corrects.count { it }
                _error.value = null
                return answerResults.corrects
            } else {
                _error.value = "Failed to submit answers: ${response.code()} ${response.message()}"
                return List(answers.size) { false }
            }
        } catch (e: Exception) {
            _error.value = "Exception: ${e.localizedMessage}"
            return List(answers.size) { false }

        }
    }

    // Reset the game state
    override fun resetGame() {
        _allQuestions.value = emptyList()
        _selectedAnswers.value = emptyList()
        _score.value = 0
        _curQuestionIndex.value = 0
        _numQuestions.value = 0
        _error.value = null
    }
}
