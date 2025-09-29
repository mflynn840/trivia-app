package com.example.co_opapp.ui.layouts

import LobbyList
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.co_opapp.R
import com.example.co_opapp.Service.Backend.AvailableLobbiesService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.ui.components.LobbyScreen.*


/**
 * Allow the user to select a lobby to join
 */
@Composable
fun LobbySelectorScreen(
    availableLobbiesService: AvailableLobbiesService,
    modifier: Modifier = Modifier,
    onNavigateToLobby: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val player = SessionManager.currentPlayer

    // Lobbies state
    var lobbies by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedLobbyName by remember { mutableStateOf<String?>(null) }

    // Listen for user create lobby and refresh lobbies
    var shouldRefreshLobbies by remember { mutableStateOf(true) }
    var lobbyToCreate by remember { mutableStateOf<String?>(null) }

    // Fetch lobbies when requested
    LaunchedEffect(shouldRefreshLobbies) {
        if (shouldRefreshLobbies) {
            try {
                val fetchedLobbies = availableLobbiesService.getAvailableLobbies()
                lobbies = fetchedLobbies
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                shouldRefreshLobbies = false
            }
        }
    }

    // Create a lobby when requested
    LaunchedEffect(lobbyToCreate) {
        lobbyToCreate?.let { name ->
            try {
                val success = availableLobbiesService.createLobby(name)
                if (success) {
                    shouldRefreshLobbies = true // trigger reload
                } else {
                    println("Failed to create lobby.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                lobbyToCreate = null // reset
            }
        }
    }

    // Background image
    Image(
        painter = painterResource(id = R.drawable.coop_background),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    // 🔙 Back Button

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
            .padding(16.dp)
    ) {

        Text("Select/Create a Lobby", style = MaterialTheme.typography.headlineMedium)

        // Create a lobby button and name selector component
        CreateLobbyUi(
            onCreateLobby = { name ->
                lobbyToCreate = name // trigger lobby creation
            },
            modifier = Modifier.padding(top = 8.dp)
        )

        //Refresh lobbies button
        RefreshButton(
            onNavigateBack = {
                shouldRefreshLobbies = true
            }
        )

        LobbyList(
            lobbyNames = lobbies,
            selectedLobbyName = selectedLobbyName.orEmpty(),
            onLobbySelect = { lobbyName -> selectedLobbyName = lobbyName },
            onJoinLobby = { lobbyName ->
                onNavigateToLobby(lobbyName)
            },
        )
    }
}
