package com.example.co_opapp.Service.Coop



import androidx.compose.runtime.*
import com.example.co_opapp.Service.Coop.WebSocketClientManager
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.PlayerDTO

class LobbyListService(
    private val wsManager: WebSocketClientManager
) {
    private val _lobbies = mutableStateOf<List<Lobby>>(emptyList())
    //val lobbies: State<List<Lobby>> get() = _lobbies

    private val _isConnected = mutableStateOf(false)
    val isConnected: Boolean get() = _isConnected.value

    fun connect() {
        wsManager.connect {
            _isConnected.value = true

            wsManager.subscribeLobbyAll { map ->
                _lobbies.value = map.values.toList()
            }

            wsManager.send("/app/lobby/getAll", "")
        }
    }

    fun disconnect() {
        wsManager.disconnect()
        _isConnected.value = false
    }

    fun createLobby(name: String) {
        wsManager.send("/app/lobby/create", mapOf("name" to name))
    }

    fun joinLobby(lobbyName: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/join/$lobbyName", player)
    }


    fun toggleReady(lobbyName: String, player: PlayerDTO) {
        wsManager.send("/app/lobby/ready/$lobbyName", player)
    }
}


