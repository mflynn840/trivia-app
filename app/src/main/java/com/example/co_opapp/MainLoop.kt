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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.co_opapp.ui.theme.CoopAppTheme
import com.example.co_opapp.ui.screens.GameModeScreen
import com.example.co_opapp.ui.screens.LobbyScreen
import com.example.co_opapp.ui.screens.LoginScreen

import com.example.co_opapp.ui.screens.CoopGameScreen
import com.example.co_opapp.ui.screens.SoloGameScreen




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
                        navController.navigate("gameMode")
                    }
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
                    onNavigateBack = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
        }

        //Single player quiz game
        composable("singlePlayerQuiz") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SoloGameScreen(
                    modifier = Modifier.padding(innerPadding),
                    isSinglePlayer = true,
                    onNavigateBack = {
                        navController.navigate("gameMode") {
                            popUpTo("gameMode") { inclusive = true }
                        }
                    },
                    onGameComplete = { score, total ->
                        // Handle game completion for single player
                        // Could navigate to a results screen or back to menu
                    }
                )
            }
        }

        //Join a coop game (lobby screen
        composable("lobby") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LobbyScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToGame = {
                        navController.navigate("coOpQuiz")
                    },
                    onNavigateBack = {
                        navController.navigate("gameMode") {
                            popUpTo("gameMode") { inclusive = true }
                        }
                    }
                )
            }
        }

        //Play a coop quiz game
        composable("coOpQuiz") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                CoopGameScreen(
                    modifier = Modifier.padding(innerPadding),
                    isSinglePlayer = false,
                    onNavigateBack = {
                        navController.navigate("lobby") {
                            popUpTo("lobby") { inclusive = true }
                        }
                    },
                    onGameComplete = { score, total ->
                        // Handle game completion for co-op
                    }
                )
            }
        }
    }
}
