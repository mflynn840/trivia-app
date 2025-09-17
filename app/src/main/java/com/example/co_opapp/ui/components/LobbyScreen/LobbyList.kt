package com.example.co_opapp.ui.components.LobbyScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.co_opapp.data_model.Lobby

@Composable
fun LobbyList(
    lobbies: List<Lobby>,
    selectedLobbyId: String?,
    onSelectLobby: (Lobby) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp),) {
        items(lobbies) { lobby ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectLobby(lobby) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedLobbyId == lobby.lobbyId)
                        MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lobby #${lobby.lobbyId}", fontWeight = FontWeight.Bold)
                    Text("Players: ${lobby.players.size}/${lobby.maxPlayers}")
                    Text("Status: ${lobby.gameState.name}")
                    if (lobby.players.isNotEmpty()) {
                        Text("Players:", fontWeight = FontWeight.SemiBold)
                        lobby.players.values.forEach { player ->
                            Text("${player.username}${if (player.isHost) " (Host)" else ""} ${if (player.isReady) "✓ Ready" else ""}")
                        }
                    }
                }
            }
        }
    }
}
