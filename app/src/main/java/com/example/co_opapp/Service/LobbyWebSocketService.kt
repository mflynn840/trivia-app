package com.example.co_opapp.Service

import android.util.Log
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.Player
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow



class LobbyWebSocketService(
    private val backendUrl: String = "ws://192.168.4.21:8080/ws"
) {

    private val gson = Gson()
    private val disposables = CompositeDisposable()
    private var stompClient: StompClient? = null

    // State flows for Compose
    private val _lobbies = MutableStateFlow<List<Lobby>>(emptyList())
    val lobbies: StateFlow<List<Lobby>> = _lobbies

    private val _lobbyChats = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val lobbyChats: StateFlow<Map<String, List<String>>> = _lobbyChats

    fun connect() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, backendUrl)
        stompClient?.connect()

        // Subscribe to lobby updates
        val disposable = stompClient?.topic("/topic/lobby-updates")
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ frame ->
                val lobby = gson.fromJson(frame.payload, Lobby::class.java)
                val updated = _lobbies.value.filter { it.lobbyId != lobby.lobbyId } + lobby
                _lobbies.value = updated
            }, { error ->
                Log.e("LobbyWebSocketService", "Error receiving lobby updates", error)
            })

        disposable?.let { disposables.add(it) }
    }

    fun disconnect() {
        disposables.clear()
        stompClient?.disconnect()
    }

    // Join a lobby
    fun joinLobby(lobbyId: String, player: Player) {
        sendMessage("/app/lobby/join/$lobbyId", player)
    }

    fun leaveLobby(lobbyId: String, player: Player) {
        sendMessage("/app/lobby/leave/$lobbyId", player)
    }

    fun toggleReady(lobbyId: String, player: Player) {
        sendMessage("/app/lobby/ready/$lobbyId", player)
    }

    fun sendChat(lobbyId: String, msg: ChatMessage) {
        sendMessage("/app/lobby/chat/$lobbyId", msg)
    }

    private fun sendMessage(destination: String, payload: Any) {
        val json = gson.toJson(payload)
        stompClient?.send(destination, json)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ /* success */ }, { error ->
                Log.e("LobbyWebSocketService", "Error sending message to $destination", error)
            })
            ?.let { disposables.add(it) }
    }

    // Optional: subscribe to a specific lobby's updates for chat
    fun subscribeToLobby(lobbyId: String) {
        val disposable = stompClient?.topic("/topic/lobby/$lobbyId")
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ frame: StompMessage ->
                val lobby = gson.fromJson(frame.payload, Lobby::class.java)
                _lobbyChats.value = _lobbyChats.value.toMutableMap().apply {
                    put(lobbyId, lobby.chatMessages)
                }
            }, { error ->
                Log.e("LobbyWebSocketService", "Error subscribing to lobby $lobbyId", error)
            })

        disposable?.let { disposables.add(it) }
    }
}
