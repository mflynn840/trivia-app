package com.example.co_opapp.ui.screens

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
import com.example.co_opapp.Service.CoopGameService
import com.example.co_opapp.data_model.GameState
import com.example.co_opapp.data_model.Player




/*
@Composable
fun LobbyScreen(
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val gameNetworkService = remember { CoOpGameService() }
    val currentPlayer by remember { derivedStateOf { gameNetworkService.getMyPlayer() } }
    var username by remember { mutableStateOf(currentPlayer?.username ?: "") }

    LaunchedEffect(currentPlayer) {
        currentPlayer?.username?.let { username = it }
    }

    var hostIp by remember { mutableStateOf("") }
    var isHosting by remember { mutableStateOf(false) }
    var isJoining by remember { mutableStateOf(false) }

    val gameState by gameNetworkService.gameState.collectAsState()
    val connectionStatus by gameNetworkService.connectionStatus.collectAsState()
    val errorMessage by gameNetworkService.errorMessage.collectAsState()

    LaunchedEffect(gameState) {
        if (gameState?.isGameStarted == true) onNavigateToGame()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Co-op Game Lobby",
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

        // Local IP display
        gameNetworkService.getLocalIpAddress()?.let { localIp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Your Local IP:", style = MaterialTheme.typography.labelMedium)
                    Text(text = localIp, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (!connectionStatus) {
            // Host/Join buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        if (username.isNotBlank()) {
                            isHosting = true
                            gameNetworkService.startHosting(
                                player = Player(id = username, username = username),
                                onSuccess = { isHosting = false },
                                onError = { isHosting = false }
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isHosting && !isJoining && username.isNotBlank()
                ) {
                    if (isHosting) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else Text("Host Game")
                }

                Button(
                    onClick = {
                        if (username.isNotBlank() && hostIp.isNotBlank()) {
                            isJoining = true
                            gameNetworkService.joinGame(
                                player = Player(id = username, username = username),
                                hostIp = hostIp,
                                onSuccess = { isJoining = false },
                                onError = { isJoining = false }
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isHosting && !isJoining && username.isNotBlank() && hostIp.isNotBlank()
                ) {
                    if (isJoining) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else Text("Join Game")
                }
            }

            OutlinedTextField(
                value = hostIp,
                onValueChange = { hostIp = it },
                label = { Text("Host IP Address") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !connectionStatus
            )
        }

        // Game room info
        gameState?.let { room ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Game Room", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Players: ${room.players.size}/${room.maxPlayers}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Status: ${room.gameState.name}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.height(120.dp)) {
                        items(room.players) { player: Player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${player.username}${if (player.isHost) " (Host)" else ""}",
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (player.isReady) "✓ Ready" else "Not Ready",
                                    color = if (player.isReady) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (player.isReady) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ready button
                    if (room.gameState == GameState.WAITING_FOR_PLAYERS) {
                        val isReady = currentPlayer?.isReady ?: false
                        Button(
                            onClick = { gameNetworkService.setPlayerReady(!isReady) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (isReady) "Not Ready" else "Ready") }
                    }

                    // Start game button (host only)
                    val allPlayersReady = room.players.size >= 2 && room.players.all { it.isReady }
                    if (allPlayersReady && gameNetworkService.isHost) {
                        Button(
                            onClick = { gameNetworkService.startTriviaGame() },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Start Trivia Game") }
                    }
                }
            }
        }

        // Error message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(text = error, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}
*/