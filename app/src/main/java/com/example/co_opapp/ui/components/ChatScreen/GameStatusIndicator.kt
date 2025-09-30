package com.example.co_opapp.ui.components.ChatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.CurrentLobbyService


@Composable
fun GameStatusIndicator(
    currentLobbyService: CurrentLobbyService,
    modifier: Modifier = Modifier
) {


    //i need to get lobby.gameState.gameStatus which is an enum (i want ot get it as a string)
    val gameStatus = currentLobbyService.gameStatus.value
    val statusText = gameStatus!!.name

    Box(
        modifier = modifier
            .background( Color(0xFF4CAF50) )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText,
            color = Color.White
        )
    }
}