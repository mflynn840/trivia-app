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
import com.example.co_opapp.ui.layouts.GameModeScreen
import com.example.co_opapp.ui.layouts.QuizScreen
import com.example.co_opapp.ui.layouts.LobbySelectorScreen
import com.example.co_opapp.Service.Backend.SoloGameService
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.Service.Backend.AvailableLobbiesService
import com.example.co_opapp.data_model.GameStatus
import com.example.co_opapp.ui.components.MusicWrapper
import com.example.co_opapp.ui.layouts.CharacterCustomizationScreen
import com.example.co_opapp.ui.layouts.ChatScreen
import com.example.co_opapp.ui.layouts.CoopQuizScreen
import com.example.co_opapp.ui.layouts.QuizSetupScreen
import com.example.co_opapp.ui.layouts.LoadingScreen
import com.example.co_opapp.ui.layouts.LoginScreen

@Composable
fun TriviaGame() {
    val context = LocalContext.current
    val authService = remember { AuthService(context) }
    var soloService by remember { mutableStateOf<SoloGameService?>(null) }
    var playerService by remember { mutableStateOf<ProfileService?>(null) }
    val navController = rememberNavController()
    var profilePicture by remember {mutableStateOf<Bitmap?>(null)}
    val currentLobbyService = remember { CurrentLobbyService() }

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

            //Coop ready up / chat lobby
            composable("joinLobby/{lobbyName}") { backStackEntry ->

                val lobbyName = backStackEntry.arguments?.getString("lobbyName")!!
                val gameStatus = currentLobbyService.gameStatus.value

                //connect to the server when this is launched
                LaunchedEffect(Unit){
                    currentLobbyService.subscribeAndJoin(lobbyName=lobbyName, player=SessionManager.currentPlayer!!)
                }

                //switch to the game loop if the game status changes to IN_PROGRESS
                LaunchedEffect(gameStatus) {
                    if (gameStatus == GameStatus.IN_PROGRESS) {
                        navController.navigate("coopGame/${lobbyName}") {
                            popUpTo("coopGame/${lobbyName}") { inclusive = true }
                        }
                    }
                }

                val lobby by currentLobbyService.lobby
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (lobby==null){
                        //show a loading screen until the server loads
                        LoadingScreen()
                    }else{
                        ChatScreen(
                            currentLobbyService=currentLobbyService,
                            modifier=Modifier.padding(innerPadding),
                            onNavigateBack = {
                                navController.navigate("gameMode") {
                                    popUpTo("gameMode") { inclusive = true }
                                }
                            }
                        )
                    }

                }
            }
            composable("coopGame/{lobbyName}") {backStackEntry ->
                val lobby by currentLobbyService.lobby
                val gameStatus = currentLobbyService.gameStatus.value
                val lobbyName = backStackEntry.arguments?.getString("lobbyName")!!

                LaunchedEffect(gameStatus){
                    navController.navigate("coopGame/${lobbyName}") {
                        popUpTo("coopGame/${lobbyName}") { inclusive = true }
                    }
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(lobby == null){
                        LoadingScreen()
                    }else{
                        CoopQuizScreen(
                            currentLobbyService = currentLobbyService,
                            modifier = Modifier.padding(innerPadding),
                            onNavigateBack = {
                                navController.navigate("lobbySelector") {
                                    popUpTo("lobbySelector")
                                }
                            }
                        )
                    }
                }
            }

            //Game over screen
            composable("gameOver/{lobbyName}") {backStackEntry ->
                val lobby by currentLobbyService.lobby

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(lobby == null){
                        LoadingScreen()
                    }else{
                        GameOverScreen(
                            currentLobbyService = currentLobbyService,
                            modifier = Modifier.padding(innerPadding),
                            onNavigateBack = {
                                navController.navigate("lobbySelector") {
                                    popUpTo("lobbySelector")
                                }
                            }
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

