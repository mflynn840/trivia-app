package com.example.co_opapp.Service.Coop

import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.PlayerDTO

class CurrentLobbyService(private val wsManager: WebSocketClientManager) {

    private val _lobby = mutableStateOf<Lobby?>(null)
    val lobby: State<Lobby?> get() = _lobby

    // Reactive chat messages for the current lobby
    private val _chatMessages = mutableStateListOf<ChatMessage>()
    val chatMessages: SnapshotStateList<ChatMessage> get() = _chatMessages

    // Track connection status
    private val _isConnected = mutableStateOf(false)
    val isConnected: State<Boolean> get() = _isConnected

    /** Subscribe to a single lobby by name */
    fun subscribe(lobbyName: String) {
        wsManager.connect {
            _isConnected.value = true // mark connected when ws opens

            wsManager.subscribeLobby("/topic/lobby/$lobbyName") { updatedLobby ->
                _lobby.value = updatedLobby

                // Update reactive chat list
                _chatMessages.clear()
                _chatMessages.addAll(updatedLobby.chatMessages)
            }
        }
    }

    /** Send a chat message to this lobby */
    fun sendChat(lobbyName: String, message: ChatMessage) {
        wsManager.send("/app/lobby/chat/$lobbyName", message)
    }

    fun leaveLobby(lobbyName: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/leave/$lobbyName", player)
    }

    fun toggleReady(lobbyName: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/ready/$lobbyName", player)
    }


    /** Disconnect */
    fun disconnect() {
        wsManager.disconnect()
        _isConnected.value = false
    }
}
