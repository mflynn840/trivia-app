package com.example.co_opapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.CategorySelectorService
import com.example.co_opapp.ui.components.QuizSetupScreen.ActionButtons
import com.example.co_opapp.ui.components.QuizSetupScreen.CategoryDropdown
import com.example.co_opapp.ui.components.QuizSetupScreen.DifficultyDropdown
import com.example.co_opapp.ui.components.QuizSetupScreen.NumQuestionsInput

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

    // State for question type contents in the backend
    var counts by remember { mutableStateOf<Map<String, Map<String, Long>>>(emptyMap()) }
    var categories by remember { mutableStateOf(listOf<String>()) }
    var difficulties by remember { mutableStateOf(listOf<String>()) }

    // Fetch categories and counts from the backend on first composition
    LaunchedEffect(Unit) {
        try {
            // Get the counts of each category/difficulty combination from the backend
            counts = catSelService.fetchCounts()

            // Compute the lists of categories and difficulties present in db
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
        // Category Dropdown
        CategoryDropdown(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        // Difficulty Dropdown
        DifficultyDropdown(
            difficulties = difficulties,
            selectedDifficulty = selectedDifficulty,
            onDifficultySelected = { selectedDifficulty = it }
        )

        // Number of Questions Input
        NumQuestionsInput(
            numQuestionsText = numQuestionsText,
            onNumQuestionsChanged = { newValue -> numQuestionsText = newValue }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        ActionButtons(
            onNavigateBack = onNavigateBack,
            onStartQuiz = {
                val numQuestions = numQuestionsText.toIntOrNull() ?: 5
                onStartQuiz(selectedCategory ?: "", selectedDifficulty ?: "", numQuestions)
            }
        )
    }
}
