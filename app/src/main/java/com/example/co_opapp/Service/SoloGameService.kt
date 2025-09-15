package com.example.co_opapp.Service

import com.example.co_opapp.data_model.AnswerRequest
import com.example.co_opapp.data_model.TriviaQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Retrofit API interface for solo game
interface SoloGameApi {
    @GET("api/game/questions/random")
    suspend fun getRandomQuestion(): Response<TriviaQuestion>

    @POST("api/game/questions/check-answer")
    suspend fun checkAnswer(@Body answerRequest: AnswerRequest): Response<AnswerResponse>
}

// Response from backend for answer check
data class AnswerResponse(
    val correct: Boolean
)

// QuizService interface to unify solo and coop usage
interface QuizService {
    val currentQuestion: StateFlow<TriviaQuestion?>
    val score: StateFlow<Int>
    val questionIndex: StateFlow<Int>
    val totalQuestions: StateFlow<Int>
    val error: StateFlow<String?>

    suspend fun fetchNextQuestion()
    suspend fun submitAnswer(answer: String): Boolean
    fun resetGame()
}

// SoloGameService implementation
class SoloGameService : QuizService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.4.21:8080/") // replace with your backend URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(SoloGameApi::class.java)

    private val _currentQuestion = MutableStateFlow<TriviaQuestion?>(null)
    override val currentQuestion: StateFlow<TriviaQuestion?> = _currentQuestion.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int> = _score.asStateFlow()

    private val _questionIndex = MutableStateFlow(0)
    override val questionIndex: StateFlow<Int> = _questionIndex.asStateFlow()

    private val _totalQuestions = MutableStateFlow(0)
    override val totalQuestions: StateFlow<Int> = _totalQuestions.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    override suspend fun fetchNextQuestion() {
        try {
            val response = api.getRandomQuestion()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    _currentQuestion.value = body
                    _questionIndex.value += 1
                    if (_totalQuestions.value < _questionIndex.value) {
                        _totalQuestions.value = _questionIndex.value
                    }
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

    override suspend fun submitAnswer(answer: String): Boolean {
        val question = _currentQuestion.value ?: return false
        return try {
            val response = api.checkAnswer(AnswerRequest(question.id, answer))
            val correct = response.body()?.correct ?: false
            if (correct) {
                _score.value += 1
            }
            _currentQuestion.value = null
            _error.value = null
            correct
        } catch (e: Exception) {
            _error.value = "Exception: ${e.localizedMessage}"
            false
        }
    }

    override fun resetGame() {
        _currentQuestion.value = null
        _score.value = 0
        _questionIndex.value = 0
        _totalQuestions.value = 0
        _error.value = null
    }
}
