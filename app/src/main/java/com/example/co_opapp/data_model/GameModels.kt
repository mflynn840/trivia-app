package com.example.co_opapp.data_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap

enum class GameStatus {
    WAITING,
    IN_PROGRESS,
    FINISHED,
    WAITING_FOR_PLAYERS,
    WAITING_FOR_READY
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
    val gameStatus: GameStatus = GameStatus.WAITING_FOR_PLAYERS,
    val gameState: MutableState<GameState>,
)

data class LobbyDTO(
    val name: String,
    val maxPlayers: Int=4,
    val players: Map<String, PlayerDTO> = emptyMap(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val gameStatus: GameStatus = GameStatus.WAITING_FOR_PLAYERS,
    val gameState: GameStateDTO
)

data class CreateLobbyRequest(
    val name: String
)

data class GameState(
    val questions: SnapshotStateList<TriviaQuestion>,
    val questionIdx: MutableState<Int>,
    val gameStatus: GameStatus = GameStatus.WAITING_FOR_PLAYERS
)

data class GameStateDTO(
    val questions: List<TriviaQuestion> = emptyList(),
    val questionIdx: Int = 0,
    val gameStatus: GameStatus = GameStatus.WAITING_FOR_PLAYERS

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


//reactive wrappers
fun LobbyDTO.toLobby(): Lobby {
    val lobby = Lobby(
        name = name,
        maxPlayers = maxPlayers,
        players = mutableStateMapOf<String, PlayerDTO>().apply { putAll(players) },
        chatMessages = mutableStateListOf<ChatMessage>().apply { addAll(chatMessages) },
        gameState = mutableStateOf( gameState.toGameState()) // fallback
    )
    return lobby
}

fun GameStateDTO.toGameState(): GameState {
    return GameState(
        questions = mutableStateListOf<TriviaQuestion>().apply { addAll(questions) },
        questionIdx = mutableIntStateOf(questionIdx),
        gameStatus = gameStatus
    )
}






