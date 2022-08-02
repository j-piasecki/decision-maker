package io.github.breskin.decisionmaker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DecisionMaker() {
    val recognizer = rememberSpeechRecognizer()
    var query by remember {
        mutableStateOf("")
    }

    Column() {
        Text(text = query)

        RecordButton(
            Modifier
                .width(128.dp)
                .height(128.dp),
        animationProgress = recognizer.animationProgress
        ) {
            recognizer.startListening(onUpdate = {
                query = it
            }, onFinish = {

            })
        }
    }
}