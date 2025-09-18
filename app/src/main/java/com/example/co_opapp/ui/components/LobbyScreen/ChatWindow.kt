package com.example.co_opapp.ui.components.LobbyScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


// Chat Dialog Composable
@Composable
fun ChatWindow(
    lobbyId: String,
    messages: List<String>,
    chatInput: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chat for Lobby #$lobbyId") },
        text = {
            Column {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(messages) { message ->
                        Text(message)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = chatInput,
                    onValueChange = onInputChange,
                    label = { Text("Type a message") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onSend,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
