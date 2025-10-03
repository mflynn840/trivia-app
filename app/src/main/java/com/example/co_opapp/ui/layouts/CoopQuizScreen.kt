package com.example.co_opapp.ui.layouts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.ui.components.QuizScreen.QuestionScreen
import com.example.co_opapp.ui.components.QuizScreen.QuizBackground
import com.example.co_opapp.ui.components.QuizScreen.QuizHotbar

@Composable
public fun CoopQuizScreen(
    currentLobbyService: CurrentLobbyService,
    onNavigateBack: () -> Unit = {},
    onGameComplete: (score: Int, totalQuestions: Int) -> Unit = { _, _ -> },
    modifier: Modifier
){

    //Reactively expose the game state from currentLobbyService
    val gameState by currentLobbyService.gameState
    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    //Reactively expose the current question
    val currentQuestion by currentLobbyService.currentQuestion

    //TODO init code
    LaunchedEffect(Unit) {

    }
    //Draw UI
    QuizBackground(onBack=onNavigateBack, modifier = modifier){
        when{
            currentQuestion != null -> {


                //TODO: Holds timer, and shows which question you are on
                QuizHotbar(
                    currentLobbyService = currentLobbyService,
                    modifier = modifier.fillMaxSize(0.2f)
                )

                QuestionScreen(
                    question = currentQuestion!!,
                    selectedAnswer = selectedAnswer,
                    onAnswerSelected = { selectedAnswer = it },
                    onSubmit = {
                        currentLobbyService.submitAnswer(
                            questionId=currentQuestion!!.id,
                            answer=selectedAnswer!!)
                        selectedAnswer = null
                    },
                )

            }
        }
    }



}