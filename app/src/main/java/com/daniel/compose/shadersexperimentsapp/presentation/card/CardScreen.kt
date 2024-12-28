package com.daniel.compose.shadersexperimentsapp.presentation.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniel.compose.shadersexperimentsapp.presentation.ui.components.GestureAndSensorRotationCard
import com.daniel.compose.shadersexperimentsapp.presentation.ui.theme.ShadersExperimentsAppTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CardScreen(private val viewModel: CardFlipViewModel) {

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun showCard() {
        val state by viewModel.cardFlipState.collectAsState()
        viewModel.onIntentReceived(CardFlipIntent.Loading)
        ShadersExperimentsAppTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LaunchedEffect("starttup") {
                    GlobalScope.launch {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(2))
                        viewModel.onIntentReceived(CardFlipIntent.LoadCard(cardId = "1"))
                    }
                }
                MySensorCard(state)
            }
        }
    }


    @Composable
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun MySensorCard(state: CardFlipState) {
        GestureAndSensorRotationCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            state = state
        )
    }
}