package com.example.co_opapp.ui.components

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AvatarPreview(
    imageUri: Uri?,
    neonColor: Color = Color(0xFF00F0FF) // default neon blue
) {
    val context = LocalContext.current

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
        modifier = Modifier
            .size(300.dp)
            .clip(CircleShape)
            .border(6.dp, neonColor.copy(alpha = glowAlpha), CircleShape) // pulsing neon border
            .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                ambientColor = neonColor.copy(alpha = glowAlpha),
                spotColor = neonColor.copy(alpha = glowAlpha)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            val bitmap = remember(imageUri) {
                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                        ImageDecoder.decodeBitmap(source)
                    }
                } catch (e: Exception) {
                    null
                }
            }
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Character Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } ?: androidx.compose.material3.Text("Failed to load image", color = Color.Red)
        } else {
            androidx.compose.material3.Text(
                "Preview",
                color = neonColor,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    shadow = Shadow(
                        color = neonColor.copy(alpha = glowAlpha),
                        offset = Offset(0f, 0f),
                        blurRadius = 24f
                    )
                )
            )
        }
    }
}
