package com.example.co_opapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.Service.RaceModeGameService
import com.example.co_opapp.data_model.GameState
import com.example.co_opapp.data_model.Player

@Composable
fun LobbyScreen(
    gameService: RaceModeGameService,
    authService: AuthService,
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Observe current player
    val currentPlayer by authService.currentPlayerFlow.collectAsState()
    var username by remember { mutableStateOf("") }

    // Keep username synced with logged-in player
    LaunchedEffect(currentPlayer) {
        username = currentPlayer?.username ?: ""
    }

    var selectedLobbyId by remember { mutableStateOf<Long?>(null) }
    var isJoining by remember { mutableStateOf(false) }

    val gameState by gameService.gameState.collectAsState()
    val connectionStatus by gameService.connectionStatus.collectAsState()
    val errorMessage by gameService.errorMessage.collectAsState()
    val lobbies by gameService.lobbies.collectAsState()

    // Navigate to game automatically when in progress
    LaunchedEffect(gameState) {
        if (gameState?.gameState == GameState.IN_PROGRESS) {
            onNavigateToGame()
        }
    }

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
            Text(
                "Select a Lobby",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onNavigateBack) { Text("Logout") }
        }

        // Username input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Your Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !connectionStatus
        )

        // Lobby cards
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(lobbies) { lobby ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLobbyId = lobby.id },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedLobbyId == lobby.id)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Lobby #${lobby.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Players: ${lobby.players.size}/${lobby.maxPlayers}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Status: ${lobby.gameState.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (lobby.players.isNotEmpty()) {
                            Text("Players:", fontWeight = FontWeight.SemiBold)
                            lobby.players.forEach { player ->
                                Text(
                                    "${player.username}${if (player.isHost) " (Host)" else ""} ${if (player.isReady) "✓ Ready" else ""}"
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Join button
        Button(
            onClick = {
                // Only join if currentPlayer exists
                currentPlayer?.let { player ->
                    selectedLobbyId?.let { lobbyId ->
                        isJoining = true
                        gameService.joinLobby(
                            player = player,
                            lobbyId = lobbyId,
                            onSuccess = { isJoining = false },
                            onError = { isJoining = false }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedLobbyId != null && !isJoining && username.isNotBlank() && currentPlayer != null
        ) {
            if (isJoining) CircularProgressIndicator(modifier = Modifier.size(16.dp))
            else Text("Join Selected Lobby")
        }

        // Error message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(error, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}
