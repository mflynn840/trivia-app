package com.example.co_opapp.ui.components.QuizScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuestionCard(
    question: String,
    questionIndex: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp,
    backgroundColor: Color = Color(0xCCB39DDB) // light purple
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp) // slightly taller to fit progress + question
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Question progress
            Text(
                text = "Question ${questionIndex + 1} of $totalQuestions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Question body
            Text(
                text = question,
                fontSize = fontSize,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f, fill = true)
            )
        }
    }
}

