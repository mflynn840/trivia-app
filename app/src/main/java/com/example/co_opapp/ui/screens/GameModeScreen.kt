package com.example.co_opapp.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.co_opapp.ui.components.CharacterImageCircle
import com.example.co_opapp.ui.components.GameModeScreen.GameModeCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.co_opapp.R
import com.example.co_opapp.Service.AuthService
import com.example.co_opapp.Service.ProfileService
import com.example.co_opapp.ui.components.LoginScreen.AnimatedGradientButton

@Composable
fun GameModeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSinglePlayer: () -> Unit = {},
    onNavigateToCoOp: () -> Unit = {},
    onNavigateToCharacterMode: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    profilePictureService: ProfileService
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forest_lobby),
            contentDescription = "Lobby Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        CharacterImageCircle(
            modifier = Modifier.align(Alignment.TopEnd),
            profilePictureService = profilePictureService
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .padding(48.dp)
                .align(Alignment.Center)
        ) {
            Spacer(modifier = Modifier.height(38.dp))

            Text(
                text = "Choose Game Mode",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            GameModeCard(
                icon = "\uD83D\uDC64",
                title = "Story Mode",
                description = "Play alone and test your knowledge",
                buttonText = "Start Single Player",
                buttonColor = Color(0xFF00F9FF),
                onClick = onNavigateToSinglePlayer
            )

            GameModeCard(
                icon = "\uD83D\uDC65",
                title = "Co-op Mode",
                description = "Play with friends on the same network",
                buttonText = "Start Co-op Game",
                buttonColor = Color(0xFFFF073A),
                onClick = onNavigateToCoOp
            )

            AnimatedGradientButton(
                text = "Character Customization",
                onClick = onNavigateToCharacterMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedGradientButton(
                text = "Back to Login",
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            )
        }
    }
}
