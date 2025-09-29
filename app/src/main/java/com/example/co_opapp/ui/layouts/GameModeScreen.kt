package com.example.co_opapp.ui.layouts

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.ui.components.CharacterImageCircle
import com.example.co_opapp.ui.components.GameModeScreen.NeonGameModeCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.co_opapp.R
import com.example.co_opapp.ui.components.LoginScreen.NeonSignButton


@Composable
fun GameModeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSinglePlayer: () -> Unit = {},
    onNavigateToCoOp: () -> Unit = {},
    onNavigateToCharacterMode: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    profilePicture: Bitmap?
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forest_lobby),
            contentDescription = "Lobby Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Back button (top-left)
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        CharacterImageCircle(
            modifier = Modifier.align(Alignment.TopEnd),
            profilePicture = profilePicture
        )

        Spacer(modifier = Modifier.height(100.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .padding(start = 38.dp, end = 38.dp, bottom = 0.dp)
                .align(Alignment.Center)
        ) {

            NeonGameModeCard(
                icon = "\uD83C\uDFAF",
                title = "Test Your Knowledge",
                buttonText = "Story Mode",
                neonColor = Color(0xFF00F0FF),
                onClick = onNavigateToSinglePlayer
            )

            NeonGameModeCard(
                icon = "⚔\uFE0F",
                title = "Play With Friends",
                buttonText = "Co-op Mode",
                neonColor = Color(0xFF00F0FF),
                onClick = onNavigateToCoOp
            )

            NeonSignButton(

                text = "Character Customization",
                onClick = onNavigateToCharacterMode,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp),
            )

        }
    }
}
