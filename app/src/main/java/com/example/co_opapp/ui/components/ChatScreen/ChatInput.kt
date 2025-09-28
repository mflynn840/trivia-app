package com.example.co_opapp.ui.components.ChatScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


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