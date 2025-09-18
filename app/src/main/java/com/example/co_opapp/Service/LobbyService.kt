package com.example.co_opapp.Service

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.PlayerDTO

/**
 *
 * High level UI-facing API for interacting with the backends websocket
 */

class LobbyService(
    backendUrl: String = "ws://192.168.4.21:8080/ws"
) {

    private val wsManager = WebSocketClientManager(backendUrl)

    // Expose UI-friendly state
    private val _lobbies = mutableStateOf<List<Lobby>>(emptyList())
    val lobbies: State<List<Lobby>> get() = _lobbies

    private val _selectedLobbyChats = mutableStateOf<Map<String, List<String>>>(emptyMap())
    val lobbyChats: State<Map<String, List<String>>> get() = _selectedLobbyChats


    //use mutableStateOf to ensure variables are reactive
    private val _isConnected = mutableStateOf(false)
    val isConnected: Boolean get() = _isConnected.value

    fun connect() {
        wsManager.connect {
            //when connected update flag and subscribe to lobby updates
            _isConnected.value = true
            subscribeToLobbyUpdates()
        }
    }

    fun disconnect() {
        wsManager.disconnect()
        _isConnected.value = false
    }

    fun joinLobby(lobbyId: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/join/$lobbyId", player)
    }

    fun leaveLobby(lobbyId: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/leave/$lobbyId", player)
    }

    fun toggleReady(lobbyId: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/ready/$lobbyId", player)
    }

    fun sendChat(lobbyId: String, message: ChatMessage) {
        wsManager.send("/app/lobby/chat/$lobbyId", message)
    }

    fun subscribeToLobby(lobbyId: String) {
        wsManager.subscribe("/topic/lobby/$lobbyId", Lobby::class.java) { lobby ->
            _lobbies.value = _lobbies.value.toMutableList().apply {
                val idx = indexOfFirst { it.name == lobby.name }
                if (idx >= 0) set(idx, lobby) else add(lobby)
            }
        }
    }

    fun createLobby() {
        wsManager.send("/app/lobby/create", Any())
    }


    private fun subscribeToLobbyUpdates() {
        wsManager.subscribe("/topic/lobby-updates", Lobby::class.java) { lobby ->
            _lobbies.value = _lobbies.value.toMutableList().apply {
                val idx = indexOfFirst { it.name == lobby.name }
                if (idx >= 0) set(idx, lobby) else add(lobby)
            }
        }
    }


}