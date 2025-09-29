package com.example.co_opapp.ui.components.QuizSetupScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultyDropdown(
    difficulties: List<String>,
    selectedDifficulty: String?,
    onDifficultySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }

    ) {

        TextField(
            value = selectedDifficulty ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Difficulty", color = Color.White) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.Gray,
                focusedContainerColor = Color.Black.copy(alpha = 0.6f),
                unfocusedContainerColor = Color.Black.copy(alpha = 0.6f),
                disabledContainerColor = Color.Black.copy(alpha = 0.3f),
                cursorColor = Color.White,
                focusedIndicatorColor = Color(0xFFFF073A),
                unfocusedIndicatorColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable) // ✅ fixed
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.9f))
        ) {
            difficulties.forEach { difficulty ->
                DropdownMenuItem(
                    text = { Text(difficulty, color = Color.White) },
                    onClick = {
                        onDifficultySelected(difficulty)
                        expanded = false
                    }
                )
            }
        }
    }
}
