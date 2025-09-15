package com.example.co_opapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.co_opapp.ui.theme.CoopAppTheme
import com.example.co_opapp.ui.screens.GameModeScreen
import com.example.co_opapp.ui.screens.QuizScreen

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
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
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
        
        composable("singlePlayerQuiz") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                QuizScreen(
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
        
        composable("coOpQuiz") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                QuizScreen(
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
        
        composable("game") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                GameScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateBack = {
                        navController.navigate("lobby") {
                            popUpTo("lobby") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CoopAppTheme {
        Greeting("Android")
    }
}