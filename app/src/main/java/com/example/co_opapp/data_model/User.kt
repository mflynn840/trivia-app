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
    var primaryButtonColor: Int = 0xFF00F0FF.toInt(),
    var secondaryButtonColor: Int = 0xFF000000.toInt(),
    var primaryCardColor: Int = 0xFFFFFFFF.toInt(),
    var neonCardColor: Int = 0xFF00FFFF.toInt(),
    var cardTextColor: Int = 0xFFFFFFFF.toInt(),
    var questionPrimaryColor: Int = 0x99FFFFFF.toInt(),
    var questionTextColor: Int = 0xFF000000.toInt(),
    var submitButtonPrimaryColor: Int = 0xFF009688.toInt(),
    var submitButtonTextColor: Int = 0xFFFFFFFF.toInt()
)