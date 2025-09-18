package com.example.co_opapp.Service

import android.content.Context
import android.media.MediaPlayer

object MediaPlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    fun initializeMediaPlayer(context: Context, musicResId: Int) {
        // Only create a new instance if it doesn't already exist
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, musicResId).apply {
                isLooping = true
                start()
            }
        }
    }

    fun stopAndRelease() {
        mediaPlayer?.let {
            it.stop()
            it.release()
            mediaPlayer = null
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
