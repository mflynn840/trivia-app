package com.example.co_opapp

import android.graphics.Bitmap
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
import com.example.co_opapp.Service.Backend.ProfileService
import com.example.co_opapp.ui.screens.GameModeScreen
import com.example.co_opapp.ui.screens.QuizScreen
import com.example.co_opapp.ui.screens.LobbySelectorScreen
import com.example.co_opapp.Service.Backend.SoloGameService
import com.example.co_opapp.Service.Backend.WebSocketClientManager
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.Service.Backend.AvailableLobbiesService
import com.example.co_opapp.ui.components.MusicWrapper
import com.example.co_opapp.ui.screens.CharacterCustomizationScreen
import com.example.co_opapp.ui.screens.ChatScreen
import com.example.co_opapp.ui.screens.QuizSetupScreen
import com.example.co_opapp.ui.screens.LoadingScreen
import com.example.co_opapp.ui.screens.LoginScreen

@Composable
fun TriviaGame() {
    val context = LocalContext.current
    val authService = remember { AuthService(context) }
    var soloService by remember { mutableStateOf<SoloGameService?>(null) }
    var playerService by remember { mutableStateOf<ProfileService?>(null) }
    val navController = rememberNavController()
    var profilePicture by remember {mutableStateOf<Bitmap?>(null)}


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
                            playerService = ProfileService(authService, context)
                            navController.navigate("gameMode")

                        }
                    )
                }
            }

            //user selects which game mode they want to play
            composable("gameMode") {
                // Fetch profile picture asynchronously after login
                LaunchedEffect(Unit) {
                    val picture = playerService?.getProfilePicture()  // Fetch profile picture
                    profilePicture = picture  // Store it in state
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameModeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToSinglePlayer = { navController.navigate("soloQuizSetup") },
                        onNavigateToCoOp = { navController.navigate("lobbySelector") },
                        onNavigateToCharacterMode = { navController.navigate("characterCustomization") },
                        onNavigateBack = {
                            navController.navigate("login") {
                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                        },
                        profilePicture = profilePicture
                    )
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
            composable("lobbySelector") {
                val availableLobbiesService=remember{AvailableLobbiesService()}
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LobbySelectorScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateBack = {
                            navController.navigate("gameMode") {
                                popUpTo("gameMode") { inclusive = true }
                            }
                        },
                        onNavigateToLobby = { name ->
                            navController.navigate("joinLobby/$name") {
                                popUpTo("joinLobby") { inclusive = true }
                            }
                        },
                        availableLobbiesService = availableLobbiesService
                    )
                }
            }


            composable("joinLobby/{lobbyName}") { backStackEntry ->
                val currentLobbyConnection = remember {WebSocketClientManager()}
                val currentLobbyService = remember { CurrentLobbyService(currentLobbyConnection) }
                val username = SessionManager.currentPlayer?.username!!
                val lobbyName = backStackEntry.arguments?.getString("lobbyName")!!

                //connect to the server when this is launched
                LaunchedEffect(Unit){
                    currentLobbyConnection.connect()
                    currentLobbyService.subscribeAndJoin(lobbyName=lobbyName, username=username)
                }

                val lobby by currentLobbyService.lobby


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (lobby==null){
                        //show a loading screen until the server loads
                        LoadingScreen()
                    }else{
                        ChatScreen(
                            currentLobbyService=currentLobbyService,
                            modifier=Modifier.padding(innerPadding)
                        )
                    }

                }
            }


            // Character customization
            composable("characterCustomization") {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CharacterCustomizationScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateBack = { navController.popBackStack() },
                        profilePictureService = ProfileService(authService, context)
                    )
                }
            }
        }
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