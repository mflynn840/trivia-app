package com.example.co_opapp.data_model

data class Player(
    val id: Long,
    val username: String,
    val score: Long = 0,
    var ready: Boolean = false,
    //var sessionId: String,
    //var profilePicture: ByteArray,
)

data class PlayerDTO(
    //val sessionId: String,
    val username: String,
    val isReady: Boolean = false,
    val id: Long,
    val score: Long,
    //val profilePicture: ByteArray
)
