package com.example.co_opapp.ui.components.QuizSetupScreen


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumQuestionsInput(
    numQuestionsText: String,
    onNumQuestionsChanged: (String) -> Unit
) {
    Text("Number of Questions", style = MaterialTheme.typography.titleMedium)
    TextField(
        value = numQuestionsText,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) onNumQuestionsChanged(newValue)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text("Questions") },
        modifier = Modifier.fillMaxWidth()
    )
}
