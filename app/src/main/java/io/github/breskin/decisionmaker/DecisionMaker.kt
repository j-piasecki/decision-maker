package io.github.breskin.decisionmaker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun DecisionMaker() {
    val recognizer = rememberSpeechRecognizer()
    val predictionState = rememberPredictionState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Prediction(state = predictionState)

        RecordButton(
            Modifier
                .width(192.dp)
                .height(192.dp),
            animationProgress = recognizer.animationProgress,
            isActive = recognizer.isActive
        ) {
            if (predictionState.isFinished) {
                predictionState.reset()

                recognizer.startListening(onUpdate = {
                    predictionState.query = it
                }, onFinish = {
                    predictionState.onQueryFinished(coroutineScope)
                })
            }
        }
    }
}