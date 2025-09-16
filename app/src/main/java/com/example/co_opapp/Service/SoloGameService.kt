package com.example.co_opapp.Service

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

    //use retrofit to create an instance of the backend API
    private val api = retrofit.create(BackendQuestionApi::class.java)


    //Create variables for questions for this run
    private val _questions = MutableStateFlow<List<TriviaQuestion>>(emptyList())
    override val currentQuestion: StateFlow<TriviaQuestion?> = _questions.asStateFlow()

    //Create variables for user selected answers for this run
    private val _selectedAnswers = MutableStateFlow<List<String>>(emptyList())
    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int> = _score.asStateFlow()

    private val _questionIndex = MutableStateFlow(0)
    override val questionIndex: StateFlow<Int> = _questionIndex.asStateFlow()

    private val _totalQuestions = MutableStateFlow(0)
    override val totalQuestions: StateFlow<Int> = _totalQuestions.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()


    // Fetch 5 questions from the backend
    override suspend fun fetchNextQuestion() {
        try {
            val response = api.getRandomQuestions(5)
            if(response.isSuccessful){
                val body = response.body()
                if(body != null && body.isNotEmpty()) {
                    _questions.value = body
                    _totalQuestions.value = body.size
                    _error.value = null
                }else {
                    _error.value = "Received empty question from server"
                }
            } else {
                _error.value = "Failed to load question: ${response.code()} ${response.message()}"
            }
        } catch (e: Exception) {
            _error.value = "Exception: ${e.localizedMessage}"
        }
    }

    //submit an answer and store it in the list of selected answers
    override suspend fun submitAnswer(answer: String) {
        val question = _questions.value.getOrNull(_questionIndex.value - 1)

        // Store the answer
        val updatedAnswers = _selectedAnswers.value + answer
        _selectedAnswers.value = updatedAnswers

        // if all questions have been answered submit it to the backend
        if (_selectedAnswers.value.size == 5) {
            submitAnswers(_selectedAnswers.value)
        }

    }

    // Send the selected answers to the backend to tally the score
    override suspend fun submitAnswers(answers: List<String>) : List<Boolean> {
        try {

            //collect data fro the backend api call
            val questionIds = _questions.value.map {it.id}
            val answers = _selectedAnswers.value

            //construct a backend call DTO
            val answersRequest = AnswersRequest(
                questionIds = questionIds,
                answers = answers
            )


            val response = api.checkAnswers(answersRequest)

            // Handle backend response to update the score based on correct answers
            if (response.isSuccessful) {
                val answerResults = response.body()!! //await response

                //process the response
                _score.value = answerResults.corrects.count { it }
                _error.value = null
            } else {
                _error.value = "Failed to submit answers: ${response.code()} ${response.message()}"
            }
        } catch (e: Exception) {
            _error.value = "Exception: ${e.localizedMessage}"
        }
    }

    override fun resetGame() {
        _questions.value = emptyList()
        _selectedAnswers.value = emptyList()
        _score.value = 0
        _questionIndex.value = 0
        _totalQuestions.value = 0
        _error.value = null
    }
}
