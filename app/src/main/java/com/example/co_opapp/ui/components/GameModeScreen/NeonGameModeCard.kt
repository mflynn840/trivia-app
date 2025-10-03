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
import androidx.compose.foundation.clickable
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.shadow
import com.example.co_opapp.SessionManager
@Composable
fun NeonGameModeCard(
    icon: String,
    title: String,
    buttonText: String,
    onClick: () -> Unit,
    cardColor: Color = SessionManager.PRIMARY_CARD_COLOR,
    neonColor: Color = SessionManager.NEON_CARD_COLOR
) {
    // 🔹 Animate alpha for pulsing glow
    val infiniteTransition = rememberInfiniteTransition(label = "neonTransition")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
            .border(
                width = 6.dp,
                color = neonColor.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(16.dp)
            )
            .shadow(
                elevation = 30.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = neonColor.copy(alpha = glowAlpha),
                spotColor = neonColor.copy(alpha = glowAlpha)
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // Icon
            Text(
                text = icon,
                fontSize = 28.sp,
                color = neonColor,
                style = TextStyle(
                    shadow = Shadow(
                        color = neonColor.copy(alpha = glowAlpha),
                        blurRadius = 16f,
                        offset = Offset(0f, 0f)
                    )
                )
            )

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Title with glow
                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = neonColor,
                    style = TextStyle(
                        shadow = Shadow(
                            color = neonColor.copy(alpha = glowAlpha),
                            blurRadius = 20f,
                            offset = Offset(0f, 0f)
                        )
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Button text with glow
                Text(
                    text = buttonText,
                    fontSize = 22.sp,
                    color = neonColor,
                    style = TextStyle(
                        shadow = Shadow(
                            color = neonColor.copy(alpha = glowAlpha),
                            blurRadius = 12f,
                            offset = Offset(0f, 0f)
                        )
                    )
                )
            }
        }
    }
}
