package com.example.co_opapp.Service.Coop

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.co_opapp.Service.Backend.WebSocketClientManager
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.GameState
import com.example.co_opapp.data_model.GameStatus
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.LobbyDTO
import com.example.co_opapp.data_model.Player
import com.example.co_opapp.data_model.PlayerDTO
import com.example.co_opapp.data_model.TriviaQuestion
import com.example.co_opapp.data_model.toDTO
import com.example.co_opapp.data_model.toLobby
import kotlinx.coroutines.flow.*
import androidx.compose.runtime.snapshotFlow
import com.example.co_opapp.data_model.AnswerRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CurrentLobbyService() {

    private val wsManager = WebSocketClientManager()

    companion object {
        private const val TAG = "CurrentLobbyService"
    }

    private val _lobby = mutableStateOf<Lobby?>(null)
    val lobby: State<Lobby?> get() = _lobby
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)


    //reactively expose important variables
    val players: State<List<PlayerDTO>> = derivedStateOf {
        _lobby.value?.players?.values?.toList() ?: emptyList()
    }
    val gameState: State<GameState?> = derivedStateOf {
        _lobby.value?.gameState?.value
    }
    val gameStatus: State<GameStatus?> = derivedStateOf {
        _lobby.value?.gameState?.value?.gameStatus
    }
    val scores: State<Map<String, Int>> = derivedStateOf {
        gameState.value?.scores ?: emptyMap()
    }
    private val gameStateFlow: Flow<GameState?> = snapshotFlow {
        _lobby.value?.gameState?.value
    }

    /**
     * Leave the current lobby but keep the WSConnection active
     */
    public fun leaveLobby(lobbyName: String, player: Player) {
        Log.d(TAG, "Leaving lobby $lobbyName as ${player.username}")
        wsManager.send("/app/lobby/leave/$lobbyName", player.toDTO())
        wsManager.disconnect()
        resetLobbyState() // Reset internal state
    }


    // Reactive chat messages for the current lobby
    private val _chatMessages = mutableStateListOf<ChatMessage>()
    val chatMessages: SnapshotStateList<ChatMessage> get() = _chatMessages

    // Track connection status
    private val _isConnected = mutableStateOf(false)
    val isConnected: State<Boolean> get() = _isConnected


    //Reactive current question flow to update current question
    // update ONLY when questions or current index change
    val currentQuestion: State<TriviaQuestion?> = derivedStateOf {
        val gs = gameState.value
        val index = gs?.questionIdx?.value ?: return@derivedStateOf null
        val questions = gs.questions
        questions.getOrNull(index)
    }


    fun submitAnswer(questionId: Long, answer: String){
        val lobbyName = lobby.value?.name!!
        val username = SessionManager.currentPlayer?.username!!

        val answerRequest = AnswerRequest(
            roomName = lobbyName,
            username = username,
            questionId = questionId,
            selectedAnswer = answer
        )

        Log.d(TAG, "Submitting answer for ${username} to $lobbyName: $answer")
        wsManager.send("/app/lobby//submit/$lobbyName", answerRequest)

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



    fun toggleReady(lobbyName: String, username: String) {
        Log.d(TAG, "Toggling ready for ${username} in lobby $lobbyName")
        val payload = mapOf("username" to username)
        wsManager.send("/app/lobby/ready/$lobbyName", payload)
    }

    /** Disconnect and leave the current lobby*/
    fun leaveAndDisconnect() {
        Log.d(TAG, "Disconnecting from current lobby")
        leaveLobby(lobby.value?.name ?: "", SessionManager.currentPlayer!!)
        wsManager.disconnect()
        _isConnected.value = false
    }

    /**
     * Subscribe to state and chat upudates and join lobby
     */
    fun subscribeAndJoin(lobbyName: String, player: Player) {
        resetLobbyState()


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
            // join the lobby
            joinLobby(lobbyName, player)
        }
    }

    /** Private helpers */
    private fun joinLobby(lobbyName: String, player: Player) {
        wsManager.send("/app/lobby/join/$lobbyName", player.toDTO())
    }

    private fun resetLobbyState() {
        _lobby.value = null
        _chatMessages.clear()
        _isConnected.value = false
    }







}
