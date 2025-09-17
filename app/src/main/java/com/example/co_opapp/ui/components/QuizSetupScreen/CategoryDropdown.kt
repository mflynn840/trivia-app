package com.example.co_opapp.ui.components.QuizSetupScreen

import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun CategoryDropdown(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    var categoryExpanded by remember { mutableStateOf(false) }

    Text("Select Category", style = MaterialTheme.typography.titleMedium)
    Box {
        TextField(
            value = selectedCategory ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { categoryExpanded = !categoryExpanded }
        )
        DropdownMenu(
            expanded = categoryExpanded,
            onDismissRequest = { categoryExpanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        categoryExpanded = false
                    }
                )
            }
        }
    }
}
