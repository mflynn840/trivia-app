package com.example.co_opapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.ChatWindow
import com.example.co_opapp.Service.LobbyService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.ui.components.LobbyScreen.*
import com.example.co_opapp.data_model.PlayerDTO

@Composable
fun LobbyScreen(
    lobbyService: LobbyService,
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val player = SessionManager.currentPlayer
    var username by remember { mutableStateOf(player?.username.orEmpty()) }

    val lobbies by lobbyService.lobbies
    val lobbyChats by lobbyService.lobbyChats
    val isConnected by remember { derivedStateOf { lobbyService.isConnected } }

    var selectedLobbyId by remember { mutableStateOf<String?>(null) }
    var isChatVisible by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }

    // Update username when the player changes
    LaunchedEffect(player) {
        username = player?.username.orEmpty()
    }

    // Connect to the service
    LaunchedEffect(Unit) { lobbyService.connect() }

    // Handle Player Actions
    val handlePlayerAction: (String, (PlayerDTO) -> Unit) -> Unit = { lobbyId, action ->
        player?.let {
            action(PlayerDTO(it.sessionId, it.username))
        }
    }

    // Show or hide chat window
    val toggleChatVisibility = { isVisible: Boolean ->
        isChatVisible = isVisible
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        BackButton(onNavigateBack = onNavigateBack)

        Text("Select a Lobby", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Your Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { lobbyService.createLobby() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Create Lobby") }

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
                    onToggleReady = { handlePlayerAction(lobby.lobbyId) { player -> lobbyService.toggleReady(lobby.lobbyId, player) } }
                )
            }
        }

        // Chat Window Trigger
        selectedLobbyId?.let { lobbyId ->
            Button(
                onClick = { toggleChatVisibility(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Chat")
            }

            // Chat Popup/Dialog
            if (isChatVisible) {
                ChatWindow(
                    lobbyId = lobbyId,
                    messages = lobbyChats[lobbyId] ?: emptyList(),
                    chatInput = chatInput,
                    onInputChange = { chatInput = it },
                    onSendMessage = {
                        lobbyService.sendChat(lobbyId, ChatMessage(username, chatInput))
                        chatInput = "" // Clear input after sending
                    },
                    onDismiss = { toggleChatVisibility(false) }
                )
            }
        }
    }
}
