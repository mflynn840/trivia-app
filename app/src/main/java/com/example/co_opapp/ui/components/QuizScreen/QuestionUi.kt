package com.example.co_opapp.ui.components.QuizScreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.ui.components.QuizScreen.AnswerButton
import com.example.co_opapp.ui.components.QuizScreen.QuestionCard

@Composable
fun QuestionScreen(
    question: com.example.co_opapp.data_model.TriviaQuestion,
    questionIndex: Int,
    totalQuestions: Int,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val options = listOf(
        question.optionA,
        question.optionB,
        question.optionC,
        question.optionD
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuestionCard(
            question = question.body,
            questionIndex = questionIndex,
            totalQuestions = totalQuestions
        )

        options.forEach { answer ->
            AnswerButton(
                text = answer,
                isSelected = (answer == selectedAnswer),
                onClick = { onAnswerSelected(answer) }
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        PrimaryButton(
            text = "Submit",
            enabled = selectedAnswer != null,
            onClick = onSubmit
        )
    }
}
