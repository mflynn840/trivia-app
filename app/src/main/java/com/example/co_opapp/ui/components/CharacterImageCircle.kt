package com.example.co_opapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CharacterImageCircle(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier

            .size(100.dp) // small circle for upper-right corner
            .border(2.dp, Color.Gray, CircleShape)
            .background(Color.LightGray.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("?", color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
    }
}
