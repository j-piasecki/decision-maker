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

    private var maxRms = 1f

    private var _animationProgress by mutableStateOf(0f)
    val animationProgress: Float
        get() = _animationProgress

    private var _isActive by mutableStateOf(false)
    val isActive: Boolean
        get() = _isActive

    private var onUpdate: (String) -> Unit = {}
    private var onFinish: (Boolean) -> Unit = {}

    public fun startListening(onUpdate: (String) -> Unit, onFinish: (Boolean) -> Unit) {
        reset()
        this.onUpdate = onUpdate
        this.onFinish = onFinish
        _isActive = true
        recognizer.startListening(intent)
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        onUpdate("")
    }

    override fun onBeginningOfSpeech() {
        onUpdate("")
    }

    override fun onRmsChanged(rms: Float) {
        maxRms = max(maxRms, rms)
        _animationProgress = max(rms / maxRms, 0f)
    }

    override fun onBufferReceived(p0: ByteArray?) {}

    override fun onEndOfSpeech() {
        onFinish(true)
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
        maxRms = 1f
        _animationProgress = 0f
        _isActive = false
    }
}