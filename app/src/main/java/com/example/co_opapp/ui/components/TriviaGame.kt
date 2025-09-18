package com.example.co_opapp.ui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.Service.CategorySelectorService
import com.example.co_opapp.Service.LobbyWebSocketService
import com.example.co_opapp.Service.ProfilePictureService
import com.example.co_opapp.ui.screens.GameModeScreen
import com.example.co_opapp.ui.screens.QuizScreen
import com.example.co_opapp.ui.screens.LobbyScreen
//import com.example.co_opapp.Service.RaceModeGameService
import com.example.co_opapp.Service.SoloGameService
import com.example.co_opapp.ui.screens.CharacterCustomizationScreen
import com.example.co_opapp.ui.screens.QuizSetupScreen
import com.example.co_opapp.ui.screens.LoadingScreen
import android.content.Intent
import com.example.co_opapp.ui.screens.LoginScreenWithMusicWrapper


@Composable
fun TriviaGame() {

    // The main loop contains a controller for switching between pages
    val navController = rememberNavController()
    val context = LocalContext.current
    val authService = remember { AuthService(context) }

    // Create the services for running a solo or co-op game later
    var soloService by remember { mutableStateOf<SoloGameService?>(null) }

    // ProfilePictureService will be created **after login**
    var profilePictureService by remember { mutableStateOf<ProfilePictureService?>(null) }

    // Start on the login page
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // When the player completes login, go to the "select game-mode" screen
        composable("login") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LoginScreenWithMusicWrapper(
                    authService = authService,
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToLobby = {
                        profilePictureService = ProfilePictureService(authService, context)
                        navController.navigate("gameMode")
                    }
                )
            }
        }

        // When the player selects a game mode, go to that game mode
        composable("gameMode") {
            val service = profilePictureService
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                if (service != null) {
                    GameModeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToSinglePlayer = { navController.navigate("soloQuizSetup") },
                        onNavigateToCoOp = { navController.navigate("lobby") },
                        onNavigateToCharacterMode = { navController.navigate("characterCustomization") },
                        onNavigateBack = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        profilePictureService = service
                    )
                } else {
                    LoadingScreen()
                }
            }
        }

        // Ask the player which category and difficulty
        composable("soloQuizSetup") {
            val categorySelectorService =
                CategorySelectorService(context, authService.getJwtToken()!!)

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                QuizSetupScreen(
                    modifier = Modifier.padding(innerPadding),
                    onStartQuiz = { category, difficulty, _ ->
                        // Pass the selected options to the quiz driver and create it
                        soloService = SoloGameService(
                            authService = authService,
                            category = category,
                            difficulty = difficulty,
                        )
                        navController.navigate("singlePlayerQuiz")
                    },
                    onNavigateBack = { navController.popBackStack() },
                    catSelService = categorySelectorService
                )
            }
        }

        // Single player quiz game is a skeleton supplied with the SoloGameService
        composable("singlePlayerQuiz") {
            val service = soloService
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                if (service != null) {
                    QuizScreen(
                        modifier = Modifier.padding(innerPadding),
                        quizService = service, // inject the service
                        onNavigateBack = {
                            navController.navigate("gameMode") {
                                popUpTo("gameMode") { inclusive = true }
                            }
                        },
                        onGameComplete = { score, total ->
                            // Handle completion for single player
                        }
                    )
                } else {
                    LoadingScreen()
                }
            }
        }

        // Lobby for co-op
        composable("lobby") {
            val lobbyService = remember { LobbyWebSocketService() }

            // Launch the connection when this composable enters the composition
            LaunchedEffect(Unit) {
                lobbyService.connect()
            }

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LobbyScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateBack = {
                        navController.navigate("gameMode") {
                            popUpTo("gameMode") { inclusive = true }
                        }
                    },
                    onNavigateToGame = { navController.navigate("coopQuiz") },
                    lobbyService = lobbyService,
                    authService = authService
                )
            }
        }

        /*
        composable("coopQuiz") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                QuizScreen(
                    modifier = Modifier.padding(innerPadding),
                    quizService = coopService,
                    onNavigateBack = {
                        navController.navigate("gameMode") {
                            popUpTo("gameMode") { inclusive = true }
                        }
                    },
                    onGameComplete = { score, total ->
                        // show results, maybe navigate back to menu
                    }
                )
            }
        }
        */

        // Character customization
        composable("characterCustomization") {
            val service = profilePictureService
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                if (service != null) {
                    CharacterCustomizationScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateBack = { navController.popBackStack() },
                        profilePictureService = service
                    )
                } else {
                    LoadingScreen()
                }
            }
        }
    }
}
