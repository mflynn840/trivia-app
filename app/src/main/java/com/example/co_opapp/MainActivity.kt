package com.example.co_opapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.co_opapp.ui.theme.CoopAppTheme
import com.example.co_opapp.ui.components.TriviaGame


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoopAppTheme {
                TriviaGame() // your composable root
            }
        }
    }
}
