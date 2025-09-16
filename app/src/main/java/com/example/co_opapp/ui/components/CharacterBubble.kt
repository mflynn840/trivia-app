package com.example.co_opapp.ui.components

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

@Composable
fun CharacterBubble(
    imageUri: Uri?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
                contentDescription = "Character Bubble",
                modifier = modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
        } ?: Surface(
            modifier = modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {}
    } else {
        Surface(
            modifier = modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {}
    }
}
