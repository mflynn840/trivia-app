package com.example.co_opapp

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

@Composable
fun LobbyScreen(
    modifier: Modifier = Modifier,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val gameNetworkService = remember { GameNetworkService() }
    val currentPlayer by remember { derivedStateOf { gameNetworkService.getMyPlayer() } }
    var username by remember { mutableStateOf(currentPlayer?.username ?: "") }

    //update user name if it changes
    LaunchedEffect(currentPlayer) {
        currentPlayer?.username?.let {
            username = it
        }
    }

    var hostIp by remember { mutableStateOf("") }
    var isHosting by remember { mutableStateOf(false) }
    var isJoining by remember { mutableStateOf(false) }
    
    val gameState by gameNetworkService.gameState.collectAsState()
    val connectionStatus by gameNetworkService.connectionStatus.collectAsState()
    val errorMessage by gameNetworkService.errorMessage.collectAsState()
    
    // Show error message
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Error will be cleared automatically
        }
    }
    
    // Navigate to game when it starts
    LaunchedEffect(gameState) {
        if (gameState?.isGameStarted == true) {
            onNavigateToGame()
        }
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
            Button(onClick = onNavigateBack) {
                Text("Logout")
            }
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
        val localIp = gameNetworkService.getLocalIpAddress()
        if (localIp != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Your Local IP:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = localIp,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        if (!connectionStatus) {
            // Host/Join buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        if (username.isNotBlank()) {
                            isHosting = true
                            gameNetworkService.startHosting(
                                player = Player(username = username),
                                onSuccess = { isHosting = false },
                                onError = { error -> 
                                    isHosting = false
                                    // Error will be shown via errorMessage state
                                }
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isHosting && !isJoining && username.isNotBlank()
                ) {
                    if (isHosting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Host Game")
                    }
                }
                
                Button(
                    onClick = {
                        if (username.isNotBlank() && hostIp.isNotBlank()) {
                            isJoining = true
                            gameNetworkService.joinGame(
                                player = Player(username = username),
                                hostIp = hostIp,
                                onSuccess = { isJoining = false },
                                onError = { error ->
                                    isJoining = false
                                    // Error will be shown via errorMessage state
                                }
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isHosting && !isJoining && username.isNotBlank() && hostIp.isNotBlank()
                ) {
                    if (isJoining) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Join Game")
                    }
                }
            }
            
            // Host IP input for joining
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
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Game Room",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Players: ${room.players.size}/${room.maxPlayers}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Status: ${room.gameState.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Players list
                    LazyColumn(
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(room.players) { player ->
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
                                
                                if (player.isReady) {
                                    Text(
                                        text = "✓ Ready",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = "Not Ready",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Ready button
                    if (room.gameState == GameState.WAITING_FOR_PLAYERS) {
                        val currentPlayer = gameNetworkService.getMyPlayer()
                        val isReady = currentPlayer?.isReady ?: false
                        
                        Button(
                            onClick = {
                                gameNetworkService.setPlayerReady(!isReady)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isReady) "Not Ready" else "Ready")
                        }
                    }
                    
                    // Start game button (host only)
                    val allPlayersReady = room.players.size >= 2 && room.players.all { it.isReady }
                    if (allPlayersReady && gameNetworkService.isHost) {
                        Button(
                            onClick = {
                                gameNetworkService.startTriviaGame()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Trivia Game")
                        }
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
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
