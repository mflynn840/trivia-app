package com.example.co_opapp.Service.Coop

import androidx.compose.runtime.mutableStateOf
import com.example.co_opapp.Service.Backend.WebSocketClientManager
import com.example.co_opapp.data_model.CreateLobbyRequest
import com.example.co_opapp.data_model.Lobby


class LobbyListService(private val wsManager: WebSocketClientManager) {
    private val _lobbies = mutableStateOf<List<Lobby>>(emptyList())
    //val lobbies: State<List<Lobby>> get() = _lobbies

    fun connect() {
        wsManager.connect {
            subscribeToAllLobbies()
        }
    }

    private fun subscribeToAllLobbies() {
        wsManager.subscribe("/topic/lobby/all", Map::class.java) { map ->
            val lobbiesList = map.values.map { it as Lobby }
            _lobbies.value = lobbiesList
        }
    }

    fun createLobby(name: String) {
        wsManager.send("/app/lobby/create", CreateLobbyRequest(name))
    }
}
