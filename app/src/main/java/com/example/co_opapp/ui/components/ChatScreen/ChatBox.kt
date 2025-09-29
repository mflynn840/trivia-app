package com.example.co_opapp.ui.components.ChatScreen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.ChatMessage



@Composable
fun ChatBox(
    currentLobbyService: CurrentLobbyService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val username = SessionManager.currentPlayer?.username ?: "Unknown"
    val messages = currentLobbyService.chatMessages
    var chatInput by remember { mutableStateOf("") }

    fun sendMessage() {
        if (chatInput.isBlank()) {
            Toast.makeText(context, "Cannot send empty message", Toast.LENGTH_SHORT).show()
            return
        }
        currentLobbyService.sendChat(ChatMessage(username = username, message = chatInput))
        chatInput = ""
    }

    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messages) { message ->
                Text("${message.username}: ${message.message}")
            }
        }

        ChatInput(
            chatInput = chatInput,
            onInputChange = { chatInput = it },
            onSend = { sendMessage() }
        )
    }
}


@Composable
fun ChatInput(
    chatInput: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = chatInput,
            onValueChange = onInputChange,
            label = { Text("Type a message") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Button(
            onClick = onSend,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send")
        }
    }
}

