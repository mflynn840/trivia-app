package com.example.co_opapp.ui.components.GameModeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*



@Composable
fun NeonGameModeCard(
    icon: String,
    title: String,
    description: String,
    buttonText: String,
    neonColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(3.dp, neonColor.copy(alpha = glowAlpha), RoundedCornerShape(16.dp))
            .background(Color(0xFF111111).copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Text(
                text = icon,
                fontSize = 32.sp,
                color = neonColor.copy(alpha = glowAlpha),
                shadow = Shadow(
                    color = neonColor.copy(alpha = glowAlpha),
                    offset = Offset(0f, 0f),
                    blurRadius = 16f
                )
            )


            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = neonColor.copy(alpha = glowAlpha),
                shadow = Shadow(
                    color = neonColor.copy(alpha = glowAlpha),
                    offset = Offset(0f, 0f),
                    blurRadius = 16f
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = description,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = neonColor.copy(alpha = glowAlpha * 0.8f),
                shadow = Shadow(
                    color = neonColor.copy(alpha = glowAlpha * 0.8f),
                    offset = Offset(0f, 0f),
                    blurRadius = 12f
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = neonColor.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = neonColor.copy(alpha = glowAlpha),
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = neonColor.copy(alpha = glowAlpha),
                        offset = Offset(0f, 0f),
                        blurRadius = 16f
                    ),

                )
            }
        }
    }
}
