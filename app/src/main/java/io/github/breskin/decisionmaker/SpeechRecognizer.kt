package io.github.breskin.decisionmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlin.math.max
import kotlin.math.min


@Composable
fun rememberSpeechRecognizer(): SpeechRecognizerState {
    val context = LocalContext.current
    val recognizer = remember {
        SpeechRecognizerState(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            recognizer.recognizer.destroy()
        }
    }

    return recognizer
}

class RmsCache(size: Int) {
    private val data = FloatArray(size)
    private var nextItem = 0
    var size = 0
        private set

    val value: Float
        get() {
            var sum = 0f
            var max = 0f
            for (i in 0 until size) {
                sum += data[i]
                if (data[i] > max) {
                    max = data[i]
                }
            }

            return if (size == 0 || max == 0f) 0f else sum / size / max
        }

    fun add(value: Float) {
        data[nextItem] = value
        size = min(size + 1, data.size)
        nextItem = (nextItem + 1) % data.size
    }

    fun clear() {
        size = 0
        nextItem = 0
    }
}

class SpeechRecognizerState(private val context: Context): RecognitionListener {
    val recognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).also {
        it.setRecognitionListener(this)
    }

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also {
        it.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        it.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pl")
        it.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        it.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    private val rmsCache = RmsCache(16)

    private var _animationProgress by mutableStateOf(0f)
    val animationProgress: Float
        get() = _animationProgress

    private var _isActive by mutableStateOf(false)
    val isActive: Boolean
        get() = _isActive

    private var onUpdate: (String) -> Unit = {}
    private var onFinish: (Boolean) -> Unit = {}

    fun startListening(onUpdate: (String) -> Unit, onFinish: (Boolean) -> Unit) {
        reset()
        this.onUpdate = onUpdate
        this.onFinish = onFinish
        _isActive = true
        recognizer.startListening(intent)

        // great solution for starting animation :)
        rmsCache.add(1f)
        rmsCache.add(1f)
        rmsCache.add(1f)
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        onUpdate("")
    }

    override fun onBeginningOfSpeech() {
        onUpdate("")
    }

    override fun onRmsChanged(rms: Float) {
        rmsCache.add(rms)
        _animationProgress = max(rmsCache.value, 0f)
    }

    override fun onBufferReceived(p0: ByteArray?) {}

    override fun onEndOfSpeech() {
        _isActive = false
    }

    override fun onError(p0: Int) {
        onFinish(false)
        _isActive = false
        reset()
    }

    override fun onResults(bundle: Bundle?) {
        val data = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: return
        if (data.isNotEmpty()) {
            onUpdate(data)
            onFinish(true)
        }
    }

    override fun onPartialResults(bundle: Bundle?) {
        val data = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: return
        if (data.isNotEmpty()) {
            onUpdate(data)
        }
    }

    override fun onEvent(p0: Int, p1: Bundle?) {}

    private fun reset() {
        this.onUpdate = {}
        this.onFinish = {}
        rmsCache.clear()
        _animationProgress = 0f
        _isActive = false
    }
}