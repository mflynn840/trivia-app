package com.example.co_opapp.Service.Backend

import com.example.co_opapp.Interfaces.GameDriver
import com.example.co_opapp.Repository.TriviaRepository
import com.example.co_opapp.data_model.AnswersRequest
import com.example.co_opapp.data_model.TriviaQuestion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SoloGameService(
    private val authService: AuthService,
    private val category: String,
    private val difficulty: String,
    private val repository: TriviaRepository = TriviaRepository()
) : GameDriver {

    private val _allQuestions = MutableStateFlow<List<TriviaQuestion>>(emptyList())
    private val _selectedAnswers = MutableStateFlow<List<String>>(emptyList())
    private val _score = MutableStateFlow(0)
    private val _curQuestionIndex = MutableStateFlow(0)
    private val _numQuestions = MutableStateFlow(0)
    private val _error = MutableStateFlow<String?>(null)

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override val currentQuestion: StateFlow<TriviaQuestion?> = _allQuestions
        .combine(_curQuestionIndex) { questions, index -> questions.getOrNull(index) }
        .stateIn(scope, SharingStarted.Companion.Eagerly, null)

    override val score: StateFlow<Int> = _score.asStateFlow()
    override val questionIndex: StateFlow<Int> = _curQuestionIndex.asStateFlow()
    override val totalQuestions: StateFlow<Int> = _numQuestions.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    override suspend fun fetchNextQuestions() {
        val token = authService.getJwtToken() ?: return
        try {
            val response = repository.getRandomQuestions(5, category, difficulty, "Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let {
                    _allQuestions.value = it
                    _numQuestions.value = it.size
                    _error.value = null
                } ?: run { _error.value = "No questions received" }
            } else {
                _error.value = "Failed to fetch questions: ${response.code()}"
            }
        } catch (e: Exception) {
            _error.value = "Exception: ${e.localizedMessage}"
        }
    }

    override suspend fun submitAnswer(answer: String) {
        val question = _allQuestions.value.getOrNull(_curQuestionIndex.value) ?: return
        _selectedAnswers.value = _selectedAnswers.value + answer
        _curQuestionIndex.value += 1

        if (_selectedAnswers.value.size == _allQuestions.value.size) {
            submitAnswers(_selectedAnswers.value)
        }
    }

    override suspend fun submitAnswers(answers: List<String>): List<Boolean> {
        val token = authService.getJwtToken() ?: return List(answers.size) { false }
        return try {
            val questionIds = _allQuestions.value.map { it.id }
            val response = repository.checkAnswers(AnswersRequest(questionIds, answers), "Bearer $token")
            if (response.isSuccessful) {
                val results = response.body()!!
                _score.value = results.corrects.count { it }
                _error.value = null
                results.corrects
            } else {
                _error.value = "Failed to submit answers: ${response.code()}"
                List(answers.size) { false }
            }
        } catch (e: Exception) {
            _error.value = "Exception: ${e.localizedMessage}"
            List(answers.size) { false }
        }
    }

    override fun resetGame() {
        _allQuestions.value = emptyList()
        _selectedAnswers.value = emptyList()
        _score.value = 0
        _curQuestionIndex.value = 0
        _numQuestions.value = 0
        _error.value = null
    }
}