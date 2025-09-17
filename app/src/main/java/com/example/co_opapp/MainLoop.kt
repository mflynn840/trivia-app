package com.example.co_opapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.co_opapp.Service.ProfilePictureService
import com.example.co_opapp.ui.theme.CoopAppTheme
import com.example.co_opapp.ui.screens.GameModeScreen
import com.example.co_opapp.ui.screens.LoginScreen
import com.example.co_opapp.ui.screens.QuizScreen
import com.example.co_opapp.ui.screens.LobbyScreen
import com.example.co_opapp.Service.RaceModeGameService
import com.example.co_opapp.Service.SoloGameService
import com.example.co_opapp.ui.screens.CharacterCustomizationScreen
import com.example.co_opapp.ui.screens.QuizSetupScreen
import com.example.co_opapp.ui.screens.LoadingScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoopAppTheme {
                CoopApp()
            }
        }
    }
}

@Composable
fun CoopApp() {

    //the main loop contains a controller for switching between pages
    val navController = rememberNavController()
    val context = LocalContext.current
    val authService = remember { AuthService(context) }

    //Create the services for running a solo or co-op game later
    var soloService by remember { mutableStateOf<SoloGameService?>(null)}
    val raceModeService = remember { RaceModeGameService() }

    // ProfilePictureService will be created **after login**
    var profilePictureService by remember { mutableStateOf<ProfilePictureService?>(null) }

    //start on the login page
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        //when the player completes login, go to the "select game-mode screen"
        composable("login") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToLobby = {
                        profilePictureService = ProfilePictureService(authService, context)
                        navController.navigate("gameMode")
                    },
                    authService = authService
                )
            }
        }

        //when the player selects a game mode, go to that game mode
        composable("gameMode") {
            val service = profilePictureService
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                if (service != null){
                    GameModeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToSinglePlayer = { navController.navigate("soloQuizSetup") },
                        onNavigateToCoOp = { navController.navigate("lobby") },
                        onNavigateToCharacterMode = { navController.navigate("characterCustomization") },
                        onNavigateBack = { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
                        profilePictureService = service
                    )

                }else{
                    LoadingScreen()
                }

            }
        }

        //ask the player which category and difficulty
        composable("soloQuizSetup") {
            val categorySelectorService = CategorySelectorService(context, authService.getJwtToken()!!)
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                QuizSetupScreen(
                    modifier = Modifier.padding(innerPadding),
                    onStartQuiz = { category, difficulty, numQuestions ->
                        // Pass the selected options to the quiz driver and create it
                        soloService = SoloGameService(
                            authService = authService,
                            category = category,
                            difficulty = difficulty,
                        )

                        navController.navigate("singlePlayerQuiz")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },

                    catSelService = categorySelectorService
                )
            }
        }


        //Single player quiz game is a game skeleton supplied with the soloGameService
        composable(route = "singlePlayerQuiz") {
            //make the game driver for this game
            val service = soloService
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                if(service != null){
                    QuizScreen(
                        modifier = Modifier.padding(innerPadding),
                        quizService = soloService!!, // inject the service

                        onNavigateBack = {
                            navController.navigate("gameMode") {
                                popUpTo("gameMode") { inclusive = true }
                            }
                        },
                        onGameComplete = { score, total ->
                            // Handle completion for single player
                        }
                    )
                }else{
                    LoadingScreen()
                }

            }
        }

        composable("lobby") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LobbyScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateBack = {
                        navController.navigate("gameMode") {
                            popUpTo("gameMode") { inclusive = true }
                        }
                    },
                    onNavigateToGame = {
                        navController.navigate("coopQuiz")
                    },
                    gameService = raceModeService,
                    authService = authService
                )
            }
        }

        /*composable("coopQuiz") {
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
        } */

        composable("characterCustomization") {

            val service = profilePictureService
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                if (service != null) {
                    CharacterCustomizationScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateBack = { navController.popBackStack() },
                        profilePictureService = service
                    )
                }else{
                    LoadingScreen()
                }
            }
        }

    }
    }