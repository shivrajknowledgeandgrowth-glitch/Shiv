package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechRecognizerHelper(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening(
        language: AppLanguage,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        stopListening()

        if (!isAvailable()) {
            onError("Speech recognition is not available on this device.")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "आवाज समजला नाही. कृपया पुन्हा बोला."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "वेळ संपली. कृपया पुन्हा बोला."
                            SpeechRecognizer.ERROR_AUDIO -> "ऑडिओ रेकॉर्डिंग समस्या."
                            SpeechRecognizer.ERROR_NETWORK -> "नेटवर्क त्रुटी."
                            else -> "आवाज ऐकताना समस्या आली ($error)."
                        }
                        onError(errorMsg)
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            onResult(matches[0])
                        } else {
                            onError("कोणताही शब्द ओळखता आला नाही.")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.code)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language.code)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language.code)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isListening.value = false
            Log.e("SpeechRecognizerHelper", "Failed to start listening", e)
            onError(e.localizedMessage ?: "माईक सुरू करता आला नाही.")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SpeechRecognizerHelper", "Error stopping recognizer", e)
        } finally {
            speechRecognizer = null
            _isListening.value = false
        }
    }
}

class TextToSpeechHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                    }
                })
            } else {
                Log.e("TextToSpeechHelper", "TTS Initialization failed: $status")
            }
        }
    }

    fun speak(text: String, language: AppLanguage) {
        if (!isInitialized || tts == null) {
            Log.w("TextToSpeechHelper", "TTS not initialized yet")
            return
        }

        // Clean out markdown symbols like * # _ from Gemini responses for speech
        val cleanText = text.replace(Regex("[*#_`>]"), "").trim()
        if (cleanText.isEmpty()) return

        // Configure language locale
        val langResult = tts?.setLanguage(language.locale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback: try language without country or English
            val fallbackResult = tts?.setLanguage(Locale(language.locale.language))
            if (fallbackResult == TextToSpeech.LANG_MISSING_DATA || fallbackResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
        }

        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "ShivAiResponseUtterance")
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TextToSpeechHelper", "Error stopping TTS", e)
        } finally {
            _isSpeaking.value = false
        }
    }

    fun shutdown() {
        try {
            stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("TextToSpeechHelper", "Error shutting down TTS", e)
        } finally {
            tts = null
            isInitialized = false
        }
    }
}
