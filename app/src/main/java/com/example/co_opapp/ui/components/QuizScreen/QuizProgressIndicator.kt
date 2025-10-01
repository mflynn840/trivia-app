package com.example.co_opapp.ui.components.QuizScreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


@Composable
fun QuizProgressIndicator(
    questionIndex: Int,
    questionCount: Int,
    modifier: Modifier
){
    Text(
        text = "Question ${questionIndex + 1} of $questionCount",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}
