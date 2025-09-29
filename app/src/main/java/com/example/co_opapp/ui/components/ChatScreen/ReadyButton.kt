package com.example.co_opapp.ui.components.ChatScreen


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.SessionManager

@Composable
fun ReadyButton(
    currentLobbyService: CurrentLobbyService,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            val lobbyName = currentLobbyService.lobby.value?.name
            val player = SessionManager.currentPlayer
            if (lobbyName != null && player != null) {
                currentLobbyService.toggleReady(lobbyName, player.username)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
    ) {
        Text("Ready")
    }
}