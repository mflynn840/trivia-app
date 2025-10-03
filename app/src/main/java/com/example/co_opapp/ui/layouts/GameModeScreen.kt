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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.co_opapp.R
import com.example.co_opapp.SessionManager
import com.example.co_opapp.ui.components.LoginScreen.Primary_NeonSignButton
import com.example.co_opapp.ui.components.LoginScreen.Secondary_NeonSignButton

@Composable
fun GameModeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSinglePlayer: () -> Unit = {},
    onNavigateToCoOp: () -> Unit = {},
    onNavigateToCharacterMode: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    profilePicture: Bitmap?
) {
    var showSettings by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forest_lobby),
            contentDescription = "Lobby Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Column for Settings + Back buttons (top-left)
        // Row for Back + Settings buttons (top-left)
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

        // Profile picture (top-right)
        CharacterImageCircle(
            modifier = Modifier.align(Alignment.TopEnd),
            profilePicture = profilePicture
        )

        // Game mode options (centered)
        // Game mode options (centered)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .padding(start = 38.dp, end = 38.dp, top = 50.dp)
                .align(Alignment.Center)
        ) {
            NeonGameModeCard(
                icon = "\uD83C\uDFAF",
                title = "Test Your Knowledge",
                buttonText = "Story Mode",
                onClick = onNavigateToSinglePlayer,
                cardColor = SessionManager.PRIMARY_CARD_COLOR,
                neonColor = SessionManager.NEON_CARD_COLOR
            )

            NeonGameModeCard(
                icon = "⚔\uFE0F",
                title = "Play With Friends",
                buttonText = "Co-op Mode",
                onClick = onNavigateToCoOp,
                cardColor = SessionManager.PRIMARY_CARD_COLOR,
                neonColor = SessionManager.NEON_CARD_COLOR
            )

            Secondary_NeonSignButton(
                text = "Character Customization",
                onClick = onNavigateToCharacterMode,
                backgroundColor = SessionManager.PRIMARY_CARD_COLOR,
                neonColor = SessionManager.NEON_CARD_COLOR,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )


            if (showSettings) {
                AlertDialog(
                    onDismissRequest = { showSettings = false },
                    title = { Text("Customize Colors", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            // 🔹 Card Background Picker
                            Text("Card Background", fontWeight = FontWeight.SemiBold)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val colors = listOf(
                                    Color.White,
                                    Color.Black,
                                    Color.DarkGray,
                                    Color(0xFF423737),
                                    Color(0xFF009688)
                                )
                                colors.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (SessionManager.PRIMARY_CARD_COLOR == color) 3.dp else 1.dp,
                                                color = if (SessionManager.PRIMARY_CARD_COLOR == color) Color.Black else Color.Gray,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                SessionManager.PRIMARY_CARD_COLOR = color
                                            }
                                    )
                                }
                            }

                            // 🔹 Neon Glow Picker
                            Text("Neon Glow", fontWeight = FontWeight.SemiBold)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val neonColors = listOf(
                                    Color.Cyan,
                                    Color.Magenta,
                                    Color.Yellow,
                                    Color.Green,
                                    Color.Red,
                                    Color.Blue
                                )
                                neonColors.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (SessionManager.NEON_CARD_COLOR == color) 3.dp else 1.dp,
                                                color = if (SessionManager.NEON_CARD_COLOR == color) Color.Black else Color.Gray,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                SessionManager.NEON_CARD_COLOR = color
                                            }
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showSettings = false }) {
                            Text("Done")
                        }
                    }
                )
            }
        }
    }
}
