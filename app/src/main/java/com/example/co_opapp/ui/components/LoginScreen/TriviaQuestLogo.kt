package com.example.co_opapp.ui.components.LoginScreen

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedTriviaQuestLogo(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }

    // Bounce-in scale
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "scaleAnim"
    )

    // Kick off once
    LaunchedEffect(Unit) { visible = true }

    // Pulsing color animation
    val infiniteTransition = rememberInfiniteTransition(label = "colorAnim")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF1E90FF),
        targetValue = Color(0xFFB22222),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorShift"
    )

    // Logo text
    Text(
        text = "TriviaQuest",
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.sp,
        color = animatedColor,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    )
}
