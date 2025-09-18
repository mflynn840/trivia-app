package com.example.co_opapp.ui.components.CharacterCustomizationScreen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.co_opapp.Service.Backend.ProfileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UploadImageButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
    ) {
        Text("Upload Image")
    }
}

@Composable
fun BackButton(onNavigateBack: () -> Unit) {
    Button(
        onClick = onNavigateBack,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
    ) {
        Text("Back")
    }
}

@Composable
fun SendToBackendButton(imageUri: Uri?, profilePictureService: ProfileService) {
    val context = LocalContext.current

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
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
    ) {
        Text("Confirm")
    }
}
