package com.example.co_opapp.data_model

// --- User/Auth ---
data class UserCredentials(val username: String, val password: String)
data class LoginResponse(
    val token: String,
    val username: String,
    val role: String,
    val id : Long,
)


enum class GameState {
    WAITING,
    IN_PROGRESS,
    FINISHED,
    WAITING_FOR_PLAYERS
}

data class GameRoom(
    val hostId: Long,
    val players: List<Player>,
    val isGameStarted: Boolean = false,
    val gameState: GameState = GameState.WAITING,
    val currentPlayerIndex: Int = 0,
    val currentRound: Int = 0,
    val currentQuestion: TriviaQuestion? = null,
    val maxPlayers: Int = 4,
    val maxRounds: Int=5
)

// --- Trivia ---
// LAN-friendly question model
data class TriviaQuestion(
    val id: Long,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val category: String,
    val difficulty: String,
    val type: String,
)

// LAN-friendly player model
data class Player(
    val id: Long,
    val username: String,
    val score: Int = 0,
    val isHost: Boolean = false,
    val isReady: Boolean = false
)

data class AnswerRequest(val questionId: Long, val answer: String)
data class AnswerResponse(val correct: Boolean, val correctAnswer: String)

data class AnswersRequest(val questionIds: List<Long>, val answers: List<String>)
data class AnswersResponse(val corrects: List<Boolean>, val correctAnswers: List<String>)

