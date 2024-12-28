package com.daniel.compose.shadersexperimentsapp.domain.usecase

import com.daniel.compose.shadersexperimentsapp.domain.model.Card

class LoadCardUseCase() {
    operator fun invoke(cardId: String): Card {
        val card = Card(id = cardId, frontText = "CardFlip", backText = "CardFlip Back!", username = "Daniel", userImage = "https://avatars.githubusercontent.com/u/8259531?v=4", isFlipped = false)
        return card.copy(isFlipped = !card.isFlipped)
    }
}

class GetFlippedCardUseCase() {
    operator fun invoke(cardId: String): Card {
        return Card(id = cardId, frontText = "CardFlip", backText = "Card Flipped !", username = "Daniel", userImage = "https://avatars.githubusercontent.com/u/8259531?v=4", isFlipped = false)
    }
}