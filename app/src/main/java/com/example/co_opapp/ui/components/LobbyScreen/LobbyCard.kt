package com.example.co_opapp.ui.components.LobbyScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.data_model.GameState
import com.example.co_opapp.data_model.Lobby
import com.example.co_opapp.data_model.Player


fun getLobbyStatus(gameState: GameState): String {
    return when (gameState) {
        GameState.WAITING -> "Waiting"
        GameState.IN_PROGRESS -> "In Progress"
        GameState.FINISHED -> "Finished"
        GameState.WAITING_FOR_PLAYERS -> "Waiting for Players"
    }
}

@Composable
fun LobbyCard(
    lobby: Lobby,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onJoin: () -> Unit,
    onLeave: () -> Unit,
    onToggleReady: () -> Unit
) {
    // Make lobby properties reactive
    val lobbyName by remember { derivedStateOf { lobby.name } }
    val numPlayers by remember { derivedStateOf { lobby.players?.size ?: 0 } }
    val maxPlayers by remember { derivedStateOf { lobby.maxPlayers } }
    val status by remember { derivedStateOf { getLobbyStatus(lobby.gameState) } }

    // Define a color based on selection status
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    // Column to arrange all the elements
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
            .clickable { onSelect() }
    ) {
        // Lobby name
        Text(
            text = "Lobby: $lobbyName",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )

        // Number of players
        Text(
            text = "Players: $numPlayers/$maxPlayers",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        // Status
        Text(
            text = "Status: $status",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        // Actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onJoin) { Text("Join") }
            Button(onClick = onLeave) { Text("Leave") }
            Button(onClick = onToggleReady) { Text("Toggle Ready") }
        }
    }
}
