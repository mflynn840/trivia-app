package com.example.co_opapp.ui.components.LobbyScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.data_model.Player

@Composable
fun LobbyActions(
    selectedLobbyId: String?,
    currentPlayer: Player?,
    onJoin: () -> Unit,
    onLeave: () -> Unit,
    onToggleReady: () -> Unit,
    onShowChat: () -> Unit
) {
    // Ensure currentPlayer is non-null and selectedLobbyId is valid
    if (currentPlayer != null && selectedLobbyId != null) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Row for action buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onJoin) { Text("Join") }
                Button(onClick = onLeave) { Text("Leave") }
                Button(onClick = onToggleReady) { Text("Toggle Ready") }
            }

            // Show Chat button
            Button(onClick = onShowChat, modifier = Modifier.fillMaxWidth()) {
                Text("Show Chat")
            }
        }
    }
}
