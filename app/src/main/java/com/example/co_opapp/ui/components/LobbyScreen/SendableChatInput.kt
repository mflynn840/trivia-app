package com.example.co_opapp.ui.components.LobbyScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SendableChatInput(
    lobbyId: String,
    messages: List<String>,
    onSendMessage: (String) -> Unit
) {
    // Chat input state
    val chatInput = remember { mutableStateOf("") }

    Column {
        // Chat Display
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                Text(message)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Input Field
        OutlinedTextField(
            value = chatInput.value,
            onValueChange = { chatInput.value = it },
            label = { Text("Type a message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Send Button
        Button(
            onClick = {
                if (chatInput.value.isNotEmpty()) {
                    onSendMessage(chatInput.value)
                    chatInput.value = "" // Reset chat input after sending
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send")
        }
    }
}
