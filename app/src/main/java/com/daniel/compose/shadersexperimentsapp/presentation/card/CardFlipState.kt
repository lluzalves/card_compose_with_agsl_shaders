package com.daniel.compose.shadersexperimentsapp.presentation.card

import com.daniel.compose.shadersexperimentsapp.domain.model.Card

sealed interface CardFlipState
data object CardLoading : CardFlipState{
    val loadingInfo = "Loading"
}
data object CardError : CardFlipState {
    var errorMessage: String = "Error"
}

data object NoCard : CardFlipState
data class CardFlip(
    val isLoading: Boolean = false,
    val card: Card? = null,
    val errorMessage: String? = null
) : CardFlipState