package com.daniel.compose.shadersexperimentsapp.domain.model

data class Card(
    val id: String,
    val frontText: String,
    val backText: String,
    val username : String,
    val userImage : String,
    val isFlipped: Boolean = false
)