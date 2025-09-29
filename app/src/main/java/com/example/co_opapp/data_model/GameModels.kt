package com.example.co_opapp.data_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap

enum class GameStatus {
    WAITING,
}


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

data class Player(
    val id: Long,
    val username: String,
    val score: Long = 0,
    var ready: Boolean = false,
    //var sessionId: String,
    //var profilePicture: ByteArray,
)

data class PlayerDTO(
    //val sessionId: String,
    val username: String,
    val isReady: Boolean = false,
    val id: Long,
    val score: Long,
    //val profilePicture: ByteArray
)

data class ChatMessage(
    val username: String,
    val message: String
)

data class Lobby(
    val name: String,
    val maxPlayers: Int = 4,
    val players: SnapshotStateMap<String, PlayerDTO> = mutableStateMapOf(),
    val chatMessages: SnapshotStateList<ChatMessage> = mutableStateListOf(),
    val gameStatus: GameStatus = GameStatus.WAITING,
    val gameState: MutableState<GameState>,
)

data class CreateLobbyRequest(
    val name: String
)

data class GameState(
    val questions: SnapshotStateList<TriviaQuestion>,
    val currentQuestion: MutableState<Int>
)

data class AnswersRequest(val questionIds: List<Long>, val answers: List<String>)
data class AnswersResponse(val corrects: List<Boolean>, val correctAnswers: List<String>)


// --- User/Auth ---
data class UserCredentials(val username: String, val password: String)
data class LoginResponse(
    val token: String,
    val user: PlayerDTO
)

fun Player.toDTO(): PlayerDTO {
    return PlayerDTO(
        //sessionId = this.sessionId,
        username = this.username,
        isReady = this.ready,
        id = this.id,
        score = this.score,
        //profilePicture = this.profilePicture
    )
}


