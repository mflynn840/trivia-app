package com.example.co_opapp.ui.layouts

import CurrentPlayers
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.ui.components.ChatScreen.BackButton
import com.example.co_opapp.ui.components.ChatScreen.ChatBox
import androidx.compose.ui.graphics.Color
import com.example.co_opapp.ui.components.ChatScreen.GameStatusIndicator
import com.example.co_opapp.ui.components.ChatScreen.ReadyButton


@Composable
fun ChatScreen(
    currentLobbyService: CurrentLobbyService,
    modifier : Modifier,
    onNavigateBack: () -> Unit = {}

) {
    //observe reactive lobby through the service
    val lobby by currentLobbyService.lobby

    //UI for the chat messages screen
    Column(
        modifier = modifier.fillMaxSize()
    ){

        // Back button (top-left)
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Title for the Chat Screen
        Text(
            text = "Chat for Lobby #${lobby?.name}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        // Game Status Indicator
        GameStatusIndicator(
            currentLobbyService = currentLobbyService,
            modifier = Modifier.weight(0.1f)
        )

        //Show players in the lobby
        CurrentPlayers(
            currentLobbyService = currentLobbyService,
            modifier = Modifier.weight(0.2f)
                .fillMaxWidth()

        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray // or MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )


        // Display chat
        ChatBox(
            currentLobbyService = currentLobbyService,
            modifier = Modifier.weight(0.6f)
                .fillMaxWidth()
        )

        ReadyButton(
            currentLobbyService = currentLobbyService,
            modifier = Modifier.weight(0.1f)
        )

    }
}
