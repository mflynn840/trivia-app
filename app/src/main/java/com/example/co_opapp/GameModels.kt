package com.example.co_opapp

// --- User/Auth ---
data class UserCredentials(val username: String, val password: String)
data class LoginResponse(val token: String, val username: String, val role: String)

// --- Player / Game ---
data class Player(
    val id: String = "",
    val username: String,
    val score: Int = 0,
    val isHost: Boolean = false,
    val isReady: Boolean = false
)

enum class GameState { WAITING, IN_PROGRESS, FINISHED }

data class GameRoom(
    val hostId: String,
    val players: List<Player>,
    val isGameStarted: Boolean = false,
    val gameState: GameState = GameState.WAITING,
    val currentPlayerIndex: Int = 0,
    val currentRound: Int = 0,
    val currentQuestion: TriviaQuestion? = null
)

// --- Trivia ---
data class TriviaQuestion(
    val id: Long,
    val question: String,
    val options: List<String>,
    val difficulty: String
)

data class AnswerRequest(val questionId: Long, val answer: String)
data class AnswerResponse(val correct: Boolean, val correctAnswer: String)
