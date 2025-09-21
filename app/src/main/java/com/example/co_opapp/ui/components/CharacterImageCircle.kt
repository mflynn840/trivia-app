package com.example.co_opapp.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Backend.ProfileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CharacterImageCircle(
    profilePicture: Bitmap?,
    modifier: Modifier = Modifier,
    circleSize: Dp = 140.dp,
    topPadding: Dp = 26.dp,
    endPadding: Dp = 26.dp,
    neonColor: Color = Color(0xFF00F0FF)
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

    Box(
        modifier = modifier
            .padding(top = topPadding, end = endPadding)
            .size(circleSize)
            .border(6.dp, neonColor.copy(alpha = glowAlpha), CircleShape) // glow border
            .background(Color.LightGray.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (profilePicture != null) {
            Image(
                bitmap = profilePicture.asImageBitmap(),
                contentDescription = "User avatar",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "?",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

