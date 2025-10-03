package com.example.co_opapp.ui.layouts

import LobbyList
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.co_opapp.R
import com.example.co_opapp.Service.Backend.AvailableLobbiesService
import com.example.co_opapp.Service.Hooks.CategorySelectorService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.CreateLobbyRequest
import com.example.co_opapp.ui.components.LobbyScreen.*


/**
 * Allow the user to select a lobby to join
 */
@Composable
fun LobbySelectorScreen(
    availableLobbiesService: AvailableLobbiesService,
    modifier: Modifier = Modifier,
    onNavigateToLobby: (String) -> Unit,
    onNavigateBack: () -> Unit,
    catSelService: CategorySelectorService
) {
    val player = SessionManager.currentPlayer

    var lobbies by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedLobbyName by remember { mutableStateOf<String?>(null) }
    var shouldRefreshLobbies by remember { mutableStateOf(true) }
    var lobbyToCreate by remember { mutableStateOf<CreateLobbyRequest?>(null) }

    LaunchedEffect(shouldRefreshLobbies) {
        if (shouldRefreshLobbies) {
            try {
                lobbies = availableLobbiesService.getAvailableLobbies()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                shouldRefreshLobbies = false
            }
        }
    }

    LaunchedEffect(lobbyToCreate) {
        lobbyToCreate?.let { request ->
            try {
                val success = availableLobbiesService.createLobby(
                    lobbyName = request.name,
                    category = request.category,
                    difficulty = request.difficulty,
                    numQuestions = request.numQuestions,
                )
                if (success) shouldRefreshLobbies = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                lobbyToCreate = null
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.chat_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(38.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                modifier = modifier
                    .fillMaxSize()

            ) {
                Text(
                    text = "Create a Lobby",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                CreateLobbyUi(
                    onCreateLobby = {name, category, difficulty, numQuestions ->
                        lobbyToCreate = CreateLobbyRequest(name=name, difficulty=difficulty,
                                                            category=category,numQuestions=numQuestions)
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    catSelService = catSelService
                )

                LobbyList(
                    lobbyNames = lobbies,
                    selectedLobbyName = selectedLobbyName.orEmpty(),
                    onLobbySelect = { lobbyName -> selectedLobbyName = lobbyName },
                    onJoinLobby = { lobbyName -> onNavigateToLobby(lobbyName) }
                )
            }
        }

        // Floating refresh button pinned at bottom center
        NeonRefreshButton(
            onClick = { shouldRefreshLobbies = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 125.dp)
        )
    }
}
