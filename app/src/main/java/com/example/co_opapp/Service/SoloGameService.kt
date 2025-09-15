class SoloGameService : QuizService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.4.21:8080/")
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
            if (response.isSuccessful && response.body() != null) {
                _currentQuestion.value = response.body()
                _questionIndex.value += 1
                _totalQuestions.value += 1
            } else {
                _error.value = "Failed to load question: ${response.message()}"
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    override suspend fun submitAnswer(answer: String): Boolean {
        val question = _currentQuestion.value ?: return false
        return try {
            val response = api.checkAnswer(AnswerRequest(question.id, answer))
            val correct = response.body()?.correct ?: false
            if (correct) _score.value += 1
            _currentQuestion.value = null
            correct
        } catch (e: Exception) {
            _error.value = e.message
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
