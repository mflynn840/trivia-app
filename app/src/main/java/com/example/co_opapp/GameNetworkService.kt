package com.example.co_opapp

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class GameNetworkService {
    private var socket: Socket? = null
    var isHost = false
        private set
    private var currentPlayer: Player? = null
    private var currentRoom: GameRoom? = null
    
    private val _gameState = MutableStateFlow<GameRoom?>(null)
    val gameState: StateFlow<GameRoom?> = _gameState.asStateFlow()
    
    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is java.net.Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Error getting IP address", e)
        }
        return null
    }
    
    fun startHosting(player: Player, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = true
            currentPlayer = player.copy(isHost = true)
            
            // For LAN, we'll use a simple approach with a local server
            // In a real implementation, you'd need a local server running
            val localIp = getLocalIpAddress()
            if (localIp == null) {
                onError("Could not determine local IP address")
                return
            }
            
            // Create a room
            currentRoom = GameRoom(
                hostId = player.id,
                players = listOf(player.copy(isHost = true))
            )
            
            _gameState.value = currentRoom
            _connectionStatus.value = true
            onSuccess()
            
            Log.d("GameNetworkService", "Hosting game on IP: $localIp")
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Error hosting game", e)
            onError(e.message ?: "Unknown error")
        }
    }
    
    fun joinGame(player: Player, hostIp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = false
            currentPlayer = player
            
            // For simplicity, we'll simulate joining
            // In a real implementation, you'd connect to the host's server
            currentRoom = GameRoom(
                hostId = "host_id", // This would come from the server
                players = listOf(
                    Player(id = "host_id", username = "Host", isHost = true),
                    player
                )
            )
            
            _gameState.value = currentRoom
            _connectionStatus.value = true
            onSuccess()
            
            Log.d("GameNetworkService", "Joined game on IP: $hostIp")
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Error joining game", e)
            onError(e.message ?: "Unknown error")
        }
    }
    
    fun setPlayerReady(isReady: Boolean) {
        currentPlayer?.let { player ->
            val updatedPlayer = player.copy(isReady = isReady)
            currentPlayer = updatedPlayer
            
            // Update room state
            currentRoom?.let { room ->
                val updatedPlayers = room.players.map { 
                    if (it.id == player.id) updatedPlayer else it
                }
                val updatedRoom = room.copy(players = updatedPlayers)
                currentRoom = updatedRoom
                _gameState.value = updatedRoom
            }
        }
    }
    
    fun startGame() {
        if (!isHost) return
        
        currentRoom?.let { room ->
            if (room.players.size >= 2 && room.players.all { it.isReady }) {
                val updatedRoom = room.copy(
                    isGameStarted = true,
                    gameState = GameState.IN_PROGRESS,
                    currentPlayerIndex = 0
                )
                currentRoom = updatedRoom
                _gameState.value = updatedRoom
            }
        }
    }
    
    fun completeTurn() {
        currentRoom?.let { room ->
            if (room.gameState == GameState.IN_PROGRESS) {
                val nextPlayerIndex = (room.currentPlayerIndex + 1) % room.players.size
                val updatedRoom = room.copy(currentPlayerIndex = nextPlayerIndex)
                currentRoom = updatedRoom
                _gameState.value = updatedRoom
            }
        }
    }
    
    fun leaveGame() {
        socket?.disconnect()
        socket = null
        currentPlayer = null
        currentRoom = null
        _gameState.value = null
        _connectionStatus.value = false
    }
    
    fun isCurrentPlayerTurn(): Boolean {
        val room = currentRoom ?: return false
        val player = currentPlayer ?: return false
        return room.currentPlayerIndex < room.players.size && 
               room.players[room.currentPlayerIndex].id == player.id
    }
    
    fun getCurrentPlayer(): Player? = currentRoom?.let { room ->
        if (room.currentPlayerIndex < room.players.size) {
            room.players[room.currentPlayerIndex]
        } else null
    }
    
    fun getCurrentPlayerInfo(): Player? = currentPlayer
    
    fun getMyPlayer(): Player? = currentPlayer
}
