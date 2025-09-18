package com.example.co_opapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.ChatWindow
import com.example.co_opapp.Service.Coop.LobbyService
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

    var selectedLobbyName by remember { mutableStateOf<String?>(null) }
    var isChatVisible by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }

    // Update username when the player changes
    LaunchedEffect(player) {
        username = player?.username.orEmpty()
    }

    // Connect to the service
    LaunchedEffect(Unit) { lobbyService.connect() }

    // Handle Player Actions
    val handlePlayerAction: (String, (PlayerDTO) -> Unit) -> Unit = { lobbyName, action ->
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

        LobbyNameSelector(
            onCreateLobby = { lobbyName -> lobbyService.createLobby(lobbyName) },
            modifier = Modifier.padding(top = 8.dp)
        )

        ConnectionStatusIndicator(connected = isConnected)

        // Lobbies List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(lobbies) { lobby ->
                LobbyCard(
                    lobby = lobby,
                    isSelected = selectedLobbyName == lobby.name,
                    onSelect = {
                        selectedLobbyName = lobby.name
                        lobbyService.subscribeToLobby(lobby.name) // Use name instead of id
                    },
                    onJoin = { handlePlayerAction(lobby.name) { player -> lobbyService.joinLobby(lobby.name, player) } },
                    onLeave = { handlePlayerAction(lobby.name) { player -> lobbyService.leaveLobby(lobby.name, player) } },
                    onToggleReady = { handlePlayerAction(lobby.name) { player -> lobbyService.toggleReady(lobby.name, player) } }
                )
            }
        }

        // Chat Window Trigger
        selectedLobbyName?.let { lobbyName ->
            Button(
                onClick = { toggleChatVisibility(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Chat")
            }

            // Chat Popup/Dialog
            if (isChatVisible) {
                ChatWindow(
                    lobbyId = lobbyName,  // Use lobby name here
                    messages = lobbyChats[lobbyName] ?: emptyList(),
                    chatInput = chatInput,
                    onInputChange = { chatInput = it },
                    onSendMessage = {
                        lobbyService.sendChat(lobbyName, ChatMessage(username, chatInput))
                        chatInput = "" // Clear input after sending
                    },
                    onDismiss = { toggleChatVisibility(false) }
                )
            }
        }
    }
}
