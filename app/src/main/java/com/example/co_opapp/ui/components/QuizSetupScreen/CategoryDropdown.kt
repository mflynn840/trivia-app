package com.example.co_opapp.ui.components.QuizSetupScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Exposed Dropdown
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },  // Manage dropdown expanded state
    ) {
        // Display the selected category in the TextField
        TextField(
            value = selectedCategory ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()  // Link the dropdown to the TextField
        )

        // DropdownMenu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // Close dropdown when clicked outside
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)  // Notify the parent when a category is selected
                        expanded = false  // Close the dropdown after selection
                    }
                )
            }
        }
    }
}
