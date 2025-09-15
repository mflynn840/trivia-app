package com.example.co_opapp

import java.util.UUID

// Game state models
data class Player(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val isHost: Boolean = false,
    val isReady: Boolean = false
)

data class GameRoom(
    val id: String = UUID.randomUUID().toString(),
    val hostId: String,
    val players: List<Player> = emptyList(),
    val maxPlayers: Int = 4,
    val isGameStarted: Boolean = false,
    val currentPlayerIndex: Int = 0,
    val gameState: GameState = GameState.WAITING_FOR_PLAYERS
)

enum class GameState {
    WAITING_FOR_PLAYERS,
    READY_TO_START,
    IN_PROGRESS,
    FINISHED
}

data class GameAction(
    val playerId: String,
    val actionType: ActionType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ActionType {
    PLAYER_JOINED,
    PLAYER_LEFT,
    PLAYER_READY,
    GAME_STARTED,
    TURN_COMPLETED,
    GAME_ENDED
}

// Socket events
object SocketEvents {
    const val CONNECT = "connect"
    const val DISCONNECT = "disconnect"
    const val JOIN_ROOM = "join_room"
    const val LEAVE_ROOM = "leave_room"
    const val PLAYER_JOINED = "player_joined"
    const val PLAYER_LEFT = "player_left"
    const val PLAYER_READY = "player_ready"
    const val START_GAME = "start_game"
    const val TURN_COMPLETED = "turn_completed"
    const val GAME_UPDATE = "game_update"
    const val ERROR = "error"
}
