package com.example.co_opapp.ui.components.LobbyScreen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
    onToggleReady: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onJoin, enabled = selectedLobbyId != null) { Text("Join") }
        Button(onClick = onLeave, enabled = selectedLobbyId != null) { Text("Leave") }
        Button(onClick = onToggleReady, enabled = selectedLobbyId != null) { Text("Toggle Ready") }
    }
}
