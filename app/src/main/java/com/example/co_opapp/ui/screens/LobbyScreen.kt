package com.example.co_opapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.LobbyService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.PlayerDTO
import com.example.co_opapp.ui.components.LobbyScreen.ConnectionStatusIndicator
import com.example.co_opapp.ui.components.LobbyScreen.LobbyCard
import com.example.co_opapp.ui.components.LobbyScreen.BackButton
import com.example.co_opapp.ui.components.LobbyScreen.ChatWindow

@Composable
fun LobbyScreen(
    lobbyService: LobbyService,
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Observe changes in SessionManager to keep the username up to date
    val player = SessionManager.currentPlayer
    var username by remember { mutableStateOf(player?.username.orEmpty()) }

    // Update username when the player changes
    LaunchedEffect(player) {
        username = player?.username.orEmpty()
    }

    val lobbies by lobbyService.lobbies
    val lobbyChats by lobbyService.lobbyChats
    val isConnected by remember { derivedStateOf { lobbyService.isConnected } }

    var selectedLobbyId by remember { mutableStateOf<String?>(null) }
    var chatLobbyId by remember { mutableStateOf<String?>(null) }
    var chatInput by remember { mutableStateOf("") }

    // Connect to the service
    LaunchedEffect(Unit) { lobbyService.connect() }

    // Handle Player Actions
    val handlePlayerAction: (String, (PlayerDTO) -> Unit) -> Unit = { lobbyId, action ->
        player?.let {
            action(PlayerDTO(it.sessionId, it.username))
        }
    }

    // Show the chat dialog if `chatLobbyId` is set
    val showChatDialog = chatLobbyId != null

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Back Button
        BackButton(onNavigateBack = onNavigateBack)

        // Title and Username
        Text("Select a Lobby", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Your Username") },
            modifier = Modifier.fillMaxWidth()
        )

        // Create Lobby Button
        Button(
            onClick = { lobbyService.createLobby() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Create Lobby") }

        // Connection Status Indicator
        ConnectionStatusIndicator(connected = isConnected)

        // Lobbies List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(lobbies) { lobby ->
                LobbyCard(
                    lobby = lobby,
                    isSelected = selectedLobbyId == lobby.lobbyId,
                    currentPlayer = player,
                    onSelect = {
                        selectedLobbyId = lobby.lobbyId
                        lobbyService.subscribeToLobby(lobby.lobbyId)
                    },
                    onJoin = { handlePlayerAction(lobby.lobbyId) { player -> lobbyService.joinLobby(lobby.lobbyId, player) } },
                    onLeave = { handlePlayerAction(lobby.lobbyId) { player -> lobbyService.leaveLobby(lobby.lobbyId, player) } },
                    onToggleReady = { handlePlayerAction(lobby.lobbyId) { player -> lobbyService.toggleReady(lobby.lobbyId, player) } },
                    onShowChat = { chatLobbyId = lobby.lobbyId }
                )
            }
        }
    }

    // Chat Dialog
    if (showChatDialog) {
        ChatWindow(
            lobbyId = chatLobbyId!!,
            messages = lobbyChats[chatLobbyId] ?: emptyList(),
            chatInput = chatInput,
            onInputChange = { chatInput = it },
            onSend = {
                player?.let {
                    if (chatInput.isNotBlank()) {
                        lobbyService.sendChat(chatLobbyId!!, ChatMessage(it.username, chatInput))
                        chatInput = ""
                    }
                }
            },
            onDismiss = { chatLobbyId = null }
        )
    }
}
