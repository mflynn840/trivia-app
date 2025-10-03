package com.example.co_opapp.ui.components.Popups

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight


@Composable
fun SettingsPopup(
    primaryCardColor: Color,
    neonColor: Color,
    onPrimaryCardColorChange: (Color) -> Unit,
    onNeonColorChange: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize Colors", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // Card Background Picker
                Text("Card Background", fontWeight = FontWeight.SemiBold)
                val cardGradientColors = listOf(
                    Color.White, Color.Black, Color.DarkGray, Color(0xFF423737),
                    Color(0xFF009688), Color(0xFFFFA500), Color(0xFF800080),
                    Color(0xFF008080), Color(0xFFFFC0CB), Color(0xFFB0E0E6),
                    Color(0xFFFFE4B5), Color(0xFFDC143C)
                )
                GradientColorPicker(
                    gradientColors = cardGradientColors,
                    selectedColor = primaryCardColor,
                    onColorSelected = onPrimaryCardColorChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )

                // Neon Glow Picker
                Text("Neon Glow", fontWeight = FontWeight.SemiBold)
                val neonGradientColors = listOf(
                    Color(0xFF00FFFF), Color(0xFFFF00FF), Color(0xFFFFFF00),
                    Color(0xFF00FF00), Color(0xFFFF4500), Color(0xFF1E90FF),
                    Color(0xFFFF1493), Color(0xFF7CFC00), Color(0xFF8A2BE2),
                    Color(0xFFFF6347), Color(0xFF00CED1), Color(0xFFFFD700)
                )
                GradientColorPicker(
                    gradientColors = neonGradientColors,
                    selectedColor = neonColor,
                    onColorSelected = onNeonColorChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
