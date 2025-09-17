package com.example.co_opapp.ui.components.QuizSetupScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultyDropdown(
    difficulties: List<String>,
    selectedDifficulty: String?,
    onDifficultySelected: (String) -> Unit
) {
    var difficultyExpanded by remember { mutableStateOf(false) }

    // Display title for the dropdown
    Text("Select Difficulty", style = MaterialTheme.typography.titleMedium)

    // ExposedDropdownMenu will automatically handle click behavior for opening/closing the menu
    ExposedDropdownMenuBox(
        expanded = difficultyExpanded,  // Toggle visibility based on state
        onExpandedChange = { difficultyExpanded = it },  // Automatically manages expanded state
        modifier = Modifier.fillMaxWidth()
    ) {
        // Display the selected difficulty in the TextField
        TextField(
            value = selectedDifficulty ?: "",  // Show selected difficulty or empty string
            onValueChange = {},  // No input allowed, just selection from the dropdown
            readOnly = true,  // Set to read-only, as the user will pick from the list
            label = { Text("Difficulty") },
            modifier = Modifier.fillMaxWidth()  // Ensure the TextField takes full width
        )

        // ExposedDropdownMenu shows the list of difficulties when expanded
        ExposedDropdownMenu(
            expanded = difficultyExpanded,
            onDismissRequest = { difficultyExpanded = false }  // Close dropdown when clicked outside
        ) {
            difficulties.forEach { difficulty ->
                DropdownMenuItem(
                    text = { Text(difficulty) },
                    onClick = {
                        onDifficultySelected(difficulty)  // Notify the parent when a difficulty is selected
                        difficultyExpanded = false  // Close the dropdown after selection
                    }
                )
            }
        }
    }
}
