package io.github.breskin.decisionmaker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun rememberPredictionState(): PredictionState {
    return remember {
        PredictionState()
    }
}

class PredictionState() {
    var query by mutableStateOf("")
}

@Composable
fun Prediction(state: PredictionState) {
    Text(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        text = state.query,
        fontSize = 28.sp,
        textAlign = TextAlign.Center,
        lineHeight = 34.sp
    )
}