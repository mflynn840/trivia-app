package com.example.co_opapp.ui.components.QuizScreen

import SubmitAnswerButton
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.TriviaQuestion

@Composable
fun QuestionScreen(
    question: TriviaQuestion,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit,
    cardColor: Color = SessionManager.QUESTION_PRIMARY_COLOR,
    buttonColor: Color = SessionManager.SUBMIT_BUTTON_PRIMARY_COLOR,
    buttonTextColor: Color = SessionManager.SUBMIT_BUTTON_TEXT_COLOR
) {
    val options = listOf(question.optionA, question.optionB, question.optionC, question.optionD)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Spacer(modifier = Modifier.height(95.dp))

        QuestionCard(
            question = question.body,
            cardColor = cardColor,
            textColor = Color.White // optional: or make it dynamic
        )


        options.forEach { answer ->
            AnswerButton(
                text = answer,
                isSelected = answer == selectedAnswer,
                onClick = { onAnswerSelected(answer) },
                buttonColor = buttonColor,
                textColor = buttonTextColor
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        SubmitAnswerButton(
            enabled = selectedAnswer != null,
            onClick = onSubmit,
        )

    }
}


