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

// Simple GameRoom model
data class GameRoom(
    val players: MutableList<Player>,
    val maxPlayers: Int = 4,
    var gameState: GameState = GameState.WAITING_FOR_PLAYERS,
    var id: Long
)


// --- Trivia ---
// LAN-friendly question model
data class TriviaQuestion(
    val id: Long,
    val body: String,
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
    var isHost: Boolean = false,
    var isReady: Boolean = false
)

data class AnswerRequest(val questionId: Long, val answer: String)
data class AnswerResponse(val correct: Boolean, val correctAnswer: String)

data class AnswersRequest(val questionIds: List<Long>, val answers: List<String>)
data class AnswersResponse(val corrects: List<Boolean>, val correctAnswers: List<String>)

