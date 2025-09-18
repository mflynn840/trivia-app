package com.example.co_opapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.co_opapp.Service.Backend.AuthService
import com.example.co_opapp.Service.Hooks.CategorySelectorService
import com.example.co_opapp.Service.Coop.LobbyService
import com.example.co_opapp.Service.Backend.ProfileService
import com.example.co_opapp.ui.screens.GameModeScreen
import com.example.co_opapp.ui.screens.QuizScreen
import com.example.co_opapp.ui.screens.LobbyScreen
//import com.example.co_opapp.Service.RaceModeGameService
import com.example.co_opapp.Service.Backend.SoloGameService
import com.example.co_opapp.ui.components.MusicWrapper
import com.example.co_opapp.ui.screens.CharacterCustomizationScreen
import com.example.co_opapp.ui.screens.QuizSetupScreen
import com.example.co_opapp.ui.screens.LoadingScreen
import com.example.co_opapp.ui.screens.LoginScreen

@Composable
fun TriviaGame() {
    val context = LocalContext.current
    val authService = remember { AuthService(context) }
    var soloService by remember { mutableStateOf<SoloGameService?>(null) }
    var profilePictureService by remember { mutableStateOf<ProfileService?>(null) }

    val navController = rememberNavController()

    MusicWrapper(musicResId = R.raw.login_music) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        authService = authService,
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToLobby = {
                            profilePictureService = ProfileService(authService, context)
                            navController.navigate("gameMode")
                        }
                    )
                }
            }

            composable("gameMode") {
                val service = profilePictureService
                if (service != null) {
                    GameModeScreen(
                        profilePictureService = service,
                        onNavigateToSinglePlayer = { navController.navigate("soloQuizSetup") },
                        onNavigateToCoOp = { navController.navigate("lobby") },
                        onNavigateToCharacterMode = { navController.navigate("characterCustomization") },
                        onNavigateBack = {
                            navController.navigate("login") {
                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                        }
                    )
                } else {
                    LoadingScreen()
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
                val lobbyDomainService = remember { LobbyService() }

                // Launch the connection when this composable enters the composition
                LaunchedEffect(Unit) {
                    lobbyDomainService.connect()
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
                        lobbyService = lobbyDomainService,
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
}
