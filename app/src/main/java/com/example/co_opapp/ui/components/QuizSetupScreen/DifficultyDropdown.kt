package com.example.co_opapp.ui.components.QuizSetupScreen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun DifficultyDropdown(
    difficulties: List<String>,
    selectedDifficulty: String?,
    onDifficultySelected: (String) -> Unit
) {
    var difficultyExpanded by remember { mutableStateOf(false) }

    Text("Select Difficulty", style = MaterialTheme.typography.titleMedium)
    Box {
        TextField(
            value = selectedDifficulty ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Difficulty") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { difficultyExpanded = !difficultyExpanded }
        )
        DropdownMenu(
            expanded = difficultyExpanded,
            onDismissRequest = { difficultyExpanded = false }
        ) {
            difficulties.forEach { difficulty ->
                DropdownMenuItem(
                    text = { Text(difficulty) },
                    onClick = {
                        onDifficultySelected(difficulty)
                        difficultyExpanded = false
                    }
                )
            }
        }
    }
}
