package io.github.breskin.decisionmaker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DecisionMaker() {
    var showTip by remember {
        mutableStateOf(true)
    }

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
        
        if (showTip) {
            Text(
                text = "Dotknij przycisk i zadaj pytanie",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 28.sp,
                lineHeight = 34.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 32.dp, end = 32.dp)
            )
        }

        RecordButton(
            Modifier
                .width(192.dp)
                .height(192.dp),
            animationProgress = recognizer.animationProgress,
            isActive = recognizer.isActive
        ) {
            showTip = false
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