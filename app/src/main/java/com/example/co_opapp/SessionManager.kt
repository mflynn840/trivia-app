package com.example.co_opapp

import androidx.compose.ui.graphics.Color
import com.example.co_opapp.data_model.Player

object SessionManager {
    var currentPlayer: Player? = null
    var jwtToken: String? = null
    var PRIMARY_BUTTON_COLOR = Color(0xFFF44336)
    var SECONDARY_BUTTON_COLOR = Color(0xFF00E1FF)


    var PRIMARY_CARD_COLOR = Color(0xFF2196F3)
    var SECONDARY_CARD_COLOR = Color(0xFFF44336)
    var CARD_TEXT_COLOR = Color(0xFF000000)

}
