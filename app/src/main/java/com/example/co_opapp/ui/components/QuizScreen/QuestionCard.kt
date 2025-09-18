package com.example.co_opapp.ui.components.QuizScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    backgroundColor: Color = Color.White.copy(alpha = 0.6f)
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Question ${questionIndex + 1} of $totalQuestions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth() // <-- important
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable question text (~2 lines visible)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // ~ lines of text visible
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = question,
                    fontFamily = FontFamily.Serif,
                    fontSize = fontSize,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
