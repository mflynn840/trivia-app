package com.example.co_opapp.data_model

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

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
        gameStatus = gameStatus,
        scores = mutableStateMapOf<String, Int>().apply { putAll(scores) }
    )
}


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