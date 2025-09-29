package com.example.co_opapp.Service.Coop

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.co_opapp.Service.Backend.WebSocketClientManager
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.GameState
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.LobbyDTO
import com.example.co_opapp.data_model.Player
import com.example.co_opapp.data_model.PlayerDTO
import com.example.co_opapp.data_model.toDTO
import com.example.co_opapp.data_model.toLobby

class CurrentLobbyService() {

    private val wsManager = WebSocketClientManager()

    companion object {
        private const val TAG = "CurrentLobbyService"
    }

    private val _lobby = mutableStateOf<Lobby?>(null)
    val lobby: State<Lobby?> get() = _lobby


    //reactive lobby members list
    val players: State<List<PlayerDTO>> = derivedStateOf {
        _lobby.value?.players?.values?.toList() ?: emptyList()
    }

    // expose a reactive game state


    // Reactive chat messages for the current lobby
    private val _chatMessages = mutableStateListOf<ChatMessage>()
    val chatMessages: SnapshotStateList<ChatMessage> get() = _chatMessages

    // Track connection status
    private val _isConnected = mutableStateOf(false)
    val isConnected: State<Boolean> get() = _isConnected


    /** Send a chat message to this lobby */
    fun sendChat(message: ChatMessage) {
        val lobbyName = lobby.value?.name
        if (lobbyName == null) {
            Log.w(TAG, "Cannot send chat, lobby is null")
            return
        }
        Log.d(TAG, "Sending chat message to lobby $lobbyName: $message")
        wsManager.send("/app/lobby/chat/$lobbyName", message)
    }

    private fun leaveLobby(lobbyName: String, player: Player) {
        Log.d(TAG, "Leaving lobby $lobbyName as ${player.username}")
        wsManager.send("/app/lobby/leave/$lobbyName", player.toDTO())
    }

    fun toggleReady(lobbyName: String, username: String) {
        Log.d(TAG, "Toggling ready for ${username} in lobby $lobbyName")
        val payload = mapOf("username" to username)
        wsManager.send("/app/lobby/ready/$lobbyName", payload)
    }

    /** Disconnect */
    fun disconnect() {
        Log.d(TAG, "Disconnecting from current lobby")
        leaveLobby(lobby.value?.name ?: "", SessionManager.currentPlayer!!)
        wsManager.disconnect()
        _isConnected.value = false
    }


    fun subscribeAndJoin(lobbyName: String, player: Player) {
        wsManager.connect {
            _isConnected.value = true

            // Subscribe to state first
            wsManager.subscribeTopic("/topic/lobby/$lobbyName/state", LobbyDTO::class.java) { lobbyDto ->
                _lobby.value = lobbyDto.toLobby()
            }

            // Subscribe to chat
            wsManager.subscribeTopic("/topic/lobby/$lobbyName/chat", ChatMessage::class.java) { msg ->
                _chatMessages.add(msg)
            }

            // Now actually join
            joinLobby(lobbyName, player)
        }
    }

    /** Private helper: only called after subscribing */
    private fun joinLobby(lobbyName: String, player: Player) {
        wsManager.send("/app/lobby/join/$lobbyName", player.toDTO())
    }



}
