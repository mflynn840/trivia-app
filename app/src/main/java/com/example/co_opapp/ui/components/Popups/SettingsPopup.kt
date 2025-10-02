package com.example.co_opapp.ui.components.Popups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip

@Composable
fun SettingsPopup(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    soundEffectsMuted: Boolean,
    onSoundEffectsToggle: (Boolean) -> Unit,
    musicMuted: Boolean,
    onMusicToggle: (Boolean) -> Unit,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,

) {
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = { Text("Settings") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Toggle sound effects
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mute Sound Effects")
                    Switch(
                        checked = soundEffectsMuted,
                        onCheckedChange = { onSoundEffectsToggle(it) }
                    )
                }

                // Toggle music
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mute Music")
                    Switch(
                        checked = musicMuted,
                        onCheckedChange = { onMusicToggle(it) }
                    )
                }

                // Color palette selector
                Text("Color Palette")
                ColorPaletteGrid(
                    colors = listOf(
                        Color(0xFFEF9A9A), // soft red
                        Color(0xFF90CAF9), // soft blue
                        Color(0xFFA5D6A7), // soft green
                        Color(0xFFFFF59D), // soft yellow
                        Color(0xFFF48FB1), // pink
                        Color(0xFF80DEEA), // cyan
                        Color(0xFFB0BEC5), // gray
                        Color(0xFF000000), // black
                        Color(0xFFFFFFFF), // white
                        Color(0xFFFFA500), // orange
                        Color(0xFF800080), // purple
                        Color(0xFF008080), // teal
                        Color(0xFF808000)  // olive
                    ),
                    selected = selectedColor,
                    onColorSelected = onColorSelected
                )
            }
        }
    )
}

@Composable
fun ColorPaletteGrid(
    colors: List<Color>,
    selected: Color,
    onColorSelected: (Color) -> Unit
) {
    Column {
        colors.chunked(5).forEach { row ->
            val arrangement = if (row.size < 5) Arrangement.Center else Arrangement.spacedBy(12.dp)
            Row(
                horizontalArrangement = arrangement,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { color ->
                    // build modifier in the right order: size -> border(if selected) -> clickable -> background -> clip
                    var m = Modifier.size(40.dp)
                    if (color == selected) {
                        m = m.border(width = 3.dp, color = Color.Black, shape = CircleShape)
                    }
                    m = m
                        .clickable { onColorSelected(color) }
                        .background(color = color, shape = CircleShape)
                        .clip(CircleShape)

                    Box(modifier = m)
                }
            }
        }
    }
}
