package com.example.co_opapp.ui.components.GameOverScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun TrophyWithProfile(
    place: Int,
    imageUri: String?,
    trophyRes: Int
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(120.dp) // Limits max size of the trophy
    ) {
        // Trophy image (sized properly to avoid over-scaling)
        Image(
            painter = painterResource(id = trophyRes),
            contentDescription = "Trophy for place $place",
            modifier = Modifier
                .size(120.dp), // Prevents full-canvas scaling
            contentScale = ContentScale.Fit
        )

        // Profile image overlaid in center (circle clipped)
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Player profile",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}
