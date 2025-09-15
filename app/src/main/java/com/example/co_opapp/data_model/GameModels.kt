package com.example.co_opapp.data_model

// --- User/Auth ---
data class UserCredentials(val username: String, val password: String)
data class LoginResponse(val token: String, val username: String, val role: String)


enum class GameState { WAITING, IN_PROGRESS, FINISHED, WAITING_FOR_PLAYERS }

data class GameRoom(
    val hostId: String,
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
    val id: String,
    val questionText: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String
)

// LAN-friendly player model
data class Player(
    val id: String,
    val username: String,
    val score: Int = 0,
    val isHost: Boolean = false
)

// Question model for LAN game
data class Question(
    val id: String,
    val questionText: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String
)

data class AnswerRequest(val questionId: Long, val answer: String)
data class AnswerResponse(val correct: Boolean, val correctAnswer: String)
