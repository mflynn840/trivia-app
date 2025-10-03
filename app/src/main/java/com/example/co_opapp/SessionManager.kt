package com.example.co_opapp

import androidx.compose.ui.graphics.Color
import com.example.co_opapp.data_model.Player

object SessionManager {
    var currentPlayer: Player? = null
    var jwtToken: String? = null

    // Buttons
    var PRIMARY_BUTTON_COLOR = Color.Black
    var SECONDARY_BUTTON_COLOR = Color(0xFFFFFFFF)

    // Cards
    var PRIMARY_CARD_COLOR = Color(0xFFFFFFFF)   // background
    var NEON_CARD_COLOR = Color(0xFF00FFFF)      // neon glow

    var CARD_TEXT_COLOR = Color(0xFFFFFFFF)

    // Questions
    var QUESTION_PRIMARY_COLOR = Color.White.copy(alpha = 0.6f)
    var QUESTION_TEXT_COLOR= Color.Black
    var SUBMIT_BUTTON_PRIMARY_COLOR = Color(0xFF009688)
    var SUBMIT_BUTTON_TEXT_COLOR = Color(0xFFFFFFFF)
    var ANSWER_SELECTED_PRIMARY_COLOR = Color(0xFF009688)
    var ANSWER_SELECTED_TEXT_COLOR= Color(0xFF009688)
    var ANSWER_UNSELECTED_PRIMARY_COLOR = Color.White.copy(alpha = 0.6f)
    var ANSWER_UNSELECTED_TEXT_COLOR = Color.Black
}

