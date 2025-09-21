package com.example.co_opapp.ui.screens

import LobbyList
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.Service.Coop.LobbyListService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.ui.components.LobbyScreen.*
import com.example.co_opapp.data_model.PlayerDTO
@Composable
fun LobbySelectorScreen(
    allLobbiesService: LobbyListService,
    modifier: Modifier = Modifier,
    onNavigateToLobby: () -> Unit,
    onNavigateBack: () -> Unit

) {
    val player = SessionManager.currentPlayer
    // Reactive state for lobbies and connection status
    val lobbies by remember { derivedStateOf { allLobbiesService.lobbies.value } }
    val isConnected by remember { derivedStateOf { allLobbiesService.isConnected } }

    var selectedLobbyName by remember { mutableStateOf<String?>(null) }

    // Ensure we have a valid player
    val playerDTO = player?.let { PlayerDTO(it.sessionId, it.username) }

    // Connect to WebSocket on initial composition
    LaunchedEffect(Unit) {
        allLobbiesService.connect()

    }

    // Join and subscribe to the selected lobby
    LaunchedEffect(selectedLobbyName) {
        selectedLobbyName?.let { lobbyName ->
            playerDTO?.let { dto ->
                allLobbiesService.joinLobby(lobbyName, dto)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button and connection status
        BackButton(onNavigateBack = onNavigateBack)
        Text("Select/Create a Lobby", style = MaterialTheme.typography.headlineMedium)
        LobbyNameSelector(
            onCreateLobby = { lobbyName -> allLobbiesService.createLobby(lobbyName) },
            modifier = Modifier.padding(top = 8.dp)
        )
        ConnectionStatusIndicator(connected = isConnected)

        // Display the list of lobbies
        LobbyList(
            lobbies = lobbies,
            selectedLobbyName = selectedLobbyName.orEmpty(),
            onLobbySelect = { lobbyName -> selectedLobbyName = lobbyName },
            onJoinLobby = { lobby ->
                playerDTO?.let { dto ->
                    allLobbiesService.joinLobby(lobby.name, dto)
                }
                onNavigateToLobby
            },
        )
    }
}
