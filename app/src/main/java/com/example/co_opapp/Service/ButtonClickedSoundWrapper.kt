package com.example.co_opapp.Service

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


// Wrapper composable that plays a sound when the button is clicked
@Composable
fun ButtonClickedSoundWrapper(
    soundResId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    // Remember the media player across recompositions
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, soundResId) }

    // Release the media player when leaving this screen or composable
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    // Wrap the button with a clickable action that plays the sound
    Box(modifier = modifier) {
        // The content is the button or any other composable passed
        Button(
            onClick = {
                // Play the sound when clicked
                mediaPlayer.start()

                // Execute the passed onClick action
                onClick()
            }
        ) {
            content() // The content inside the button (e.g., text, icon, etc.)
        }
    }
}

