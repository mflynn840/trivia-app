package com.example.co_opapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuestionCard(
    question: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp,
    backgroundColor: Color = Color(0xFFE1BEE7) // light purple
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(16.dp)), // black border
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = question,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
