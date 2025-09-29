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
fun CategoryDropdown(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }

    ) {
        // Display the selected category in the TextField
        TextField(
            value = selectedCategory ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category", color = Color.White) },
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
                .menuAnchor(MenuAnchorType.PrimaryNotEditable) // ✅ updated
        )


        // Dropdown menu with scroll
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
                    modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.9f))
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
