package com.daniel.compose.shadersexperimentsapp.presentation.card

sealed class CardFlipIntent{
    data object Loading : CardFlipIntent()
    data class LoadCard(val cardId : String) : CardFlipIntent()
    data object FlipCard : CardFlipIntent(){
        var cardId = ""
    }
}