package io.github.breskin.decisionmaker

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.floor

@Composable
fun rememberPredictionState(): PredictionState {
    val backgroundColor = MaterialTheme.colorScheme.onBackground
    val tint = MaterialTheme.colorScheme.primary

    return remember {
        PredictionState(backgroundColor, tint)
    }
}

class PredictionState(private val backgroundColor: Color, private val tint: Color) {
    private var _isFinished by mutableStateOf(true)
    private var _isListening by mutableStateOf(true)
    private var _result by mutableStateOf(listOf<String>())
    private var _chosenOption by mutableStateOf(0)

    var query by mutableStateOf("")


    val isFinished: Boolean
        get() = _isFinished

    val isListening: Boolean
        get() = _isListening

    val result: List<String>
        get() = _result

    val chosenOption: Int
        get() = _chosenOption

    val animationProgress = Animatable(0f)
    val chosenColor = Animatable(backgroundColor)

    fun reset() {
        query = ""
        _isListening = true
    }

    fun onQueryFinished(coroutineScope: CoroutineScope) {
        _isFinished = false
        coroutineScope.launch {
            animationProgress.snapTo(0f)
            chosenColor.snapTo(backgroundColor)
        }

        val keywordIndex = query.indexOf(" czy ")

        _result = if (keywordIndex == -1) {
            listOf(query)
        } else {
            val split = query.split(" czy ")
            val wordsRight = split[1].split(" ")
            val wordsLeft = split[0].split(" ")

            val prefixLength =
                if (wordsRight.size > wordsLeft.size) 0 else wordsLeft.size - wordsRight.size

            val prefix = wordsLeft.take(prefixLength).joinToString(" ") + if (prefixLength != 0) " " else ""
            val left = wordsLeft.takeLast(wordsRight.size).joinToString(" ")
            val right = split[1]

            listOf(prefix, left, " czy ", right)
        }

        _chosenOption = floor(Math.random() * 2).toInt()
        _isListening = false

        coroutineScope.launch {
            if (_result.size == 4) {
                delay(1000)
            } else {
                animationProgress.snapTo(1.25f)
                val prevChosen = chosenOption
                _chosenOption = -1
                delay(200)
                animationProgress.animateTo(0f, spring(stiffness = 50f))
                delay(800)
                _chosenOption = prevChosen
            }

            coroutineScope.launch { animationProgress.animateTo(1f, spring(stiffness = 50f)) }
            coroutineScope.launch {
                chosenColor.animateTo(tint, spring(stiffness = 50f))
                _isFinished = true
            }
        }
    }
}

@Composable
fun Prediction(state: PredictionState) {
    val color = MaterialTheme.colorScheme.onBackground
    val fontSize = 32.sp
    val lineHeight = 38.sp

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (state.isListening) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                text = state.query,
                fontSize = fontSize,
                textAlign = TextAlign.Center,
                lineHeight = lineHeight,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            if (state.result.size == 4) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            color = color.copy(alpha = (1.25f - state.animationProgress.value).coerceAtMost(1f))
                        )) {
                            append(state.result[0])
                        }
                        withStyle(style = SpanStyle(
                            color = if (state.chosenOption == 0) state.chosenColor.value else color.copy(alpha = (1.4f - state.animationProgress.value).coerceAtMost(1f))
                        )) {
                            append(state.result[1])
                        }
                        withStyle(style = SpanStyle(
                            color = color.copy(alpha = (1.25f - state.animationProgress.value).coerceAtMost(1f))
                        )) {
                            append(state.result[2])
                        }
                        withStyle(style = SpanStyle(
                            color = if (state.chosenOption == 1) state.chosenColor.value else color.copy(alpha = (1.4f - state.animationProgress.value).coerceAtMost(1f))
                        )) {
                            append(state.result[3])
                        }
                    },
                    fontSize = fontSize,
                    textAlign = TextAlign.Center,
                    lineHeight = lineHeight,
                    color = color
                )
            } else {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text = state.query,
                    fontSize = fontSize,
                    textAlign = TextAlign.Center,
                    lineHeight = lineHeight,
                    color = color
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Tak",
                        fontSize = fontSize,
                        color = if (state.chosenOption == 0) state.chosenColor.value else color.copy(alpha =  (1.25f - state.animationProgress.value).coerceAtMost(1f))
                    )
                    Text(
                        text = "Nie",
                        fontSize = fontSize,
                        color = if (state.chosenOption == 1) state.chosenColor.value else color.copy(alpha =  (1.25f - state.animationProgress.value).coerceAtMost(1f))
                    )
                }
            }
        }
    }
}