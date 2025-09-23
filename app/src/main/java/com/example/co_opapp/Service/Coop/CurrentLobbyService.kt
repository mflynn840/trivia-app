package com.example.co_opapp.Service.Coop

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.co_opapp.Service.Backend.WebSocketClientManager
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.PlayerDTO

class CurrentLobbyService(private val wsManager: WebSocketClientManager) {

    companion object {
        private const val TAG = "CurrentLobbyService"
    }

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
        Log.d(TAG, "Subscribing to lobby $lobbyName")
        wsManager.connect {
            _isConnected.value = true
            Log.i(TAG, "WebSocket connected for lobby $lobbyName")

            // Subscribe to lobby state updates
            wsManager.subscribeTopic("/topic/lobby/$lobbyName/state", Lobby::class.java) { lobby ->
                _lobby.value = lobby
                Log.d(TAG, "Lobby state updated: $lobby")
            }

            // Subscribe to chat messages
            wsManager.subscribeTopic("/topic/lobby/$lobbyName/chat", ChatMessage::class.java) { chatMessage ->
                Log.d(TAG, "New chat message: $chatMessage")
                _chatMessages.add(chatMessage)
            }
        }
    }

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

    fun leaveLobby(lobbyName: String, username: String) {
        Log.d(TAG, "Leaving lobby $lobbyName as $username")
        wsManager.send("/app/lobby/leave/$lobbyName", PlayerDTO(lobbyName, username))
    }

    fun toggleReady(lobbyName: String, username: String) {
        Log.d(TAG, "Toggling ready for $username in lobby $lobbyName")
        wsManager.send("/app/lobby/ready/$lobbyName", PlayerDTO(lobbyName, username))
    }

    /** Disconnect */
    fun disconnect() {
        Log.d(TAG, "Disconnecting from current lobby")
        wsManager.disconnect()
        _isConnected.value = false
    }


    fun subscribeAndJoin(lobbyName: String, username: String) {
        wsManager.connect {
            _isConnected.value = true

            // Subscribe to state first
            wsManager.subscribeTopic("/topic/lobby/$lobbyName/state", Lobby::class.java) { lobby ->
                _lobby.value = lobby
            }

            // Subscribe to chat
            wsManager.subscribeTopic("/topic/lobby/$lobbyName/chat", ChatMessage::class.java) { msg ->
                _chatMessages.add(msg)
            }

            // Now actually join
            joinLobby(lobbyName, username)
        }
    }

    /** Private helper: only called after subscribing */
    private fun joinLobby(lobbyName: String, username: String) {
        wsManager.send("/app/lobby/join/$lobbyName", PlayerDTO(lobbyName, username))
    }



}
