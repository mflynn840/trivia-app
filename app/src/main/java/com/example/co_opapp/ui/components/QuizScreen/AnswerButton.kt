package com.example.co_opapp.ui.components.QuizScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnswerButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Determine colors
    val backgroundColor = if (isSelected) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.6f)
    val textColor = if (isSelected) Color.White else Color.Black

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.85f)              // <-- narrower width to match QuestionCard
            .height(55.dp)
            .padding(vertical = 4.dp)
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                color = textColor
            )
        }
    }
}
