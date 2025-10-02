package com.example.co_opapp.ui.components.QuizScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.data_model.TriviaQuestion
import com.example.co_opapp.ui.components.QuizScreen.AnswerButton
import com.example.co_opapp.ui.components.QuizScreen.QuestionCard

@Composable
fun QuestionScreen(
    question: TriviaQuestion,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val options = listOf(question.optionA, question.optionB, question.optionC, question.optionD)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        QuestionCard(
            question = question.body
        )

        options.forEach { answer ->
            AnswerButton(
                text = answer,
                isSelected = answer == selectedAnswer,
                onClick = { onAnswerSelected(answer) }
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = onSubmit,
            enabled = selectedAnswer != null,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            )
        ) {
            Text("Submit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}


