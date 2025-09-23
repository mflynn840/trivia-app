package com.example.co_opapp.Service.Coop

import android.util.Log
import androidx.compose.runtime.*
import com.example.co_opapp.Service.Backend.WebSocketClientManager
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.PlayerDTO

class LobbyListService(
    private val wsManager: WebSocketClientManager
) {
    companion object {
        private const val TAG = "LobbyListService"
    }

    private val _lobbies = mutableStateOf<List<Lobby>>(emptyList())
    val lobbies: State<List<Lobby>> get() = _lobbies

    private val _isConnected = mutableStateOf(false)
    val isConnected: Boolean get() = _isConnected.value

    fun connect() {
        Log.d(TAG, "Attempting to connect to WebSocket…")
        wsManager.connect {
            _isConnected.value = true
            Log.i(TAG, "WebSocket connected")

            // Subscribe to all lobbies, update the server list
            wsManager.subscribeLobbyAll { map ->
                _lobbies.value = map.values.toList()
                Log.d(TAG, "Received ${_lobbies.value.size} lobbies from backend")
                _lobbies.value.forEach { lobby ->
                    Log.d(TAG, "Lobby: $lobby")
                }
            }

            Log.d(TAG, "Requesting all lobbies from backend")
            wsManager.send("/app/lobby/getAll", "")
        }
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting from WebSocket…")
        wsManager.disconnect()
        _isConnected.value = false
        Log.i(TAG, "WebSocket disconnected")
    }

    fun createLobby(name: String) {
        Log.d(TAG, "Creating lobby with name=$name")
        wsManager.send("/app/lobby/create", mapOf("name" to name))
    }

    fun toggleReady(lobbyName: String, player: PlayerDTO) {
        Log.d(TAG, "Toggling ready for player=${player.username} in lobby=$lobbyName")
        wsManager.send("/app/lobby/ready/$lobbyName", player)
    }
}
