package com.example.co_opapp.ui.components.LobbyScreen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.data_model.ChatMessage
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.Player

@Composable
fun LobbyCard(
    lobby: Lobby,
    isSelected: Boolean,
    chatMessages: List<String>,
    currentPlayer: Player?,
    onSelect: () -> Unit,
    onSendMessage: (String) -> Unit,
    onJoin: (Player) -> Unit,
    onLeave: (Player) -> Unit,
    onToggleReady: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    var chatInput by remember { mutableStateOf("") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Lobby info
            Text("Lobby #${lobby.lobbyId}", style = MaterialTheme.typography.titleMedium)
            Text("Players: ${lobby.players.size}/${lobby.maxPlayers}")
            Text("Status: ${lobby.gameState.name}")
            if (lobby.players.isNotEmpty()) {
                Column {
                    Text("Players:", style = MaterialTheme.typography.bodyMedium)
                    lobby.players.values.forEach { player ->
                        Text("${player.username}${if (player.isHost) " (Host)" else ""} ${if (player.isReady) "✓ Ready" else ""}")
                    }
                }
            }

            // Join / Leave / Ready buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                currentPlayer?.let { player ->
                    Button(onClick = { onJoin(player) }) { Text("Join") }
                    Button(onClick = { onLeave(player) }) { Text("Leave") }
                    Button(onClick = { onToggleReady(player) }) { Text("Toggle Ready") }
                }
            }

            // Chat, only if lobby is selected
            if (isSelected) {
                Column(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(chatMessages) { msg -> Text(msg) }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            label = { Text("Type a message") },
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = {
                            onSendMessage(chatInput)
                            chatInput = ""
                        }) { Text("Send") }
                    }
                }
            }
        }
    }
}
