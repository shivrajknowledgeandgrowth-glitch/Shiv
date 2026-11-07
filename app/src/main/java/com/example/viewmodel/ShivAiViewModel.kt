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
import com.example.data.local.ShivAiDatabase
import com.example.data.local.StepData
import com.example.data.local.VoiceInteraction
import com.example.data.repository.ShivAiRepository
import com.example.service.StepTrackingService
import com.example.util.PhoneControlManager
import com.example.util.SettingsShortcut
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
import java.util.Date
import java.util.Locale
import com.example.sensor.SensorStatusInfo

private const val TAG = "ShivAiViewModel"

class ShivAiViewModel(application: Application) : AndroidViewModel(application) {

    val repository = ShivAiRepository(ShivAiDatabase.getDatabase(application))
    val phoneControlManager = PhoneControlManager(application)
    val isTorchOn: StateFlow<Boolean> = phoneControlManager.isTorchOn
    val isServiceRunning: StateFlow<Boolean> = StepTrackingService.isServiceRunning

    val sensorStatus: StateFlow<SensorStatusInfo?> = StepTrackingService.sensorStatus
    val speechHelper = SpeechRecognizerHelper(application)
    val ttsHelper = TextToSpeechHelper(application)

    // Lazy initialization of Firebase AI Logic model as mandated by firebase-ai-logic-android
    private val generativeModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val _isLoggedIn = MutableStateFlow(true)
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
                text = "नमस्कार! मी तुमचा ShivAI असिस्टंट आहे. २४ तास अखंड फिटनेस ट्रॅकिंग आणि मोबाईल कंट्रोल सज्ज आहे."
            )
        )
    )
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    // Real Live Fitness state synced continuously with 24/7 StepTrackingService
    private val _todaySteps = MutableStateFlow(0)
    val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

    private val _todayCalories = MutableStateFlow(0)
    val todayCalories: StateFlow<Int> = _todayCalories.asStateFlow()

    private val _monthHistory = MutableStateFlow<List<DayFitnessRecord>>(emptyList())
    val monthHistory: StateFlow<List<DayFitnessRecord>> = _monthHistory.asStateFlow()

    init {
        // Start 24/7 background foreground service immediately
        StepTrackingService.start(application)

        generate30DaysHistory()
        loadPersistedData()
        syncWithBackgroundTrackingService()
    }

    private fun syncWithBackgroundTrackingService() {
        viewModelScope.launch {
            StepTrackingService.liveSteps.collect { steps ->
                _todaySteps.value = steps
            }
        }
        viewModelScope.launch {
            StepTrackingService.liveCalories.collect { calories ->
                _todayCalories.value = calories
            }
        }
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun loadPersistedData() {
        viewModelScope.launch {
            val dateStr = getTodayDateString()
            val savedToday = repository.getStepDataByDateOnce(dateStr)
            if (savedToday != null) {
                _todaySteps.value = savedToday.steps
                _todayCalories.value = savedToday.calories
                StepTrackingService.updateLiveStepsManually(savedToday.steps)
            }

            // Restore conversation history from Room Database if present
            repository.allInteractions.collect { interactions ->
                if (interactions.isNotEmpty()) {
                    _chatHistory.value = interactions.map { interaction ->
                        ChatMessage(
                            id = interaction.id.toString(),
                            sender = if (interaction.sender == "USER") MessageSender.USER else MessageSender.SHIVAI,
                            text = interaction.text,
                            timestamp = interaction.timestamp
                        )
                    }
                }
            }
        }
    }

    private fun persistTodaySteps(steps: Int, calories: Int) {
        viewModelScope.launch {
            val dateStr = getTodayDateString()
            val existing = repository.getStepDataByDateOnce(dateStr)
            val distanceKm = (steps * 0.00075f)
            val stepData = StepData(
                id = existing?.id ?: 0,
                dateString = dateStr,
                steps = steps,
                calories = calories,
                distanceKm = (distanceKm * 10).toInt() / 10f,
                dailyGoal = _profile.value.dailyGoal,
                timestamp = System.currentTimeMillis()
            )
            repository.saveStepData(stepData)
        }
    }

    private fun generate30DaysHistory() {
        viewModelScope.launch {
            repository.allStepData.collect { recordedList ->
                val list = mutableListOf<DayFitnessRecord>()
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                val keyDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val recordedMap = recordedList.associateBy { it.dateString }

                for (i in 1..30) {
                    val calCopy = calendar.clone() as Calendar
                    calCopy.add(Calendar.DAY_OF_YEAR, -i)
                    val dateKey = keyDateFormat.format(calCopy.time)
                    val displayDate = dateFormat.format(calCopy.time)

                    val saved = recordedMap[dateKey]
                    val steps = saved?.steps ?: 0
                    val calories = saved?.calories ?: (steps * 0.04).toInt()
                    val distanceKm = saved?.distanceKm ?: ((steps * 0.00076f * 10).toInt() / 10f)

                    list.add(
                        DayFitnessRecord(
                            dayNumber = i,
                            label = "$displayDate",
                            steps = steps,
                            calories = calories,
                            distanceKm = distanceKm
                        )
                    )
                }
                _monthHistory.value = list
            }
        }
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
        val newCalories = (newSteps * 0.04).toInt()
        _todaySteps.value = newSteps
        _todayCalories.value = newCalories
        StepTrackingService.updateLiveStepsManually(newSteps)
        persistTodaySteps(newSteps, newCalories)
    }

    fun resetTodaySteps() {
        _todaySteps.value = 0
        _todayCalories.value = 0
        StepTrackingService.updateLiveStepsManually(0)
        persistTodaySteps(0, 0)
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

    // --- SMART MOBILE CONTROLS & LOCAL COMMANDS ---
    private fun handleLocalCommand(query: String, lang: AppLanguage): String? {
        val q = query.lowercase().trim()
        val steps = _todaySteps.value
        val goal = _profile.value.dailyGoal
        val cal = _todayCalories.value
        val percent = ((steps.toFloat() / goal.coerceAtLeast(1)) * 100).toInt()

        // --- 1. TORCH / FLASHLIGHT MOBILE CONTROL ---
        if (q.contains("टॉर्च चालू") || q.contains("torch on") || q.contains("फ्लॅश चालू") || q.contains("बत्ती लावा") || q.contains("लाइट चालू") || q.contains("लाईट चालू")) {
            phoneControlManager.toggleTorch(turnOn = true)
            return when (lang) {
                AppLanguage.MARATHI -> "होय, मी मोबाईलचा टॉर्च चालू केला आहे! 🔦"
                AppLanguage.HINDI -> "हाँ, मैंने मोबाइल की टॉर्च चालू कर दी है! 🔦"
                AppLanguage.ENGLISH -> "I have turned ON the flashlight! 🔦"
            }
        }
        if (q.contains("टॉर्च बंद") || q.contains("torch off") || q.contains("फ्लॅश बंद") || q.contains("बत्ती बंद") || q.contains("लाइट बंद") || q.contains("लाईट बंद")) {
            phoneControlManager.toggleTorch(turnOn = false)
            return when (lang) {
                AppLanguage.MARATHI -> "मी मोबाईलचा टॉर्च बंद केला आहे."
                AppLanguage.HINDI -> "मैंने मोबाइल की टॉर्च बंद कर दी है।"
                AppLanguage.ENGLISH -> "I have turned OFF the flashlight."
            }
        }
        if (q.contains("टॉर्च") || q.contains("torch") || q.contains("flashlight")) {
            val newState = phoneControlManager.toggleTorch()
            val isNowOn = phoneControlManager.isTorchOn.value
            return when (lang) {
                AppLanguage.MARATHI -> if (isNowOn) "टॉर्च चालू केला 🔦" else "टॉर्च बंद केला."
                AppLanguage.HINDI -> if (isNowOn) "टॉर्च चालू किया गया 🔦" else "टॉर्च बंद किया गया।"
                AppLanguage.ENGLISH -> if (isNowOn) "Flashlight turned ON 🔦" else "Flashlight turned OFF."
            }
        }

        // --- 2. VOLUME CONTROLS ---
        if (q.contains("आवाज वाढवा") || q.contains("volume up") || q.contains("sound up") || q.contains("आवाज मोठा")) {
            val res = phoneControlManager.raiseVolume()
            return when (lang) {
                AppLanguage.MARATHI -> "मी मोबाईलचा $res"
                AppLanguage.HINDI -> "मोबाइल का $res"
                AppLanguage.ENGLISH -> "Raised volume: $res"
            }
        }
        if (q.contains("आवाज कमी") || q.contains("volume down") || q.contains("sound down") || q.contains("आवाज लहान")) {
            val res = phoneControlManager.lowerVolume()
            return when (lang) {
                AppLanguage.MARATHI -> "मी मोबाईलचा $res"
                AppLanguage.HINDI -> "मोबाइल का $res"
                AppLanguage.ENGLISH -> "Lowered volume: $res"
            }
        }
        if (q.contains("म्युट") || q.contains("mute") || q.contains("शांत करा") || q.contains("सायलेंट")) {
            val res = phoneControlManager.muteVolume()
            return when (lang) {
                AppLanguage.MARATHI -> "मी मोबाईलचा $res"
                AppLanguage.HINDI -> "मोबाइल का $res"
                AppLanguage.ENGLISH -> "Muted audio: $res"
            }
        }
        if (q.contains("फुल आवाज") || q.contains("max volume") || q.contains("पूर्ण आवाज")) {
            val res = phoneControlManager.maxVolume()
            return when (lang) {
                AppLanguage.MARATHI -> "मी मोबाईलचा $res"
                AppLanguage.HINDI -> "मोबाइल का $res"
                AppLanguage.ENGLISH -> "Max volume set: $res"
            }
        }

        // --- 3. CALLS & DIALER ---
        if (q.contains("कॉल") || q.contains("call") || q.contains("फोन लावा") || q.contains("डायलर") || q.contains("dialer")) {
            val digits = Regex("""\d{10,}""").find(q)?.value
            phoneControlManager.openDialer(digits)
            return when (lang) {
                AppLanguage.MARATHI -> if (digits != null) "मी $digits क्रमांकासाठी फोन डायलर उघडला आहे 📞" else "मी मोबाईलचा फोन डायलर उघडला आहे 📞"
                AppLanguage.HINDI -> if (digits != null) "मैंने $digits के लिए फोन डायलर खोल दिया है 📞" else "मैंने फोन डायलर खोल दिया है 📞"
                AppLanguage.ENGLISH -> if (digits != null) "Opening dialer for $digits 📞" else "Opening phone dialer 📞"
            }
        }

        // --- 4. WHATSAPP ---
        if (q.contains("व्हॉट्सअ‍ॅप") || q.contains("whatsapp") || q.contains("व्हॉट्सॲप") || q.contains("व्हॉटसप")) {
            phoneControlManager.openWhatsApp()
            return when (lang) {
                AppLanguage.MARATHI -> "मी मोबाईलमध्ये WhatsApp उघडले आहे 💬"
                AppLanguage.HINDI -> "मैंने WhatsApp खोल दिया है 💬"
                AppLanguage.ENGLISH -> "Opening WhatsApp 💬"
            }
        }

        // --- 5. SMS / MESSAGING ---
        if (q.contains("मेसेज") || q.contains("sms") || q.contains("संदेश") || q.contains("message")) {
            phoneControlManager.openSms()
            return when (lang) {
                AppLanguage.MARATHI -> "मी मेसेजिंग (SMS) उघडले आहे ✉️"
                AppLanguage.HINDI -> "मैंने मैसेजिंग ऐप खोल दिया है ✉️"
                AppLanguage.ENGLISH -> "Opening Messaging app ✉️"
            }
        }

        // --- 6. ALARM & TIMER ---
        if (q.contains("अलार्म") || q.contains("alarm")) {
            phoneControlManager.openAlarmClock()
            return when (lang) {
                AppLanguage.MARATHI -> "मी मोबाईलचे अलार्म घड्याळ उघडले आहे ⏰"
                AppLanguage.HINDI -> "मैंने अलार्म क्लॉक खोल दिया है ⏰"
                AppLanguage.ENGLISH -> "Opening Alarm Clock ⏰"
            }
        }
        if (q.contains("टाइमर") || q.contains("timer")) {
            val seconds = Regex("""\d+""").find(q)?.value?.toIntOrNull()?.let { it * 60 } ?: 300
            phoneControlManager.setTimer(seconds)
            return when (lang) {
                AppLanguage.MARATHI -> "मी ${seconds / 60} मिनिटांचा टाइमर सेट केला आहे ⏳"
                AppLanguage.HINDI -> "मैंने ${seconds / 60} मिनट का टाइमर सेट कर दिया है ⏳"
                AppLanguage.ENGLISH -> "Set timer for ${seconds / 60} minutes ⏳"
            }
        }

        // --- 7. CAMERA ---
        if (q.contains("कॅमेरा") || q.contains("camera") || q.contains("फोटो काढा") || q.contains("photo")) {
            phoneControlManager.openCamera()
            return when (lang) {
                AppLanguage.MARATHI -> "मी मोबाईलचा कॅमेरा उघडला आहे 📷"
                AppLanguage.HINDI -> "मैंने मोबाइल का कैमरा खोल दिया है 📷"
                AppLanguage.ENGLISH -> "Opening Camera 📷"
            }
        }

        // --- 8. SYSTEM SETTINGS ---
        if (q.contains("वायफाय") || q.contains("wifi") || q.contains("वाय-फाय")) {
            phoneControlManager.openSettings(SettingsShortcut.WIFI)
            return when (lang) {
                AppLanguage.MARATHI -> "मी वाय-फाय (Wi-Fi) सेटिंग्ज उघडल्या आहेत 📶"
                AppLanguage.HINDI -> "मैंने वाई-फाई सेटिंग्स खोल दी हैं 📶"
                AppLanguage.ENGLISH -> "Opening Wi-Fi settings 📶"
            }
        }
        if (q.contains("ब्लूटूथ") || q.contains("bluetooth")) {
            phoneControlManager.openSettings(SettingsShortcut.BLUETOOTH)
            return when (lang) {
                AppLanguage.MARATHI -> "मी ब्लूटूथ सेटिंग्ज उघडल्या आहेत ᛒ"
                AppLanguage.HINDI -> "मैंने ब्लूटूथ सेटिंग्स खोल दी हैं ᛒ"
                AppLanguage.ENGLISH -> "Opening Bluetooth settings ᛒ"
            }
        }
        if (q.contains("सेटिंग्ज") || q.contains("settings") || q.contains("सेटिंग")) {
            phoneControlManager.openSettings(SettingsShortcut.MAIN)
            return when (lang) {
                AppLanguage.MARATHI -> "मी मोबाईलच्या मुख्य सेटिंग्ज उघडल्या आहेत ⚙️"
                AppLanguage.HINDI -> "मैंने मोबाइल सेटिंग्स खोल दी हैं ⚙️"
                AppLanguage.ENGLISH -> "Opening Phone Settings ⚙️"
            }
        }

        // --- 9. MAPS & NAVIGATION ---
        if (q.contains("नकाशा") || q.contains("maps") || q.contains("मॅप") || q.contains("रस्ता") || q.contains("दिशा")) {
            phoneControlManager.openMaps()
            return when (lang) {
                AppLanguage.MARATHI -> "मी गुगल मॅप्स (नकाशा) उघडले आहे 🗺️"
                AppLanguage.HINDI -> "मैंने गूगल मैप्स खोल दिया है 🗺️"
                AppLanguage.ENGLISH -> "Opening Google Maps 🗺️"
            }
        }

        // --- 10. YOUTUBE / SONGS ---
        if (q.contains("युट्यूब") || q.contains("youtube") || q.contains("गाणे") || q.contains("गाणी") || q.contains("song")) {
            phoneControlManager.openYouTube()
            return when (lang) {
                AppLanguage.MARATHI -> "मी YouTube उघडले आहे 📺"
                AppLanguage.HINDI -> "मैंने YouTube खोल दिया है 📺"
                AppLanguage.ENGLISH -> "Opening YouTube 📺"
            }
        }

        // --- 11. FITNESS & STEPS COMMANDS ---
        val containsAdd = q.contains("जोडा") || q.contains("वाढवा") || q.contains("add") || q.contains("plus") || q.contains("+")
        if (containsAdd && (q.contains("पावले") || q.contains("steps") || q.contains("कदम"))) {
            val digits = Regex("""\d+""").find(q)?.value?.toIntOrNull() ?: 500
            addSteps(digits)
            return when (lang) {
                AppLanguage.MARATHI -> "मी $digits पावले जोडली आहेत! आता तुमची एकूण पावले ${_todaySteps.value} झाली आहेत."
                AppLanguage.HINDI -> "मैंने $digits कदम जोड़ दिए हैं! अब आपके कुल कदम ${_todaySteps.value} हो गए हैं।"
                AppLanguage.ENGLISH -> "Added $digits steps! Your total steps are now ${_todaySteps.value}."
            }
        }

        if (q.contains("पावले") || q.contains("step") || q.contains("चाललो") || q.contains("कदम") || q.contains("walk")) {
            return when (lang) {
                AppLanguage.MARATHI -> "२४ तास ट्रॅकरनुसार आज तुम्ही $steps पावले चालला आहात. तुमचे दैनिक उद्दिष्ट $goal पावले आहे ($percent% पूर्ण झाले)."
                AppLanguage.HINDI -> "24 घंटे ट्रैकर के अनुसार आज आप $steps कदम चले हैं। आपका दैनिक लक्ष्य $goal कदम है ($percent% पूरा हुआ)।"
                AppLanguage.ENGLISH -> "According to 24h tracker, you have walked $steps steps today. Your daily goal is $goal steps ($percent% completed)."
            }
        }

        if (q.contains("कॅलरी") || q.contains("calorie") || q.contains("ऊर्जा") || q.contains("burn")) {
            return when (lang) {
                AppLanguage.MARATHI -> "आज तुम्ही अंदाजे $cal kcal कॅलरीज बर्न केल्या आहेत. उत्तम प्रयत्न चालू ठेवा!"
                AppLanguage.HINDI -> "आज आपने लगभग $cal kcal कैलोरी बर्न की हैं। शानदार प्रदर्शन!"
                AppLanguage.ENGLISH -> "You have burned approximately $cal kcal today. Great job keeping active!"
            }
        }

        if (q.contains("ध्येय") || q.contains("उद्दिष्ट") || q.contains("goal") || q.contains("target") || q.contains("लक्ष्य")) {
            val remaining = (goal - steps).coerceAtLeast(0)
            return when (lang) {
                AppLanguage.MARATHI -> if (steps >= goal) "अभिनंदन! तुमचे $goal पावलांचे दैनिक ध्येय आज पूर्ण झाले आहे! 🎉" else "तुमचे दैनिक ध्येय $goal पावले आहे. उद्दिष्टासाठी आणखी $remaining पावले बाकी आहेत."
                AppLanguage.HINDI -> if (steps >= goal) "बधाई हो! आपका $goal कदमों का दैनिक लक्ष्य पूरा हो गया है! 🎉" else "आपका दैनिक लक्ष्य $goal कदम है। लक्ष्य तक पहुँचने के लिए $remaining कदम बाकी हैं।"
                AppLanguage.ENGLISH -> if (steps >= goal) "Congratulations! You reached your daily goal of $goal steps! 🎉" else "Your daily goal is $goal steps. You need $remaining more steps to achieve it."
            }
        }

        // --- 12. WORKOUT COMMANDS ---
        if (q.contains("chest") || q.contains("छाती") || q.contains("डंबेल") || q.contains("dumbbell") || q.contains("पुश") || q.contains("pushup")) {
            selectTab(AppTab.WORKOUT)
            return when (lang) {
                AppLanguage.MARATHI -> "छातीसाठी डंबेल बेंच प्रेस आणि पुश-अप्स हे सर्वोत्तम व्यायाम आहेत. ३ सेट्समध्ये १० ते १२ रेप्स करा. सविस्तर ३-स्टेप पद्धती पाहण्यासाठी मी Workout टॅब उघडला आहे!"
                AppLanguage.HINDI -> "चेस्ट के लिए डंबल बेंच प्रेस और पुश-अप्स सबसे बेहतरीन एक्सरसाइज हैं। ३ सेट्स में १० से १२ रेप्स करें। विस्तार से देखने के लिए Workout टैब देखें!"
                AppLanguage.ENGLISH -> "For chest development, Dumbbell Bench Press and Push-Ups are top tier. Aim for 3 sets of 10-12 reps. I have opened the Workout tab for full 3-step guides!"
            }
        }

        if (q.contains("वर्कआउट") || q.contains("workout") || q.contains("व्यायाम")) {
            selectTab(AppTab.WORKOUT)
            return when (lang) {
                AppLanguage.MARATHI -> "मी तुमच्यासाठी Workout टॅब उघडला आहे! येथे छाती, हात, पाठ, खांदे, पाय आणि अ‍ॅब्सचे डंबेल व विना-डंबेल ३-स्टेप व्यायाम उपलब्ध आहेत."
                AppLanguage.HINDI -> "मैंने आपके लिए Workout टैब खोल दिया है! यहाँ चेस्ट, आर्म्स, बैक, शोल्डर, लेग्स और एब्स के ३-स्टेप एक्सरसाइज उपलब्ध हैं।"
                AppLanguage.ENGLISH -> "I opened the Workout tab for you! Here you can explore 3-step guides for Chest, Arms, Back, Shoulders, Legs, and Core with and without dumbbells."
            }
        }

        if (q.contains("सल्ला") || q.contains("टिप") || q.contains("फिटनेस") || q.contains("tip") || q.contains("advice")) {
            return when (lang) {
                AppLanguage.MARATHI -> "आजचा फिटनेस सल्ला: दररोज किमान ३० मिनिटे जलद गतीने चालल्याने हृदय निरोगी राहते आणि ऊर्जा वाढते. नियमित भरपूर पाणी प्या!"
                AppLanguage.HINDI -> "आज का फिटनेस टिप: रोज़ाना कम से कम 30 मिनट तेज़ी से टहलने से दिल स्वस्थ रहता है। पर्याप्त पानी पिएं!"
                AppLanguage.ENGLISH -> "Today's Fitness Tip: Walking briskly for 30 minutes daily boosts cardiovascular health and metabolism. Stay hydrated!"
            }
        }

        if (q.contains("तू कोण") || q.contains("नाव काय") || q.contains("who are you") || q.contains("तुम कौन") || q.contains("shivai")) {
            return when (lang) {
                AppLanguage.MARATHI -> "मी ShivAI आहे, तुमचा वैयक्तिक व्हॉइस असिस्टंट, फिटनेस ट्रॅकर आणि मोबाईल कंट्रोलर! मी २४ तास पावले मोजतो, फोन कंट्रोल्स चालवतो आणि वर्कआउट सल्ले देतो."
                AppLanguage.HINDI -> "मैं ShivAI हूँ, आपका पर्सनल वॉयस असिस्टेंट, फिटनेस ट्रैकर और मोबाइल कंट्रोलर! मैं 24 घंटे कदम ट्रैक करता हूँ और फोन कंट्रोल्स संभालता हूँ।"
                AppLanguage.ENGLISH -> "I am ShivAI, your 24/7 personal fitness tracker, voice assistant, and phone controller! I can control device torch, sound, calls, and track steps continuously."
            }
        }

        if (q == "नमस्कार" || q == "नमस्ते" || q == "हाय" || q == "हॅलो" || q == "hello" || q == "hi" || q == "hey") {
            return when (lang) {
                AppLanguage.MARATHI -> "नमस्कार ${_profile.value.name}! मी ShivAI. २४ तास फिटनेस ट्रॅकिंग व मोबाईल कंट्रोल्स चालू आहेत. सांगा कशी मदत करू?"
                AppLanguage.HINDI -> "नमस्ते ${_profile.value.name}! मैं ShivAI हूँ। 24 घंटे ट्रैकिंग और मोबाइल कंट्रोल्स तैयार हैं। बताइए कैसे मदद करूँ?"
                AppLanguage.ENGLISH -> "Hello ${_profile.value.name}! I am ShivAI with 24/7 background tracking and device controls ready. How can I help?"
            }
        }

        return null
    }

    private fun getGracefulFallbackReply(query: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.MARATHI -> "तुमच्या प्रश्नासाठी धन्यवाद! निरोगी राहण्यासाठी दररोज चालणे, संतुलित आहार आणि व्यायाम आवश्यक आहे. मोबाईल कंट्रोल्ससाठी 'टॉर्च', 'आवाज', 'कॉल' किंवा 'कॅमेरा' बोला."
            AppLanguage.HINDI -> "आपके सवाल के लिए धन्यवाद! स्वस्थ रहने के लिए नियमित व्यायाम बहुत जरूरी है। मोबाइल कंट्रोल्स के लिए 'टॉर्च', 'आवाज', 'कॉल' या 'कैमरा' बोलें।"
            AppLanguage.ENGLISH -> "Thank you! For a healthy routine, keep walking daily. For phone controls, simply say 'Torch', 'Volume up', 'Call', or 'Camera'."
        }
    }

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        if (!_profile.value.isAiEnabled) {
            _statusMessage.value = "AI बंद आहे"
            return
        }

        // Add user message to chat history and persist to Room
        _chatHistory.value = _chatHistory.value + ChatMessage(sender = MessageSender.USER, text = trimmed)
        viewModelScope.launch {
            repository.logVoiceInteraction(
                sender = "USER",
                text = trimmed,
                languageCode = _profile.value.language.code
            )
        }

        // 1. Check instant local commands first for immediate feedback
        val localResponse = handleLocalCommand(trimmed, _profile.value.language)
        if (localResponse != null) {
            _aiStatus.value = AiUiStatus.Success(localResponse)
            _chatHistory.value = _chatHistory.value + ChatMessage(sender = MessageSender.SHIVAI, text = localResponse)
            _statusMessage.value = "माईकवर टॅप करा आणि बोला..."
            viewModelScope.launch {
                repository.logVoiceInteraction(
                    sender = "SHIVAI",
                    text = localResponse,
                    languageCode = _profile.value.language.code
                )
            }
            if (_profile.value.isAutoTtsEnabled) {
                ttsHelper.speak(localResponse, _profile.value.language)
            }
            return
        }

        // 2. Call Firebase AI Logic (gemini-3.8-flash) streaming
        _statusMessage.value = "विचार करत आहे..."
        _aiStatus.value = AiUiStatus.Thinking

        viewModelScope.launch {
            val prompt = """
                तू ShivAI आहेस - एक मराठी/भारतीय व्हॉइस असिस्टंट, फिटनेस ट्रॅकर आणि मोबाईल कंट्रोलर.
                भाषा: ${_profile.value.language.name}
                उत्तर २ ते ३ वाक्यांत संक्षिप्त, प्रभावी आणि स्पष्ट दे, जेणेकरून ते आवाजात ऐकणे सोपे होईल.
                युझरची आजची पावले: ${_todaySteps.value} (दैनिक उद्दिष्ट: ${_profile.value.dailyGoal}).
                युझरचे नाव: ${_profile.value.name}.

                युझर म्हणाला: $trimmed
            """.trimIndent()

            val accumulatedText = StringBuilder()

            fun emitResponse(reply: String) {
                _aiStatus.value = AiUiStatus.Success(reply)
                _chatHistory.value = _chatHistory.value + ChatMessage(sender = MessageSender.SHIVAI, text = reply)
                _statusMessage.value = "माईकवर टॅप करा आणि बोला..."
                viewModelScope.launch {
                    repository.logVoiceInteraction(
                        sender = "SHIVAI",
                        text = reply,
                        languageCode = _profile.value.language.code
                    )
                }
                if (_profile.value.isAutoTtsEnabled) {
                    ttsHelper.speak(reply, _profile.value.language)
                }
            }

            try {
                withTimeout(45_000L) {
                    generativeModel.generateContentStream(prompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulatedText.append(chunkText)
                        _aiStatus.value = AiUiStatus.Streaming(accumulatedText.toString())
                    }
                }

                val finalReply = accumulatedText.toString().trim()
                if (finalReply.isNotEmpty()) {
                    emitResponse(finalReply)
                } else {
                    val fallback = getGracefulFallbackReply(trimmed, _profile.value.language)
                    emitResponse(fallback)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Generation timed out", e)
                val fallback = getGracefulFallbackReply(trimmed, _profile.value.language)
                emitResponse(fallback)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI generation exception, using graceful fallback", e)
                val fallback = getGracefulFallbackReply(trimmed, _profile.value.language)
                emitResponse(fallback)
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
        // Note: StepTrackingService continues in background for true 24/7 continuous tracking!
        speechHelper.stopListening()
        ttsHelper.shutdown()
    }
}
