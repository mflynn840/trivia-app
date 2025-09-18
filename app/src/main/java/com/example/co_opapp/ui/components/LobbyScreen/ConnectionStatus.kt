package com.example.co_opapp.ui.components.LobbyScreen



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionStatusIndicator(
    connected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(if (connected) Color(0xFF4CAF50) else Color(0xFFF44336))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (connected) "Connected" else "Disconnected",
            color = Color.White
        )
    }
}
