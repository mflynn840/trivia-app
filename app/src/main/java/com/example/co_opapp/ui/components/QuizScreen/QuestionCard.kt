package com.example.co_opapp.ui.components.QuizScreen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.SessionManager


@Composable
fun QuestionCard(
    question: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp,
){

    val scrollState = rememberScrollState()
    Card(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = SessionManager.QUESTION_PRIMARY_COLOR)
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ){
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
                    color = SessionManager.QUESTION_TEXT_COLOR,
                    textAlign = TextAlign.Center
                )
            }
        }

    }


}


