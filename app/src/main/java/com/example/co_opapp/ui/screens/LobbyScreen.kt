package com.example.co_opapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.Service.LobbyWebSocketService
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.Player
import kotlinx.coroutines.launch

@Composable
fun LobbyScreen(
    lobbyService: LobbyWebSocketService,
    authService: AuthService,
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // Current player
    val currentPlayer by authService.currentPlayerFlow.collectAsState()
    var username by remember { mutableStateOf("") }

    LaunchedEffect(currentPlayer) {
        username = currentPlayer?.username ?: ""
    }

    var selectedLobbyId by remember { mutableStateOf<String?>(null) }
    var chatInput by remember { mutableStateOf("") }

    val lobbies by lobbyService.lobbies.collectAsState()
    val lobbyChats by lobbyService.lobbyChats.collectAsState()

    // Connect websocket on first composition
    LaunchedEffect(Unit) { lobbyService.connect() }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Select a Lobby", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Button(onClick = onNavigateBack) { Text("Logout") }
        }

        // Username input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Your Username") },
            modifier = Modifier.fillMaxWidth(),
        )

        // Lobby list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(lobbies) { lobby ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedLobbyId = lobby.lobbyId
                            lobbyService.subscribeToLobby(lobby.lobbyId)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedLobbyId == lobby.lobbyId)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Lobby #${lobby.lobbyId}", fontWeight = FontWeight.Bold)
                        Text("Players: ${lobby.players.size}/${lobby.maxPlayers}")
                        Text("Status: ${lobby.gameState.name}")
                        if (lobby.players.isNotEmpty()) {
                            Text("Players:", fontWeight = FontWeight.SemiBold)
                            lobby.players.values.forEach { player ->
                                Text("${player.username}${if (player.isHost) " (Host)" else ""} ${if (player.isReady) "✓ Ready" else ""}")
                            }
                        }
                    }
                }
            }
        }

        // Chat
        selectedLobbyId?.let { lobbyId ->
            Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val messages = lobbyChats[lobbyId] ?: emptyList()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(messages) { msg ->
                        Text(msg)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        label = { Text("Type a message") },
                        modifier = Modifier.weight(1f),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                currentPlayer?.let { player ->
                                    lobbyService.sendChat(lobbyId, ChatMessage(player.username, chatInput))
                                    chatInput = ""
                                }
                            }
                        )
                    )
                    Button(
                        onClick = {
                            currentPlayer?.let { player ->
                                lobbyService.sendChat(lobbyId, ChatMessage(player.username, chatInput))
                                chatInput = ""
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) { Text("Send") }
                }
            }
        }

        // Join / Leave / Ready buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    currentPlayer?.let { player ->
                        selectedLobbyId?.let { lobbyId ->
                            lobbyService.joinLobby(lobbyId, player)
                        }
                    }
                },
                enabled = selectedLobbyId != null
            ) { Text("Join") }

            Button(
                onClick = {
                    currentPlayer?.let { player ->
                        selectedLobbyId?.let { lobbyId ->
                            lobbyService.leaveLobby(lobbyId, player)
                        }
                    }
                },
                enabled = selectedLobbyId != null
            ) { Text("Leave") }

            Button(
                onClick = {
                    currentPlayer?.let { player ->
                        selectedLobbyId?.let { lobbyId ->
                            lobbyService.toggleReady(lobbyId, player)
                        }
                    }
                },
                enabled = selectedLobbyId != null
            ) { Text("Toggle Ready") }
        }
    }
}
