package com.example.co_opapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.ChatWindow
import com.example.co_opapp.Service.Coop.LobbyListService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.ui.components.LobbyScreen.*
import com.example.co_opapp.data_model.PlayerDTO

@Composable
fun LobbyScreen(
    lobbyService: LobbyListService,
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val player = SessionManager.currentPlayer
    var username by remember { mutableStateOf(player?.username.orEmpty()) }


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

        // Lobbies List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {

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


            }
        }

