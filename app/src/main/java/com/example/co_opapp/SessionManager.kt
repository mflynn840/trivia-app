package com.example.co_opapp

import com.example.co_opapp.data_model.Player

object SessionManager {
    var currentPlayer: Player? = null
    var jwtToken: String? = null
}
