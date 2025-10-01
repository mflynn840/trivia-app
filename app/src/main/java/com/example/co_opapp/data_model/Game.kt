package com.example.co_opapp.data_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap


// --- Trivia game models ---
data class GameState(
    val questions: SnapshotStateList<TriviaQuestion>,
    val questionIdx: MutableState<Int>,
    val gameStatus: GameStatus = GameStatus.WAITING_FOR_PLAYERS,
    val scores: SnapshotStateMap<String, Int>
)

data class GameStateDTO(
    val questions: List<TriviaQuestion> = emptyList(),
    val questionIdx: Int = 0,
    val gameStatus: GameStatus = GameStatus.WAITING_FOR_PLAYERS,
    val scores: Map<String, Int>
)

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



