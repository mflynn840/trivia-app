package com.example.co_opapp.data_model

import androidx.compose.ui.graphics.Color

data class Player(
    val id: Long,
    val username: String,
    val score: Long = 0,
    var ready: Boolean = false,
    var colorPallete: ColorPallete,
    //var sessionId: String,
    //var profilePicture: ByteArray,
)

data class PlayerDTO(
    //val sessionId: String,
    val username: String,
    val isReady: Boolean = false,
    val id: Long,
    val score: Long,
    val colorPallete: ColorPallete
    //val profilePicture: ByteArray
)

data class ColorPallete(
    val PRIMARY_BUTTON_COLOR: Long,
    val SECONDARY_BUTTON_COLOR: Long,
    var PRIMARY_CARD_COLOR: Long,
    var NEON_CARD_COLOR: Long,
    val CARD_TEXT_COLOR: Long,
    val QUESTION_PRIMARY_COLOR: Long,
    val QUESTION_TEXT_COLOR: Long,
    val SUBMIT_BUTTON_PRIMARY_COLOR: Long,
    val SUBMIT_BUTTON_TEXT_COLOR: Long

)