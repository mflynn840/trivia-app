package com.example.co_opapp.ui.components.LobbyScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.Player
import com.example.co_opapp.data_model.PlayerDTO
@Composable
fun LobbyCard(
    lobby: Lobby,
    isSelected: Boolean,
    currentPlayer: Player?,
    onSelect: () -> Unit,
    onJoin: (PlayerDTO) -> Unit,
    onLeave: (PlayerDTO) -> Unit,
    onToggleReady: (PlayerDTO) -> Unit,
    modifier: Modifier = Modifier
) {
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
            // Lobby information
            Text("Lobby #${lobby.lobbyId}", style = MaterialTheme.typography.titleMedium)
            Text("Players: ${lobby.players.size}/${lobby.maxPlayers}")
            Text("Status: ${lobby.gameState.name}")

            // Display players in the lobby
            if (lobby.players.isNotEmpty()) {
                Column {
                    Text("Players:", style = MaterialTheme.typography.bodyMedium)
                    lobby.players.values.forEach { player ->
                        Text(" ${player.username} ${if (player.isReady) "✓ Ready" else ""}")
                    }
                }
            }

            // Integrating LobbyActions component
            currentPlayer?.let { player ->
                LobbyActions(
                    selectedLobbyId = lobby.lobbyId,
                    currentPlayer = player,
                    onJoin = { onJoin(PlayerDTO(player.sessionId, player.username)) },
                    onLeave = { onLeave(PlayerDTO(player.sessionId, player.username)) },
                    onToggleReady = { onToggleReady(PlayerDTO(player.sessionId, player.username)) }
                )
            }
        }
    }
}
