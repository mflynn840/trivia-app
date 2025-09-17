package com.example.co_opapp.ui.components.LobbyScreen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LobbyChat(
    messages: List<String>,
    chatInput: String,
    onChatInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column() {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(messages) { msg -> Text(msg) }
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = onChatInputChange,
                label = { Text("Type a message") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onSendMessage, modifier = Modifier.padding(start = 8.dp)) {
                Text("Send")
            }
        }
    }
}
