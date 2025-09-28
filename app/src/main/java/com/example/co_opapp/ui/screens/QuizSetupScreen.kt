package com.example.co_opapp.ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.co_opapp.R
import com.example.co_opapp.Service.Hooks.CategorySelectorService
import com.example.co_opapp.ui.components.QuizSetupScreen.ActionButtons
import com.example.co_opapp.ui.components.QuizSetupScreen.CategoryDropdown
import com.example.co_opapp.ui.components.QuizSetupScreen.DifficultyDropdown
import com.example.co_opapp.ui.components.QuizSetupScreen.NumQuestionsInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    modifier: Modifier = Modifier,
    onStartQuiz: (category: String, difficulty: String, numQuestions: Int) -> Unit,
    onNavigateBack: () -> Unit,
    catSelService: CategorySelectorService
) {
    // State
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    var numQuestionsText by remember { mutableStateOf("5") }

    var counts by remember { mutableStateOf<Map<String, Map<String, Long>>>(emptyMap()) }
    var categories by remember { mutableStateOf(listOf<String>()) }
    var difficulties by remember { mutableStateOf(listOf<String>()) }

    // Fetch from backend
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

    Box(modifier = modifier.fillMaxSize()) {
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
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Quiz Setup",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Single Card containing all inputs
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Category dropdown
                    CategoryDropdown(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Difficulty dropdown
                    DifficultyDropdown(
                        difficulties = difficulties,
                        selectedDifficulty = selectedDifficulty,
                        onDifficultySelected = { selectedDifficulty = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))


                    // Number of questions input
                    NumQuestionsInput(
                        numQuestionsText = numQuestionsText,
                        onNumQuestionsChanged = { numQuestionsText = it }
                    )
                }
            }

            // Action buttons
            ActionButtons(
                onNavigateBack = onNavigateBack,
                onStartQuiz = {
                    val numQuestions = numQuestionsText.toIntOrNull() ?: 5
                    onStartQuiz(
                        selectedCategory ?: "",
                        selectedDifficulty ?: "",
                        numQuestions
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText =
        selectedCategory ?: if (categories.isEmpty()) "Loading..." else "Select category"

    Box(modifier = Modifier.fillMaxWidth().zIndex(1f)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = displayText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clickable(enabled = categories.isNotEmpty()) { expanded = true },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = true },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.9f))
                    .heightIn(max = 500.dp) // fixed dropdown size
            ) {

                    }
                }
            }
        }

