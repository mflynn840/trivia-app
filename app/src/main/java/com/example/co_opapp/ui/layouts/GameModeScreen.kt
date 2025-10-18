package com.example.co_opapp.ui.layouts

import android.graphics.Bitmap
import android.util.Log
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.co_opapp.R
import com.example.co_opapp.Service.Backend.ProfileService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.ui.components.LoginScreen.Secondary_NeonSignButton
import com.example.co_opapp.ui.components.Popups.SettingsPopup
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope


@Composable
fun GameModeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSinglePlayer: () -> Unit = {},
    onNavigateToCoOp: () -> Unit = {},
    onNavigateToCharacterMode: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    profilePicture: Bitmap?,
    profileService: ProfileService
) {
    var showSettings by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                cardColor = Color(SessionManager.currentPlayer!!.colorPallete.primaryCardColor),
                neonColor = Color(SessionManager.currentPlayer!!.colorPallete.neonCardColor)
            )

            NeonGameModeCard(
                icon = "⚔\uFE0F",
                title = "Play With Friends",
                buttonText = "Co-op Mode",
                onClick = onNavigateToCoOp,
                cardColor = Color(SessionManager.currentPlayer!!.colorPallete.primaryCardColor),
                neonColor = Color(SessionManager.currentPlayer!!.colorPallete.neonCardColor)
            )

            Secondary_NeonSignButton(
                text = "Character Customization",
                onClick = onNavigateToCharacterMode,
                backgroundColor = Color(SessionManager.currentPlayer!!.colorPallete.primaryCardColor),
                neonColor = Color(SessionManager.currentPlayer!!.colorPallete.neonCardColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )

            if (showSettings) {
                SettingsPopup(
                    primaryCardColor = Color(SessionManager.currentPlayer!!.colorPallete.primaryCardColor),
                    neonColor = Color(SessionManager.currentPlayer!!.colorPallete.neonCardColor),
                    onPrimaryCardColorChange = { SessionManager.currentPlayer!!.colorPallete.primaryCardColor = it.value.toInt() },
                    onNeonColorChange = { SessionManager.currentPlayer!!.colorPallete.neonCardColor = it.value.toInt() },
                    onDismiss = {

                        coroutineScope.launch {
                            profileService.uploadColorPallete(SessionManager.currentPlayer!!.colorPallete)
                        }
                        Log.d(
                            "GameModeScreen",
                            "User Color Palette: PRIMARY=${SessionManager.currentPlayer!!.colorPallete.primaryCardColor}, NEON=${SessionManager.currentPlayer!!.colorPallete.neonCardColor}"
                        )

                        showSettings = false
                    }
                )
            }
        }
    }
}