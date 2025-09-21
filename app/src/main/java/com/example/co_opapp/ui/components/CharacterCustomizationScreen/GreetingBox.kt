package com.example.co_opapp.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GreetingBox(username: String, neonColor: Color = Color(0xFF00F0FF)) {
    // Pulsing neon animation
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(3.dp, neonColor.copy(alpha = glowAlpha), RoundedCornerShape(12.dp)) // glowing border
            .background(Color(0xFF111111), shape = RoundedCornerShape(12.dp)) // dark background
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Upload profile picture for $username",
            color = neonColor,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                fontSize = 28.sp,
                shadow = Shadow(
                    color = neonColor.copy(alpha = glowAlpha),
                    offset = Offset(0f, 0f),
                    blurRadius = 24f
                )
            ),
            textAlign = TextAlign.Center
        )
    }
}
