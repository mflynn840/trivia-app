package com.example.co_opapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.ChatMessage


@Composable
fun ChatScreen(
    currentLobbyService: CurrentLobbyService,
    modifier : Modifier

) {
    val context = LocalContext.current

    //observe reactive lobby through the service
    val lobby by currentLobbyService.lobby
    val username = SessionManager.currentPlayer?.username!!

    //observe reactive list of chat messages through the currentLobbyService
    val messages = remember(lobby) { mutableStateListOf<ChatMessage>()}

    //when the lobby changes update the shown messages
    lobby.let {
        messages.clear()
        messages.addAll(it?.chatMessages ?: emptyList())
    }

    //manage the state of the users chat input
    var chatInput by remember {mutableStateOf("")}

    //send message button handler
    fun sendMessage() {
        if(chatInput.isBlank()){
            Toast.makeText(context, "Cannot send empty message", Toast.LENGTH_SHORT).show()
            return
        }
        //send the chat message to backend using kt service layer
        currentLobbyService.sendChat(ChatMessage(username=username, message=chatInput))
        chatInput = ""
    }

    //UI for the chat messages screen
    Column(
        modifier = modifier.fillMaxSize()
    ){
        // Title for the Chat Screen
        Text(
            text = "Chat for Lobby #${lobby?.name}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
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

        // ChatInput component for user to type and send messages
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
        // OutlinedTextField for the message input
        OutlinedTextField(
            value = chatInput,
            onValueChange = onInputChange,
            label = { Text("Type a message") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp) // Padding below the text field
        )

        // Send button
        Button(
            onClick = onSend,
            modifier = Modifier.fillMaxWidth() // Take up full width
        ) {
            Text("Send")
        }
    }
}