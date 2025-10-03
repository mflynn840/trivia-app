package com.example.co_opapp.ui.layouts

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.co_opapp.R
import com.example.co_opapp.Service.Backend.ProfileService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.ui.components.*
import com.example.co_opapp.ui.components.CharacterCustomizationScreen.SendToBackendButton
import com.example.co_opapp.ui.components.CharacterCustomizationScreen.UploadImageButton
@Composable
fun CharacterCustomizationScreen(
    profilePictureService: ProfileService,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    var showSettings by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Box(modifier = modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.character_forest),
            contentDescription = "Character Customization Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top-left: Back + Settings
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            IconButton(onClick = { showSettings = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }

        // Settings popup
        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("Customize Colors", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text("Background Color", fontWeight = FontWeight.SemiBold)
                        GradientColorPicker(
                            gradientColors = listOf(
                                Color.White, Color.Black, Color.DarkGray,
                                Color(0xFF009688), Color(0xFFFFA500),
                                Color(0xFF800080), Color(0xFF008080),
                                Color(0xFFFFC0CB), Color(0xFFB0E0E6),
                                Color(0xFFDC143C)
                            ),
                            selectedColor = SessionManager.QUESTION_PRIMARY_COLOR,
                            onColorSelected = { SessionManager.QUESTION_PRIMARY_COLOR = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        )

                        Text("Neon Effect", fontWeight = FontWeight.SemiBold)
                        GradientColorPicker(
                            gradientColors = listOf(
                                Color.Cyan, Color.Magenta, Color.Yellow,
                                Color.Green, Color.Red, Color.Blue,
                                Color(0xFFFFA500), Color(0xFF800080)
                            ),
                            selectedColor = SessionManager.NEON_CARD_COLOR,
                            onColorSelected = { SessionManager.NEON_CARD_COLOR = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettings = false }) {
                        Text("Done")
                    }
                }
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GreetingBox(
                username = SessionManager.currentPlayer?.username!!,
                neonColor = SessionManager.NEON_CARD_COLOR
            )

            Spacer(modifier = Modifier.height(24.dp))

            AvatarPreview(
                imageUri = imageUri,
                neonColor = SessionManager.NEON_CARD_COLOR
            )

            Spacer(modifier = Modifier.height(32.dp))

            UploadImageButton(
                onClick = { launcher.launch("image/*") },
                neonColor = SessionManager.NEON_CARD_COLOR
            )

            Spacer(modifier = Modifier.height(16.dp))

            SendToBackendButton(
                imageUri = imageUri,
                profilePictureService = profilePictureService,
                neonColor = SessionManager.NEON_CARD_COLOR
            )
        }
    }
}
