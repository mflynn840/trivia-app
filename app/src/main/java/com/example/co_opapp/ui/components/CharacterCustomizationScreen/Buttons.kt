package com.example.co_opapp.ui.components.CharacterCustomizationScreen

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.Service.Backend.ProfileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UploadImageButton(
    onClick: () -> Unit,
    neonColor: Color = Color(0xFF00F0FF) // default neon blue
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

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(3.dp, neonColor.copy(alpha = glowAlpha), RoundedCornerShape(12.dp)), // neon border
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF111111) // dark background
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = "Upload Image",
            color = neonColor,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                fontSize = 18.sp,
                shadow = Shadow(
                    color = neonColor.copy(alpha = glowAlpha),
                    offset = Offset(0f, 0f),
                    blurRadius = 20f
                )
            )
        )
    }
}

@Composable
fun SendToBackendButton(
    imageUri: Uri?,
    profilePictureService: ProfileService,
    neonColor: Color = Color(0xFF00F0FF) // default neon blue
) {
    val context = LocalContext.current

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = {
            imageUri?.let { uri ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val success = profilePictureService.uploadProfilePicture(uri)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                if (success) "Avatar uploaded!" else "Upload failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Error uploading image: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } ?: run {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(3.dp, neonColor.copy(alpha = glowAlpha), RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)), // dark background
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = "Confirm",
            color = neonColor,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                fontSize = 18.sp,
                shadow = Shadow(
                    color = neonColor.copy(alpha = glowAlpha),
                    offset = Offset(0f, 0f),
                    blurRadius = 20f
                )
            )
        )
    }
}
