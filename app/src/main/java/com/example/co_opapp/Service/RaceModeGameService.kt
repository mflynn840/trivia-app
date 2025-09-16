package com.example.co_opapp.Service

import com.example.co_opapp.data_model.GameState
import com.example.co_opapp.data_model.GameRoom

import com.example.co_opapp.data_model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.NetworkInterface

class RaceModeGameService {

    private val _gameState = MutableStateFlow<GameRoom?>(null)
    val gameState: StateFlow<GameRoom?> = _gameState.asStateFlow()

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var isHost = false
        private set

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var myPlayer: Player? = null

    fun getMyPlayer(): Player? = myPlayer

    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (intf in interfaces) {
                val addrs = intf.inetAddresses
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is InetAddress) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (e: Exception) { }
        return null
    }

    fun startHosting(player: Player, onSuccess: () -> Unit, onError: () -> Unit) {
        myPlayer = player.copy(isHost = true)
        isHost = true
        _gameState.value = GameRoom(players = mutableListOf(myPlayer!!))
        _connectionStatus.value = true
        scope.launch { onSuccess() }
    }

    fun joinGame(player: Player, hostIp: String, onSuccess: () -> Unit, onError: () -> Unit) {
        myPlayer = player.copy(isHost = false)
        isHost = false
        _gameState.value?.players?.add(myPlayer!!)
            ?: run { _gameState.value = GameRoom(players = mutableListOf(myPlayer!!)) }
        _connectionStatus.value = true
        scope.launch { onSuccess() }
    }

    fun setPlayerReady(ready: Boolean) {
        myPlayer?.isReady = ready
        _gameState.value?.players?.find { it.id == myPlayer?.id }?.isReady = ready
        // Trigger state update
        _gameState.value = _gameState.value
    }

    fun startTriviaGame() {
        _gameState.value?.gameState = GameState.IN_PROGRESS
        // Trigger state update
        _gameState.value = _gameState.value
    }
}


