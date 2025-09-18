package com.example.co_opapp.ui.components.LobbyScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LobbyNameSelector(
    onCreateLobby: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var lobbyName by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = lobbyName,
            onValueChange = { lobbyName = it },
            label = { Text("Lobby Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onCreateLobby(lobbyName)
                lobbyName = "" // clear after creating
            },
            enabled = lobbyName.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Create Lobby")
        }
    }
}
