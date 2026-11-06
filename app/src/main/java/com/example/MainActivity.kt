package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.data.AppTab
import com.example.ui.components.ShivAiBottomBar
import com.example.ui.components.ShivAiTopBar
import com.example.ui.components.VoiceCommandsModal
import com.example.ui.screens.FitnessScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InformationScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SettingScreen
import com.example.ui.theme.ShivAiTheme
import com.example.ui.theme.SlateBackground
import com.example.util.configureAppCheck
import com.example.viewmodel.ShivAiViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ShivAiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check initialization with launch intent as per firebase-ai-logic-android contract
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            ShivAiTheme {
                ShivAiApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShivAiApp(viewModel: ShivAiViewModel) {
    val context = LocalContext.current
    var showVoiceCommandsModal by remember { mutableStateOf(false) }
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val aiStatus by viewModel.aiStatus.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val chatHistory by viewModel.chatHistory.collectAsState()
    val todaySteps by viewModel.todaySteps.collectAsState()
    val todayCalories by viewModel.todayCalories.collectAsState()
    val monthHistory by viewModel.monthHistory.collectAsState()
    val isSpeaking by viewModel.ttsHelper.isSpeaking.collectAsState()

    // Microphone audio permission launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceInput { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                context,
                "व्हॉइस इनपुटसाठी मायक्रोफोन परवानगी आवश्यक आहे.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun requestVoiceInput() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.startVoiceInput { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { username, password ->
                val ok = viewModel.login(username, password)
                if (ok) {
                    Toast.makeText(context, "लॉगिन यशस्वी झाले!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    } else {
        // BackHandler for sub-tabs to navigate back to Home
        BackHandler(enabled = currentTab != AppTab.HOME) {
            viewModel.selectTab(AppTab.HOME)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ShivAiTopBar(
                    isAiEnabled = profile.isAiEnabled,
                    languageCode = profile.language.code,
                    onOpenVoiceCommands = { showVoiceCommandsModal = true }
                )
            },
            bottomBar = {
                ShivAiBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            },
            containerColor = SlateBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(SlateBackground)
            ) {
                Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                    when (tab) {
                        AppTab.HOME -> {
                            HomeScreen(
                                profile = profile,
                                aiStatus = aiStatus,
                                statusMessage = statusMessage,
                                chatHistory = chatHistory,
                                isSpeaking = isSpeaking,
                                onToggleAi = { viewModel.toggleAi() },
                                onStartMic = { requestVoiceInput() },
                                onStopMic = { viewModel.stopVoiceInput() },
                                onSendMessage = { text -> viewModel.sendMessage(text) },
                                onSpeakMessage = { text -> viewModel.speakText(text) },
                                onStopSpeaking = { viewModel.stopSpeaking() },
                                onOpenVoiceCommands = { showVoiceCommandsModal = true }
                            )
                        }
                        AppTab.FITNESS -> {
                            FitnessScreen(
                                todaySteps = todaySteps,
                                todayCalories = todayCalories,
                                dailyGoal = profile.dailyGoal,
                                monthHistory = monthHistory,
                                onAddSteps = { amount -> viewModel.addSteps(amount) }
                            )
                        }
                        AppTab.INFORMATION -> {
                            InformationScreen(profile = profile)
                        }
                        AppTab.SETTING -> {
                            SettingScreen(
                                profile = profile,
                                onUpdateName = { name -> viewModel.updateProfileName(name) },
                                onUpdateGoal = { goal -> viewModel.updateDailyGoal(goal) },
                                onSelectLanguage = { lang -> viewModel.setLanguage(lang) },
                                onToggleAutoTts = { viewModel.toggleAutoTts() },
                                onTestTts = {
                                    val testPhrase = when (profile.language) {
                                        com.example.data.AppLanguage.MARATHI -> "नमस्कार, मी ShivAI आहे. तुमचा आवाज उत्तम ऐकू येत आहे!"
                                        com.example.data.AppLanguage.HINDI -> "नमस्ते, मैं ShivAI हूँ। आपकी आवाज़ बहुत अच्छी सुनाई दे रही है!"
                                        com.example.data.AppLanguage.ENGLISH -> "Hello, I am ShivAI. Audio test is working perfectly!"
                                    }
                                    viewModel.speakText(testPhrase)
                                },
                                onLogout = { viewModel.logout() }
                            )
                        }
                    }
                }
            }
        }

        if (showVoiceCommandsModal) {
            VoiceCommandsModal(
                currentLanguage = profile.language,
                onDismiss = { showVoiceCommandsModal = false },
                onSelectCommand = { command ->
                    if (currentTab != AppTab.HOME) {
                        viewModel.selectTab(AppTab.HOME)
                    }
                    viewModel.sendMessage(command)
                }
            )
        }
    }
}
