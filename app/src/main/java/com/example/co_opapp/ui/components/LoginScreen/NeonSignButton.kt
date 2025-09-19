package com.example.co_opapp.ui.components.LoginScreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
    fun NeonSignButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        neonColor: Color = Color(0xFF00F9FF) // Neon cyan by default
    ) {
        // Pulsing glow animation
        val infiniteTransition = rememberInfiniteTransition()
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .height(56.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = neonColor.copy(alpha = glowAlpha * 0.6f),
                    spotColor = neonColor.copy(alpha = glowAlpha * 0.6f)
                )
                .border(3.dp, neonColor.copy(alpha = glowAlpha), RoundedCornerShape(16.dp))
                .background(Color(0xFF111111), RoundedCornerShape(16.dp)) // dark base
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                style = TextStyle(
                    color = neonColor.copy(alpha = glowAlpha),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.SansSerif,
                    shadow = Shadow(
                        color = neonColor.copy(alpha = glowAlpha),
                        offset = Offset(0f, 0f),
                        blurRadius = 24f
                    )
                )
            )
    }
}