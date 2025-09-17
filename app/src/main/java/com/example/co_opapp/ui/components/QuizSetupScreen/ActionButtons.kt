package com.example.co_opapp.ui.components.QuizSetupScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

@Composable
fun ActionButtons(
    onNavigateBack: () -> Unit,
    onStartQuiz: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onNavigateBack,
            modifier = Modifier.weight(1f)
        ) {
            Text("Back")
        }

        Button(
            onClick = onStartQuiz,
            modifier = Modifier.weight(1f)
        ) {
            Text("Start Quiz")
        }
    }
}
