package com.example.co_opapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.CategorySelectorService

@Composable
fun QuizSetupScreen(
    modifier: Modifier = Modifier,
    onStartQuiz: (category: String, difficulty: String, numQuestions: Int) -> Unit,
    onNavigateBack: () -> Unit,
    catSelService: CategorySelectorService
) {
    // State for selections
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    var numQuestionsText by remember { mutableStateOf("5") }


    //State for question type contents in the backend
    var counts by remember { mutableStateOf<Map<String, Map<String, Long>>>(emptyMap()) }
    var categories by remember { mutableStateOf(listOf<String>()) }
    var difficulties by remember { mutableStateOf(listOf<String>()) }


    // Dropdown expanded state
    var categoryExpanded by remember { mutableStateOf(false) }
    var difficultyExpanded by remember { mutableStateOf(false) }


    // Fetch cateogries and counts from the backend on first composition
    LaunchedEffect(Unit) {
        try {

            //get the counts of each category/difficulty combination from the backend
            counts = catSelService.fetchCounts()

            //compute the lists of categories and difficulties present in db
            categories = counts.keys.toList()
            difficulties = counts.values.flatMap { it.keys }.distinct()
            selectedCategory = categories.firstOrNull()
            selectedDifficulty = difficulties.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
                value = selectedCategory ?: "",
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
                value = selectedDifficulty ?: "",
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
                    onStartQuiz(selectedCategory ?: "", selectedDifficulty ?: "" , numQuestions)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Quiz")
            }
        }
    }
}
