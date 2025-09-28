package com.example.co_opapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.co_opapp.R
import com.example.co_opapp.Service.Hooks.CategorySelectorService
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
            counts = catSelService.fetchCounts()
            categories = counts.keys.toList()
            difficulties = counts.values.flatMap { it.keys }.distinct()
            selectedCategory = categories.firstOrNull()
            selectedDifficulty = difficulties.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Use a Box so the background image sits behind the content
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.quiz_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Back button at top-left
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Quiz Setup",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Dropdown Card
            SetupCard(title = "Select Category") {
                CategoryDropdown(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            // Difficulty Dropdown Card
            SetupCard(title = "Select Difficulty") {
                DifficultyDropdown(
                    difficulties = difficulties,
                    selectedDifficulty = selectedDifficulty,
                    onDifficultySelected = { selectedDifficulty = it }
                )
            }

            // Number of Questions Card
            SetupCard(title = "Number of Questions") {
                NumQuestionsInput(
                    numQuestionsText = numQuestionsText,
                    onNumQuestionsChanged = { newValue -> numQuestionsText = newValue }
                )
            }

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
            content()
        }
    }

    @Composable
    fun CategoryDropdown(
        categories: List<String>,
        selectedCategory: String?,
        onCategorySelected: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategory ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor() // attaches dropdown to this field
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.8f))
                    .heightIn(max = 250.dp) // ensures scrolling if list is long
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category, color = Color.White) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
