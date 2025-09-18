package com.example.co_opapp.Repository

import com.example.co_opapp.data_model.Lobby
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LobbyRepository {
    private val _lobbies = MutableStateFlow<List<Lobby>>(emptyList())
    val lobbies: StateFlow<List<Lobby>> = _lobbies

    fun updateLobby(lobbyUpdate: Lobby, includeChat: Boolean = true) {
        val updatedList = _lobbies.value.toMutableList()
        val index = updatedList.indexOfFirst { it.name == lobbyUpdate.name }

        if (index >= 0) {
            val existing = updatedList[index]
            existing.players.clear()
            existing.players.putAll(lobbyUpdate.players)
            if (includeChat) {
                existing.chatMessages.clear()
                existing.chatMessages.putAll(lobbyUpdate.chatMessages)
            }
        } else {
            updatedList.add(lobbyUpdate)
        }
        _lobbies.value = updatedList
    }
}