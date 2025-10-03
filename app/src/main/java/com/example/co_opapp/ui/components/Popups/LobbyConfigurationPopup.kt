package com.example.co_opapp.ui.components.Popups

import com.example.co_opapp.ui.components.QuizSetupScreen.CategoryDropdown
import com.example.co_opapp.ui.components.QuizSetupScreen.DifficultyDropdown
import com.example.co_opapp.ui.components.QuizSetupScreen.NumQuestionsInput
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun QuizSetupPopup(
    categories: List<String>,
    difficulties: List<String>,
    onSubmit: (category: String, difficulty: String, numQuestions: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    var numQuestionsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val numQuestions = numQuestionsText.toIntOrNull() ?: 0
                    if (selectedCategory != null && selectedDifficulty != null && numQuestions > 0) {
                        onSubmit(selectedCategory!!, selectedDifficulty!!, numQuestions)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF073A))
            ) {
                Text("Submit", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        title = {
            Text("Quiz Setup", color = Color.White)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryDropdown(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                DifficultyDropdown(
                    difficulties = difficulties,
                    selectedDifficulty = selectedDifficulty,
                    onDifficultySelected = { selectedDifficulty = it }
                )

                NumQuestionsInput(
                    numQuestionsText = numQuestionsText,
                    onNumQuestionsChanged = { numQuestionsText = it }
                )
            }
        },
        containerColor = Color.Black.copy(alpha = 0.9f)
    )
}
