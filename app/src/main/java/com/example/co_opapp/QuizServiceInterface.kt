interface QuizService {
    val currentQuestion: StateFlow<TriviaQuestion?>   // Current question
    val score: StateFlow<Int>                         // Current score
    val questionIndex: StateFlow<Int>                 // Current question number
    val totalQuestions: StateFlow<Int>               // Total questions attempted
    val error: StateFlow<String?>                    // Error state

    suspend fun fetchNextQuestion()
    suspend fun submitAnswer(answer: String): Boolean
    fun resetGame()
}
