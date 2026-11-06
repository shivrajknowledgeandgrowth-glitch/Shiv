package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiUiStatus
import com.example.data.AppLanguage
import com.example.data.AppTab
import com.example.data.ChatMessage
import com.example.data.DayFitnessRecord
import com.example.data.MessageSender
import com.example.data.UserProfile
import com.example.voice.SpeechRecognizerHelper
import com.example.voice.TextToSpeechHelper
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

private const val TAG = "ShivAiViewModel"

class ShivAiViewModel(application: Application) : AndroidViewModel(application) {

    val speechHelper = SpeechRecognizerHelper(application)
    val ttsHelper = TextToSpeechHelper(application)

    // Lazy initialization of Firebase AI Logic model as mandated by firebase-ai-logic-android
    private val generativeModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _profile = MutableStateFlow(
        UserProfile(
            name = "Shivraj",
            username = "shiv_user",
            dailyGoal = 6000,
            language = AppLanguage.MARATHI,
            isAiEnabled = true,
            isAutoTtsEnabled = true
        )
    )
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _aiStatus = MutableStateFlow<AiUiStatus>(AiUiStatus.Idle)
    val aiStatus: StateFlow<AiUiStatus> = _aiStatus.asStateFlow()

    private val _statusMessage = MutableStateFlow("माईकवर टॅप करा आणि बोला...")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.SHIVAI,
                text = "नमस्कार! मी तुमचा ShivAI असिस्टंट आहे. AI चालू ठेवा आणि प्रश्न विचारा."
            )
        )
    )
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    // Fitness state
    private val _todaySteps = MutableStateFlow(4250)
    val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

    private val _todayCalories = MutableStateFlow(170)
    val todayCalories: StateFlow<Int> = _todayCalories.asStateFlow()

    private val _monthHistory = MutableStateFlow<List<DayFitnessRecord>>(emptyList())
    val monthHistory: StateFlow<List<DayFitnessRecord>> = _monthHistory.asStateFlow()

    init {
        generate30DaysHistory()
    }

    private fun generate30DaysHistory() {
        val list = mutableListOf<DayFitnessRecord>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        for (i in 1..30) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val dateStr = dateFormat.format(calendar.time)
            // Generate realistic steps between 3,500 and 7,500
            val steps = 3500 + Random.nextInt(4000)
            val calories = (steps * 0.04).toInt()
            val distanceKm = (steps * 0.00075f)
            list.add(
                DayFitnessRecord(
                    dayNumber = i,
                    label = "दिवस $i ($dateStr)",
                    steps = steps,
                    calories = calories,
                    distanceKm = (distanceKm * 10).toInt() / 10f
                )
            )
        }
        _monthHistory.value = list
    }

    fun login(usernameInput: String, passwordInput: String): Boolean {
        val u = usernameInput.trim()
        val p = passwordInput.trim()
        if (u.isEmpty() || p.isEmpty()) {
            return false
        }
        _profile.value = _profile.value.copy(
            username = u,
            name = if (_profile.value.name.isBlank()) u else _profile.value.name
        )
        _isLoggedIn.value = true
        return true
    }

    fun logout() {
        ttsHelper.stop()
        speechHelper.stopListening()
        _isLoggedIn.value = false
        _currentTab.value = AppTab.HOME
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleAi() {
        val newStatus = !_profile.value.isAiEnabled
        _profile.value = _profile.value.copy(isAiEnabled = newStatus)
        if (!newStatus) {
            ttsHelper.stop()
            speechHelper.stopListening()
            _statusMessage.value = "AI बंद आहे"
            _aiStatus.value = AiUiStatus.Idle
            val disabledNotice = ChatMessage(
                sender = MessageSender.SHIVAI,
                text = "AI बंद केले आहे. पुन्हा सुरू करण्यासाठी वरचे स्विच ऑन करा."
            )
            _chatHistory.value = _chatHistory.value + disabledNotice
        } else {
            _statusMessage.value = "माईकवर टॅप करा आणि बोला..."
        }
    }

    fun updateProfileName(newName: String) {
        if (newName.isNotBlank()) {
            _profile.value = _profile.value.copy(name = newName.trim())
        }
    }

    fun updateDailyGoal(goal: Int) {
        if (goal > 0) {
            _profile.value = _profile.value.copy(dailyGoal = goal)
        }
    }

    fun setLanguage(language: AppLanguage) {
        _profile.value = _profile.value.copy(language = language)
        val welcomeMsg = when (language) {
            AppLanguage.MARATHI -> "भाषा मराठी निवडली आहे. मी तुम्हाला कशी मदत करू?"
            AppLanguage.HINDI -> "भाषा हिंदी चुनी गई है। मैं आपकी क्या मदद कर सकता हूँ?"
            AppLanguage.ENGLISH -> "Language changed to English. How can I assist you?"
        }
        _chatHistory.value = _chatHistory.value + ChatMessage(sender = MessageSender.SHIVAI, text = welcomeMsg)
        if (_profile.value.isAutoTtsEnabled) {
            ttsHelper.speak(welcomeMsg, language)
        }
    }

    fun toggleAutoTts() {
        _profile.value = _profile.value.copy(isAutoTtsEnabled = !_profile.value.isAutoTtsEnabled)
        if (!_profile.value.isAutoTtsEnabled) {
            ttsHelper.stop()
        }
    }

    fun addSteps(amount: Int) {
        val newSteps = _todaySteps.value + amount
        _todaySteps.value = newSteps
        _todayCalories.value = (newSteps * 0.04).toInt()
    }

    fun startVoiceInput(onErrorCallback: (String) -> Unit) {
        if (!_profile.value.isAiEnabled) {
            onErrorCallback("कृपया आधी वरील AI स्विच चालू करा!")
            return
        }

        _statusMessage.value = "ऐकत आहे..."
        _aiStatus.value = AiUiStatus.Listening

        speechHelper.startListening(
            language = _profile.value.language,
            onResult = { recognizedText ->
                _statusMessage.value = "विचार करत आहे..."
                sendMessage(recognizedText)
            },
            onError = { error ->
                _statusMessage.value = "माईकवर टॅप करा आणि बोला..."
                _aiStatus.value = AiUiStatus.Error(error)
                onErrorCallback(error)
            }
        )
    }

    fun stopVoiceInput() {
        speechHelper.stopListening()
        if (_aiStatus.value is AiUiStatus.Listening) {
            _aiStatus.value = AiUiStatus.Idle
            _statusMessage.value = "माईकवर टॅप करा आणि बोला..."
        }
    }

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        if (!_profile.value.isAiEnabled) {
            _statusMessage.value = "AI बंद आहे"
            return
        }

        // Add user message to chat history
        _chatHistory.value = _chatHistory.value + ChatMessage(sender = MessageSender.USER, text = trimmed)

        viewModelScope.launch {
            _statusMessage.value = "विचार करत आहे..."
            _aiStatus.value = AiUiStatus.Thinking

            val lang = _profile.value.language.code
            val prompt = """
                तू 'ShivAI' नावाचा पर्सनल असिस्टंट आणि फिटनेस मार्गदर्शक आहेस.
                युझरने निवडलेली भाषा: $lang
                जर भाषा mr-IN असेल तर मराठीत, hi-IN असेल तर हिंदीत आणि en-US असेल तर इंग्लिशमध्ये उत्तर दे.
                युझरशी अत्यंत आदराने, उत्साहाने आणि मित्रासारखे बोल.
                उत्तर २ ते ३ वाक्यांत संक्षिप्त, प्रभावी आणि स्पष्ट दे, जेणेकरून ते आवाजात ऐकणे सोपे होईल.
                युझरची आजची पावले: ${_todaySteps.value} (दैनिक उद्दिष्ट: ${_profile.value.dailyGoal}).
                युझरचे नाव: ${_profile.value.name}.

                युझर म्हणाला: $trimmed
            """.trimIndent()

            val accumulatedText = StringBuilder()

            try {
                // Mandated: realistic 30-60s timeout window for model inference
                withTimeout(60_000L) {
                    generativeModel.generateContentStream(prompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulatedText.append(chunkText)
                        _aiStatus.value = AiUiStatus.Streaming(accumulatedText.toString())
                    }
                }

                val finalReply = accumulatedText.toString().trim()
                if (finalReply.isNotEmpty()) {
                    _aiStatus.value = AiUiStatus.Success(finalReply)
                    _chatHistory.value = _chatHistory.value + ChatMessage(sender = MessageSender.SHIVAI, text = finalReply)
                    _statusMessage.value = "माईकवर टॅप करा आणि बोला..."

                    // Auto speak if TTS is enabled
                    if (_profile.value.isAutoTtsEnabled) {
                        ttsHelper.speak(finalReply, _profile.value.language)
                    }
                } else {
                    _aiStatus.value = AiUiStatus.Error("Model returned an empty response.")
                    _statusMessage.value = "उत्तर मिळाले नाही. पुन्हा प्रयत्न करा."
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Generation timed out", e)
                val timeoutMsg = "प्रक्रियेची वेळ संपली. कृपया पुन्हा प्रयत्न करा."
                _aiStatus.value = AiUiStatus.Error(timeoutMsg)
                _statusMessage.value = timeoutMsg
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI generation failed", e)
                val errMsg = e.localizedMessage ?: "AI संपर्क साधताना त्रुटी आली."
                _aiStatus.value = AiUiStatus.Error(errMsg)
                _statusMessage.value = "त्रुटी: $errMsg"
                _chatHistory.value = _chatHistory.value + ChatMessage(
                    sender = MessageSender.SHIVAI,
                    text = "क्षमस्व, काही तांत्रिक त्रुटी आली: $errMsg"
                )
            }
        }
    }

    fun speakText(text: String) {
        ttsHelper.speak(text, _profile.value.language)
    }

    fun stopSpeaking() {
        ttsHelper.stop()
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.stopListening()
        ttsHelper.shutdown()
    }
}
