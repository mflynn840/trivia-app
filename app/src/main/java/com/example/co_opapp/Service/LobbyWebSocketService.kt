package com.example.co_opapp.Service

import android.util.Log
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.Lobby
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.co_opapp.data_model.PlayerDTO


class LobbyWebSocketService(
    private val backendUrl: String = "ws://192.168.4.21:8080/ws"
) {
    private val TAG = "LobbyWebSocketService"

    private val gson = Gson()
    private val disposables = CompositeDisposable()
    private var stompClient: StompClient? = null
    private var isClientConnected = false

    // State flows for Compose
    private val _lobbies = MutableStateFlow<List<Lobby>>(emptyList())
    val lobbies: StateFlow<List<Lobby>> = _lobbies

    private val _lobbyChats = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val lobbyChats: StateFlow<Map<String, List<String>>> = _lobbyChats

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    /**
     * Connect to backend WebSocket server
     */

    fun connect() {
        Log.d(TAG, "Attempting to connect to WebSocket at $backendUrl")


        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, backendUrl)
        stompClient?.connect()

        // Track lifecycle events
        stompClient?.lifecycle()?.subscribe { event: LifecycleEvent ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d(TAG, "Stomp connection opened")
                    _isConnected.value = true
                    isClientConnected = true

                    // Subscribe to lobby updates after connection
                    subscribeToLobbyUpdates()
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.w(TAG, "Stomp connection closed")
                    _isConnected.value = false
                    isClientConnected = false
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e(TAG, "Stomp connection error: ${event.exception?.message}", event.exception)
                    _isConnected.value = false
                    isClientConnected = false
                }
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                    Log.e(TAG, "Stomp heartbeat failed")
                    _isConnected.value = false
                    isClientConnected = false
                }
            }
        }?.let { disposables.add(it) }

        try {
            stompClient?.connect()
            Log.d(TAG, "Stomp connect() called")
        } catch (e: Exception) {
            Log.e(TAG, "Exception while initiating WebSocket connection", e)
        }
    }


    /**
     * Disconnect the client
     */
    fun disconnect() {
        Log.d(TAG, "Disconnecting WebSocket")
        disposables.clear()
        try {
            stompClient?.disconnect()
            Log.d(TAG, "Stomp disconnect called")
        } catch (e: Exception) {
            Log.e(TAG, "Exception during disconnect", e)
        }
        _isConnected.value = false
        isClientConnected = false
    }

    /**
     * Sends a message only if connected
     */
    private fun sendMessage(destination: String, payload: Any) {
        if (!isClientConnected) {
            Log.w(TAG, "Cannot send message, WebSocket not connected: $destination")
            return
        }

        val json = gson.toJson(payload)
        Log.d(TAG, "Sending message to $destination: $json")

        try {
            stompClient?.send(destination, json)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    Log.d(TAG, "Message successfully sent to $destination")
                }, { error ->
                    Log.e(TAG, "Error sending message to $destination", error)
                })?.let { disposables.add(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Exception sending message to $destination", e)
        }
    }

    /**
     * Lobby operations
     */
    fun createLobby() {
        Log.d(TAG, "Creating lobby")
        sendMessage("/app/lobby/create", Any())
    }

    fun joinLobby(lobbyId: String, player: PlayerDTO) {
        Log.d(TAG, "Joining lobby $lobbyId with player ${player.username}")
        sendMessage("/app/lobby/join/$lobbyId", player)
    }

    fun leaveLobby(lobbyId: String, player: PlayerDTO) {
        Log.d(TAG, "Leaving lobby $lobbyId with player ${player.username}")
        sendMessage("/app/lobby/leave/$lobbyId", player)
    }

    fun toggleReady(lobbyId: String, player: PlayerDTO) {
        Log.d(TAG, "Toggling ready state in lobby $lobbyId for player ${player.username}")
        sendMessage("/app/lobby/ready/$lobbyId", player)
    }

    fun sendChat(lobbyId: String, msg: ChatMessage) {
        Log.d(TAG, "Sending chat message to lobby $lobbyId: ${msg.message}")
        sendMessage("/app/lobby/chat/$lobbyId", msg)
    }

    /**
     * Subscribe to a single lobby's chat
     */
    fun subscribeToLobby(lobbyId: String) {
        if (!isClientConnected) {
            Log.w(TAG, "Cannot subscribe to lobby $lobbyId, WebSocket not connected")
            return
        }

        Log.d(TAG, "Subscribing to lobby $lobbyId")
        try {
            stompClient?.topic("/topic/lobby/$lobbyId")
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ frame: StompMessage ->
                    Log.d(TAG, "Received lobby message for $lobbyId: ${frame.payload}")
                    val lobby = gson.fromJson(frame.payload, Lobby::class.java)
                    _lobbyChats.value = _lobbyChats.value.toMutableMap().apply {
                        put(lobbyId, lobby.chatMessages)
                    }
                }, { error ->
                    Log.e(TAG, "Error subscribing to lobby $lobbyId", error)
                })?.let { disposables.add(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Exception subscribing to lobby $lobbyId", e)
        }
    }

    /**
     * Subscribe to lobby list updates
     */
    private fun subscribeToLobbyUpdates() {
        Log.d(TAG, "Subscribing to lobby updates")
        try {
            stompClient?.topic("/topic/lobby-updates")
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ frame: StompMessage ->
                    Log.d(TAG, "Received lobby update: ${frame.payload}")
                    val lobby = gson.fromJson(frame.payload, Lobby::class.java)
                    val updated = _lobbies.value.filter { it.lobbyId != lobby.lobbyId } + lobby
                    _lobbies.value = updated
                }, { error ->
                    Log.e(TAG, "Error receiving lobby updates", error)
                })?.let { disposables.add(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Exception subscribing to lobby updates", e)
        }
    }
}
