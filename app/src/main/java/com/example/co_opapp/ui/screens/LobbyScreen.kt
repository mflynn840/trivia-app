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
import com.example.co_opapp.Service.RaceModeGameService

import com.example.co_opapp.data_model.GameState
import com.example.co_opapp.data_model.Player

@Composable
fun LobbyScreen(
    gameService : RaceModeGameService,
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val currentPlayer by remember { derivedStateOf { gameService.getMyPlayer() } }
    var username by remember { mutableStateOf(currentPlayer?.username ?: "") }
    var hostIp by remember { mutableStateOf("") }
    var isHosting by remember { mutableStateOf(false) }
    var isJoining by remember { mutableStateOf(false) }

    val gameState by gameService.gameState.collectAsState()
    val connectionStatus by gameService.connectionStatus.collectAsState()
    val errorMessage by gameService.errorMessage.collectAsState()

    LaunchedEffect(gameState) {
        if (gameState?.gameState == GameState.IN_PROGRESS){
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
            Text("Co-op Game Lobby", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
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

        // Local IP
        gameService.getLocalIpAddress()?.let { localIp ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Local IP:", style = MaterialTheme.typography.labelMedium)
                    Text(localIp, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
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
                            gameService.startHosting(
                                player = Player(id = currentPlayer?.id?:0L, username = username),
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
                            gameService.joinGame(
                                player = Player(id = currentPlayer?.id ?: 0L,username = username),
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
                    Text("Game Room", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Players: ${room.players.size}/${room.maxPlayers}", style = MaterialTheme.typography.bodyMedium)
                    Text("Status: ${room.gameState.name}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.height(120.dp)) {
                        items(room.players) { player ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${player.username}${if (player.isHost) " (Host)" else ""}", fontSize = 16.sp)
                                Text(
                                    if (player.isReady) "✓ Ready" else "Not Ready",
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
                            onClick = { gameService.setPlayerReady(!isReady) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (isReady) "Not Ready" else "Ready") }
                    }

                    // Start game button (host only)
                    val allPlayersReady = room.players.size >= 2 && room.players.all { it.isReady }
                    if (allPlayersReady && gameService.isHost) {
                        Button(
                            onClick = { gameService.startTriviaGame() },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Start Trivia Game") }
                    }
                }
            }
        }

        // Error
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
