package com.example.co_opapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.Service.LobbyWebSocketService
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.ui.components.LobbyScreen.ConnectionStatusIndicator
import com.example.co_opapp.ui.components.LobbyScreen.LobbyCard
import com.example.co_opapp.data_model.PlayerDTO  // <-- new DTO

@Composable
fun LobbyScreen(
    lobbyService: LobbyWebSocketService,
    authService: AuthService,
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentPlayer by authService.currentPlayerFlow.collectAsState()
    var username by remember { mutableStateOf("") }

    val lobbies by lobbyService.lobbies.collectAsState()
    val lobbyChats by lobbyService.lobbyChats.collectAsState()
    var selectedLobbyId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentPlayer) { username = currentPlayer?.username ?: "" }
    LaunchedEffect(Unit) { lobbyService.connect() }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Select a Lobby", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Your Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { lobbyService.createLobby() }, modifier = Modifier.fillMaxWidth()) {
            Text("Create Lobby")
        }

        val isConnected by lobbyService.isConnected.collectAsState()
        ConnectionStatusIndicator(connected = isConnected)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(lobbies) { lobby ->
                LobbyCard(
                    lobby = lobby,
                    isSelected = selectedLobbyId == lobby.lobbyId,
                    chatMessages = lobbyChats[lobby.lobbyId] ?: emptyList(),
                    currentPlayer = currentPlayer,
                    onSelect = {
                        selectedLobbyId = lobby.lobbyId
                        lobbyService.subscribeToLobby(lobby.lobbyId)
                    },
                    onSendMessage = { message ->
                        currentPlayer?.let { player ->
                            lobbyService.sendChat(lobby.lobbyId, ChatMessage(player.username, message))
                        }
                    },
                    // Use PlayerDTO instead of full Player
                    onJoin = { player ->
                        currentPlayer?.let { p ->
                            lobbyService.joinLobby(lobby.lobbyId, PlayerDTO(p.sessionId, p.username))
                        }
                    },
                    onLeave = { player ->
                        currentPlayer?.let { p ->
                            lobbyService.leaveLobby(lobby.lobbyId, PlayerDTO(p.sessionId, p.username))
                        }
                    },
                    onToggleReady = { player ->
                        currentPlayer?.let { p ->
                            lobbyService.toggleReady(lobby.lobbyId, PlayerDTO(p.sessionId, p.username))
                        }
                    }
                )
            }
        }
    }
}
