package com.example.co_opapp.ui.components

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun MusicWrapper(
    musicResId: Int,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val mediaPlayer = remember {
        MediaPlayer().apply {
            isLooping = true
            setDataSource(context, android.net.Uri.parse("android.resource://${context.packageName}/$musicResId"))
            setOnPreparedListener { start() } // start when ready
            prepareAsync() // prepare in background
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    content()
}

