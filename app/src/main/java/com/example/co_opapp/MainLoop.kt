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
    //Create the services for running a solo or co-op game
    val soloService = remember { SoloGameService(authService) }
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
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                GameModeScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToSinglePlayer = {
                        navController.navigate("singlePlayerQuiz")
                    },
                    onNavigateToCoOp = {
                        navController.navigate("lobby")
                    },

                    // <-- New callback for Character Customization
                    onNavigateToCharacterMode = { navController.navigate("characterCustomization") },
                    onNavigateBack = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }

                        }
                    },
                    profilePictureService = profilePictureService!!
                )
            }
        }

        composable("quizSetup") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                QuizSetupScreen(
                    modifier = Modifier.padding(innerPadding),
                    onStartQuiz = { category, difficulty, numQuestions ->
                        // Pass the selected options to the single player quiz
                        navController.navigate("singlePlayerQuiz/$category/$difficulty/$numQuestions")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }


        //Single player quiz game is a game skeleton supplied with the soloGameService
        composable(
            route = "singlePlayerQuiz/{category}/{difficulty}/{numQuestions}"
        ) {backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "General"
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "Easy"
            val numQuestions = backStackEntry.arguments?.getString("numQuestions")?.toIntOrNull() ?: 5

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                QuizScreen(
                    modifier = Modifier.padding(innerPadding),
                    quizService = soloService, // inject the service
                    category = category,
                    difficulty = difficulty,
                    numQuestions = numQuestions,

                    onNavigateBack = {
                        navController.navigate("gameMode") {
                            popUpTo("gameMode") { inclusive = true }
                        }
                    },
                    onGameComplete = { score, total ->
                        // Handle completion for single player
                    }
                )
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

        composable(
            "characterCustomization"
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "Player"
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                CharacterCustomizationScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateBack = { navController.popBackStack() },
                    profilePictureService = profilePictureService!!
                )
            }
        }

    }
    }