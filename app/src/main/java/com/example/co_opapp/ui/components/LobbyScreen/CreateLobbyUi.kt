package com.example.co_opapp.ui.components.LobbyScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Hooks.CategorySelectorService
import com.example.co_opapp.ui.components.Popups.QuizSetupPopup

@Composable
fun CreateLobbyUi(
    onCreateLobby: (String, String, String, Int) -> Unit, // pass all lobby info
    modifier: Modifier = Modifier,
    catSelService: CategorySelectorService
) {
    var lobbyName by remember { mutableStateOf("") }
    var showPopup by remember { mutableStateOf(false) }

    // store categories, difficulties and counts from backend
    var counts by remember { mutableStateOf<Map<String, Map<String, Long>>>(emptyMap()) }
    var categories by remember { mutableStateOf(listOf<String>()) }
    var difficulties by remember { mutableStateOf(listOf<String>()) }

    // store lobby settings locally
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    var numQuestions by remember { mutableStateOf(0) }




    // Fetch categories, difficulties and counts from backend
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


    if (showPopup) {
        QuizSetupPopup(
            categories = categories,
            difficulties = difficulties,
            onSubmit = { category, difficulty, numQs ->
                selectedCategory = category
                selectedDifficulty = difficulty
                numQuestions = numQs
                showPopup = false
            },
            onDismiss = { showPopup = false }
        )
    }

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = lobbyName,
            onValueChange = { lobbyName = it },
            label = { Text("Lobby Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Open lobby settings popup
        Button(
            onClick = { showPopup = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lobby Settings")
        }

        Spacer(Modifier.height(8.dp))

        // Create lobby button
        Button(
            onClick = {
                if (selectedCategory != null && selectedDifficulty != null && numQuestions > 0) {
                    onCreateLobby(lobbyName, selectedCategory!!, selectedDifficulty!!, numQuestions)
                    lobbyName = "" // reset
                }
            },
            enabled = lobbyName.isNotBlank() && selectedCategory != null && selectedDifficulty != null && numQuestions > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Lobby")
        }
    }
}
