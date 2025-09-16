package com.example.co_opapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.co_opapp.R
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.Service.ProfilePictureService
import com.example.co_opapp.ui.components.*
import com.example.co_opapp.ui.components.CharacterCustomizationScreen.BackButton
import com.example.co_opapp.ui.components.CharacterCustomizationScreen.SendToBackendButton
import com.example.co_opapp.ui.components.CharacterCustomizationScreen.UploadImageButton

@Composable
fun CharacterCustomizationScreen(
    profilePictureService: ProfilePictureService,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Box(modifier = modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.character_forest),
            contentDescription = "Character Customization Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GreetingBox(username = profilePictureService.authService.getUsername()!!)

            Spacer(modifier = Modifier.height(24.dp))

            AvatarPreview(imageUri = imageUri)

            Spacer(modifier = Modifier.height(32.dp))

            UploadImageButton { launcher.launch("image/*") }

            Spacer(modifier = Modifier.height(16.dp))

            BackButton(onNavigateBack)

            Spacer(modifier = Modifier.height(16.dp))

            SendToBackendButton(
                imageUri = imageUri,
                profilePictureService = profilePictureService,
            )
        }
    }
}
