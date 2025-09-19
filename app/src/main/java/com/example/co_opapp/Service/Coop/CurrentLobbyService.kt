package com.example.co_opapp.Service.Coop

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.co_opapp.Service.Backend.WebSocketClientManager
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.PlayerDTO

/**
 *
 * High level UI-facing API for interacting with the backends websocket
 */
class CurrentLobbyService(private val wsManager: WebSocketClientManager) {

    private val _lobby = mutableStateOf<Lobby?>(null)
    val lobby: State<Lobby?> get() = _lobby

    // Reactive chat messages for the current lobby
    private val _chatMessages = mutableStateListOf<String>()
    val chatMessages: SnapshotStateList<String> get() = _chatMessages

    fun subscribe(lobbyName: String) {
        wsManager.subscribe("/topic/lobby/$lobbyName", Lobby::class.java) { updatedLobby ->
            _lobby.value = updatedLobby
            // Update reactive chat list
            _chatMessages.clear()
            //_chatMessages.addAll(updatedLobby.chatMessages)
        }
    }

    fun sendChat() {

    }

    fun joinLobby(lobbyName: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/join/$lobbyName", player)
    }

    fun leaveLobby(lobbyName: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/leave/$lobbyName", player)
    }

    fun toggleReady(lobbyName: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/ready/$lobbyName", player)
    }

    fun sendChat(lobbyName: String, message: ChatMessage) {
        wsManager.send("/app/lobby/chat/$lobbyName", message)
    }
}

