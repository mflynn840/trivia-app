package com.example.co_opapp.ui.components.QuizScreen

import SubmitAnswerButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.SessionManager
import com.example.co_opapp.data_model.TriviaQuestion

@Composable
fun QuestionScreen(
    question: TriviaQuestion,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit,
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
        )

        options.forEach { answer ->
            AnswerButton(
                text = answer,
                isSelected = answer == selectedAnswer,
                onClick = { onAnswerSelected(answer) }
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        SubmitAnswerButton(
            enabled = selectedAnswer != null,
            onClick = onSubmit,
        )

    }
}


