package com.example.co_opapp.ui.components.QuizSetupScreen


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumQuestionsInput(
    numQuestionsText: String,
    onNumQuestionsChanged: (String) -> Unit
) {
    // Title above the field
    Text(
        "Number of Questions",
        style = MaterialTheme.typography.titleMedium,
        color = Color.White // match dropdown titles
    )

    TextField(
        value = numQuestionsText,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) onNumQuestionsChanged(newValue)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text("Questions", color = Color.White) }, // white label
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledTextColor = Color.Gray,
            focusedContainerColor = Color.Black.copy(alpha = 0.6f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.6f),
            disabledContainerColor = Color.Black.copy(alpha = 0.3f),
            cursorColor = Color.White,
            focusedIndicatorColor = Color(0xFFFF073A), // neon red
            unfocusedIndicatorColor = Color.Gray
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
