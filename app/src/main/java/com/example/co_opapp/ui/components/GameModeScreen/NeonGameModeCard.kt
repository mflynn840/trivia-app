package com.example.co_opapp.ui.components.GameModeScreen

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.CardDefaults




@Composable
fun NeonGameModeCard(
    icon: String,
    title: String,

    buttonText: String,
    neonColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(8.dp, neonColor.copy(alpha = glowAlpha), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D0D0D)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            // Icon
            Text(
                text = icon,
                fontSize = 36.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                style = TextStyle(
                    shadow = Shadow(
                        color = neonColor.copy(alpha = 0.8f),
                        offset = Offset(0f, 0f),
                        blurRadius = 48f
                    )
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = neonColor.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = buttonText,
                    fontSize = 24.sp,
                    color = Color(0xFFFFFFFF),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFF00F0FF),
                            offset = Offset(0f, 0f),
                            blurRadius = 58f
                        )
                    )
                )
            }
        }
    }
}
