package com.example.co_opapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.ChatWindow
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.Service.Coop.LobbyListService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.ui.components.LobbyScreen.*
import com.example.co_opapp.data_model.PlayerDTO

@Composable
fun LobbyScreen(
    allLobbiesService: LobbyListService,
    currentLobbyService: CurrentLobbyService,
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val player = SessionManager.currentPlayer
    var username by remember { mutableStateOf(player?.username.orEmpty()) }

    val lobbies by remember { derivedStateOf { allLobbiesService.lobbies.value } }
    val currentLobbyState by remember { derivedStateOf { currentLobbyService.lobby.value } }
    val isConnected by remember { derivedStateOf { allLobbiesService.isConnected }}

    var selectedLobbyName by remember { mutableStateOf<String?>(null) }
    var isChatVisible by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }

    // Update username when player changes
    LaunchedEffect(player) {
        username = player?.username.orEmpty()
    }

    // Connect to all lobbies service
    LaunchedEffect(Unit) {
        allLobbiesService.connect()
    }

    // Connect to current lobby when selected
    LaunchedEffect(selectedLobbyName) {
        selectedLobbyName?.let { lobbyName ->
            SessionManager.currentPlayer?.let { player ->
                allLobbiesService.joinLobby(lobbyName, PlayerDTO(player.sessionId, player.username))
            }
        }
    }
    // Handle player actions in current lobby
    val handlePlayerAction: (PlayerDTO, (PlayerDTO) -> Unit) -> Unit = { playerDTO, action ->
        action(playerDTO)
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
            onCreateLobby = { lobbyName -> allLobbiesService.createLobby(lobbyName) },
            modifier = Modifier.padding(top = 8.dp)
        )

        ConnectionStatusIndicator(connected = isConnected)

        // List of available lobbies
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
                    },
                    onJoin = { player?.let { p ->
                        handlePlayerAction(PlayerDTO(p.sessionId, p.username)) { dto ->
                            allLobbiesService.joinLobby(lobby.name, dto)
                        }
                    } },
                    onLeave = { player?.let { p ->
                        handlePlayerAction(PlayerDTO(p.sessionId, p.username)) { dto ->
                            currentLobbyService.leaveLobby(lobby.name, dto)
                        }
                    } },
                    onToggleReady = { player?.let { p ->
                        handlePlayerAction(PlayerDTO(p.sessionId, p.username)) { dto ->
                            currentLobbyService.toggleReady(lobby.name, dto)
                        }
                    } }
                )
            }
        }

        // Chat for selected lobby
        selectedLobbyName?.let { lobbyName ->
            Button(
                onClick = { isChatVisible = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Chat")
            }

            if (isChatVisible) {
                ChatWindow(
                    lobbyId = lobbyName,
                    messages = currentLobbyState?.chatMessages ?: mutableStateListOf(),
                    chatInput = chatInput,
                    onInputChange = { chatInput = it },
                    onSendMessage = {
                        player?.let { p ->
                            val msg = ChatMessage(p.username, chatInput)
                            currentLobbyService.sendChat(lobbyName,msg)
                            chatInput = ""
                        }
                    },
                    onDismiss = { isChatVisible = false }
                )
            }
        }
    }
}
