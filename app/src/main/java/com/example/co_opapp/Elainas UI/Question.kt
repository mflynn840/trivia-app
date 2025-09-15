package com.example.trivia_game

data class Question(
    val text: String,
    val answers: List<String>,
    val correctAnswer: String
)
