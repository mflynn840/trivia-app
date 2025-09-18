package com.example.co_opapp.Service

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.data_model.ChatMessage

@Composable
fun ChatWindow(
    lobbyId: String,
    messages: List<String>,
    chatInput: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
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
                    onClick = onSendMessage,
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
