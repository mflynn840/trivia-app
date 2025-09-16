package com.example.co_opapp.Service

import com.example.co_opapp.data_model.GameState
import com.example.co_opapp.data_model.GameRoom
import com.example.co_opapp.data_model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.InetAddress

class RaceModeGameService {

    // Currently connected player
    private var myPlayer: Player? = null

    // Connection and game state
    private val _gameState = MutableStateFlow<GameRoom?>(null)
    val gameState: StateFlow<GameRoom?> = _gameState.asStateFlow()

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // List of all lobbies
    private val _lobbies = MutableStateFlow<List<GameRoom>>(emptyList())
    val lobbies: StateFlow<List<GameRoom>> = _lobbies.asStateFlow()

    init {
        // Create 4 empty lobbies at startup
        _lobbies.value = (1..4).map { id ->
            GameRoom(
                id = id.toLong(),
                players = mutableListOf(),
                maxPlayers = 4,
                gameState = GameState.WAITING_FOR_PLAYERS
            )
        }
    }

    fun getMyPlayer(): Player? = myPlayer

    fun getLocalIpAddress(): String? = try {
        InetAddress.getLocalHost().hostAddress
    } catch (e: Exception) { null }

    fun startHosting(player: Player, onSuccess: () -> Unit, onError: () -> Unit) {
        myPlayer = player
        // Pick the first empty lobby to host
        val lobby = _lobbies.value.firstOrNull { it.players.isEmpty() }
        if (lobby != null) {
            player.isHost = true
            lobby.players.add(player)
            _gameState.value = lobby
            onSuccess()
        } else {
            _errorMessage.value = "No empty lobby available"
            onError()
        }
    }

    fun joinLobby(player: Player, lobbyId: Long, onSuccess: () -> Unit, onError: () -> Unit) {
        val lobby = _lobbies.value.find { it.id == lobbyId }
        if (lobby != null && lobby.players.size < lobby.maxPlayers) {
            player.isHost = false
            lobby.players.add(player)
            myPlayer = player
            _gameState.value = lobby
            onSuccess()
        } else {
            _errorMessage.value = "Lobby full or not found"
            onError()
        }
    }

    fun setPlayerReady(isReady: Boolean) {
        val player = myPlayer ?: return
        player.isReady = isReady
        _gameState.value = _gameState.value // trigger update
    }

    val isHost: Boolean
        get() = myPlayer?.isHost == true

    fun startTriviaGame() {
        _gameState.value?.gameState = GameState.IN_PROGRESS
        _gameState.value = _gameState.value
    }
}
