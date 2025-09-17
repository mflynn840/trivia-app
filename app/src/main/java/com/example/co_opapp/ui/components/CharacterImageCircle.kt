package com.example.co_opapp.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.Service.ProfilePictureService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CharacterImageCircle(
    profilePictureService: ProfilePictureService,
    modifier: Modifier = Modifier
) {
    var avatarBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load avatar asynchronously
    LaunchedEffect(profilePictureService) {
        try {
            val bytes: ByteArray? = withContext(Dispatchers.IO) {
                val b = profilePictureService.getProfilePictureBytes()
                Log.d("CharacterImageCircle", "Fetched bytes: ${b?.size}")
                b
            }
            bytes?.let {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                avatarBitmap = bmp
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = modifier
            .size(100.dp)
            .border(2.dp, Color.Gray, CircleShape)
            .background(Color.LightGray.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (avatarBitmap != null) {
            Image(
                bitmap = avatarBitmap!!.asImageBitmap(),
                contentDescription = "User avatar",
                modifier = Modifier.size(100.dp)
            )
        } else {
            Text("?", color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
