package com.example.co_opapp.ui.components.LobbyScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.ui.components.LoginScreen.Secondary_NeonSignButton

@Composable
fun NeonRefreshButton(onClick: () -> Unit) {
    Secondary_NeonSignButton(
        text = "Refresh",
        onClick = onClick,
        neonColor = Color(0xFF00F0FF),
        modifier = Modifier
            .fillMaxWidth(0.6f) // narrower
            .height(56.dp)      // shorter
    )
}

