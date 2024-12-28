package com.daniel.compose.shadersexperimentsapp.presentation.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.compose.shadersexperimentsapp.domain.usecase.GetFlippedCardUseCase
import com.daniel.compose.shadersexperimentsapp.domain.usecase.LoadCardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardFlipViewModel(
    private val flipCardUseCase: GetFlippedCardUseCase,
    private val loadCardUseCase: LoadCardUseCase
) :
    ViewModel() {

    private val _cardFlipState = MutableStateFlow<CardFlipState>(CardLoading)
    val cardFlipState: StateFlow<CardFlipState> get() = _cardFlipState

    fun onIntentReceived(intent: CardFlipIntent) {
        when (intent) {
            is CardFlipIntent.FlipCard -> {
                flipCardSide(intent.cardId)
            }

            is CardFlipIntent.LoadCard -> {
                loadCard(intent.cardId)
            }
            is CardFlipIntent.Loading ->{
                _cardFlipState.value = CardLoading
            }
        }
    }

    private fun flipCardSide(cardId: String) {
        viewModelScope.launch {
            _cardFlipState.value = CardLoading
            try {
                val card = flipCardUseCase.invoke(cardId)
                _cardFlipState.value = CardFlip(
                    isLoading = false,
                    card = card,
                    errorMessage = null
                )
            }catch (exception : Exception){
                CardError.errorMessage = exception.localizedMessage ?: "Error"
                _cardFlipState.value = CardError
            }
        }
    }

    private fun loadCard(cardId: String) {
        viewModelScope.launch {
            _cardFlipState.value = CardLoading
            try {
                val card = loadCardUseCase.invoke(cardId)
                _cardFlipState.value = CardFlip(
                    isLoading = false,
                    card = card,
                    errorMessage = null
                )
            }catch (exception : Exception){
                CardError.errorMessage = exception.localizedMessage ?: "Error"
                _cardFlipState.value = CardError
            }
        }
    }
}