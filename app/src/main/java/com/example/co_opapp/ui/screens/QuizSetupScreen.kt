package com.example.co_opapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun QuizSetupScreen(
    modifier: Modifier = Modifier,
    onStartQuiz: (category: String, difficulty: String, numQuestions: Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    // State for selections
    var selectedCategory by remember { mutableStateOf("General") }
    var selectedDifficulty by remember { mutableStateOf("Easy") }
    var numQuestionsText by remember { mutableStateOf("5") }

    val categories = listOf("General", "Science", "History", "Math", "Sports")
    val difficulties = listOf("Easy", "Medium", "Hard")

    // Dropdown expanded state
    var categoryExpanded by remember { mutableStateOf(false) }
    var difficultyExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Category dropdown
        Text("Select Category", style = MaterialTheme.typography.titleMedium)
        Box {
            TextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { categoryExpanded = true }
            )
            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        // Difficulty dropdown
        Text("Select Difficulty", style = MaterialTheme.typography.titleMedium)
        Box {
            TextField(
                value = selectedDifficulty,
                onValueChange = {},
                readOnly = true,
                label = { Text("Difficulty") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { difficultyExpanded = true }
            )
            DropdownMenu(
                expanded = difficultyExpanded,
                onDismissRequest = { difficultyExpanded = false }
            ) {
                difficulties.forEach { difficulty ->
                    DropdownMenuItem(
                        text = { Text(difficulty) },
                        onClick = {
                            selectedDifficulty = difficulty
                            difficultyExpanded = false
                        }
                    )
                }
            }
        }

        // Number of questions input
        Text("Number of Questions", style = MaterialTheme.typography.titleMedium)
        TextField(
            value = numQuestionsText,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) numQuestionsText = newValue
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Questions") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Buttons
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
                onClick = {
                    val numQuestions = numQuestionsText.toIntOrNull() ?: 5
                    onStartQuiz(selectedCategory, selectedDifficulty, numQuestions)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Quiz")
            }
        }
    }
}
