package com.example.co_opapp.data_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap

data class Lobby(
    val name: String,
    val maxPlayers: Int = 4,
    val players: SnapshotStateMap<String, PlayerDTO> = mutableStateMapOf(),
    val chatMessages: SnapshotStateList<ChatMessage> = mutableStateListOf(),
    val gameStatus: GameStatus = GameStatus.WAITING_FOR_PLAYERS,
    val gameState: MutableState<GameState>,
    val numQuestions: Integer,
    val difficulty: String,
    val category: String
)

data class LobbyDTO(
    val name: String,
    val maxPlayers: Int=4,
    val players: Map<String, PlayerDTO> = emptyMap(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val gameStatus: GameStatus = GameStatus.WAITING_FOR_PLAYERS,
    val gameState: GameStateDTO,
    val numQuestions: Integer,
    val difficulty: String,
    val category: String
)

data class ChatMessage(
    val username: String,
    val message: String
)

data class CreateLobbyRequest(
    val name: String
)

enum class GameStatus {
    WAITING,
    IN_PROGRESS,
    FINISHED,
    WAITING_FOR_PLAYERS,
    WAITING_FOR_READY
}

data class TimerRequest(
    val type: String,
    val questionId: Int,
    val startEpochTime: Long?,
    val durationMs: Long?
)

data class AnswerRequest(
    val roomName: String,
    val username: String,
    val questionId: Long,
    val selectedAnswer: String
)

data class AnswerListResponse(val corrects: List<Boolean>, val correctAnswers: List<String>)
data class AnswerResponse(val correct: Boolean, val correctAnswer: String)
