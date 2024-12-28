package com.daniel.compose.shadersexperimentsapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.daniel.compose.shadersexperimentsapp.domain.usecase.GetFlippedCardUseCase
import com.daniel.compose.shadersexperimentsapp.domain.usecase.LoadCardUseCase
import com.daniel.compose.shadersexperimentsapp.presentation.card.CardFlipViewModel
import com.daniel.compose.shadersexperimentsapp.presentation.card.CardScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val flippedCardUseCase = GetFlippedCardUseCase()
            val loadUseCase = LoadCardUseCase()
            CardScreen(CardFlipViewModel(flippedCardUseCase, loadUseCase)).showCard()
        }
    }
}
