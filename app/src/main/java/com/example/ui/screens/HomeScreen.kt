package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AiUiStatus
import com.example.data.ChatMessage
import com.example.data.MessageSender
import com.example.data.UserProfile
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FitnessGreen
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    profile: UserProfile,
    aiStatus: AiUiStatus,
    statusMessage: String,
    chatHistory: List<ChatMessage>,
    isSpeaking: Boolean,
    isTorchOn: Boolean = false,
    onToggleAi: () -> Unit,
    onStartMic: () -> Unit,
    onStopMic: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSpeakMessage: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onOpenVoiceCommands: () -> Unit,
    onToggleTorch: () -> Unit = {},
    onRaiseVolume: () -> Unit = {},
    onLowerVolume: () -> Unit = {},
    onMuteVolume: () -> Unit = {},
    onOpenDialer: () -> Unit = {},
    onOpenWhatsApp: () -> Unit = {},
    onOpenAlarm: () -> Unit = {},
    onOpenCamera: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenMaps: () -> Unit = {},
    onOpenYouTube: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll chat to bottom on new message
    LaunchedEffect(chatHistory.size, aiStatus) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    // Ripple pulse animation for mic when listening
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (aiStatus is AiUiStatus.Listening) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. AI Status Toggle Container
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_ai_toggle_container"),
                colors = CardDefaults.cardColors(
                    containerColor = SlateSurface.copy(alpha = 0.85f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI Status",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (profile.isAiEnabled) "AI सध्या चालू आहे (व्हॉइस & कंट्रोल्स)" else "AI बंद आहे",
                            fontSize = 12.sp,
                            color = if (profile.isAiEnabled) FitnessGreen else TextSecondary,
                            modifier = Modifier.testTag("home_ai_status_label")
                        )
                    }

                    Switch(
                        checked = profile.isAiEnabled,
                        onCheckedChange = { onToggleAi() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CyanPrimary,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SlateSurfaceVariant
                        ),
                        modifier = Modifier.testTag("home_ai_switch_toggle")
                    )
                }
            }
        }

        // 2. MOBILE CONTROL CENTER (मोबाईल नियंत्रण केंद्र)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_mobile_control_center_card"),
                colors = CardDefaults.cardColors(
                    containerColor = SlateSurface.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📱", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "मोबाईल नियंत्रण केंद्र (Phone Controls)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "१-टॅप किंवा आवाजाने फोन चालवा",
                                    fontSize = 11.sp,
                                    color = CyanPrimary
                                )
                            }
                        }

                        // Torch status pill indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isTorchOn) FitnessGreen.copy(alpha = 0.2f) else SlateSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isTorchOn) "🔦 टॉर्च ON" else "🔦 टॉर्च OFF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTorchOn) FitnessGreen else TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Action Buttons Grid
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickControlChip(
                            icon = "🔦",
                            label = if (isTorchOn) "टॉर्च बंद" else "टॉर्च चालू",
                            isHighlight = isTorchOn,
                            onClick = onToggleTorch,
                            tag = "control_torch_btn"
                        )
                        QuickControlChip(
                            icon = "🔊",
                            label = "आवाज +",
                            onClick = onRaiseVolume,
                            tag = "control_vol_up_btn"
                        )
                        QuickControlChip(
                            icon = "🔉",
                            label = "आवाज -",
                            onClick = onLowerVolume,
                            tag = "control_vol_down_btn"
                        )
                        QuickControlChip(
                            icon = "🔇",
                            label = "म्युट",
                            onClick = onMuteVolume,
                            tag = "control_mute_btn"
                        )
                        QuickControlChip(
                            icon = "📞",
                            label = "फोन कॉल",
                            onClick = onOpenDialer,
                            tag = "control_call_btn"
                        )
                        QuickControlChip(
                            icon = "💬",
                            label = "WhatsApp",
                            onClick = onOpenWhatsApp,
                            tag = "control_whatsapp_btn"
                        )
                        QuickControlChip(
                            icon = "⏰",
                            label = "अलार्म",
                            onClick = onOpenAlarm,
                            tag = "control_alarm_btn"
                        )
                        QuickControlChip(
                            icon = "📷",
                            label = "कॅमेरा",
                            onClick = onOpenCamera,
                            tag = "control_camera_btn"
                        )
                        QuickControlChip(
                            icon = "⚙️",
                            label = "सेटिंग्ज",
                            onClick = onOpenSettings,
                            tag = "control_settings_btn"
                        )
                        QuickControlChip(
                            icon = "🗺️",
                            label = "नकाशा",
                            onClick = onOpenMaps,
                            tag = "control_maps_btn"
                        )
                        QuickControlChip(
                            icon = "📺",
                            label = "YouTube",
                            onClick = onOpenYouTube,
                            tag = "control_youtube_btn"
                        )
                    }
                }
            }
        }

        // 3. Voice Commands Info Guide Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenVoiceCommands() }
                    .testTag("home_voice_commands_guide_banner"),
                colors = CardDefaults.cardColors(
                    containerColor = SlateSurface.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "व्हॉइस कमांड्स मार्गदर्शक (Voice Commands)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "उदा. 'टॉर्च चालू करा', 'आवाज वाढवा', 'कॉल करा'",
                                fontSize = 11.sp,
                                color = CyanPrimary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyanDark.copy(alpha = 0.35f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "पहा ➔",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                    }
                }
            }
        }

        // 4. Mic Box (Voice input circular button)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    if (aiStatus is AiUiStatus.Listening) {
                        Box(
                            modifier = Modifier
                                .size(105.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.25f))
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(
                                elevation = if (profile.isAiEnabled) 16.dp else 0.dp,
                                shape = CircleShape,
                                spotColor = CyanPrimary
                            )
                            .clip(CircleShape)
                            .background(
                                if (profile.isAiEnabled) {
                                    if (aiStatus is AiUiStatus.Listening) DangerRed else CyanPrimary
                                } else {
                                    SlateBorder
                                }
                            )
                            .clickable(enabled = profile.isAiEnabled) {
                                if (aiStatus is AiUiStatus.Listening) {
                                    onStopMic()
                                } else {
                                    onStartMic()
                                }
                            }
                            .testTag("home_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (aiStatus is AiUiStatus.Thinking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (aiStatus is AiUiStatus.Listening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (aiStatus is AiUiStatus.Listening) "Stop" else "Speak",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusMessage,
                    color = if (aiStatus is AiUiStatus.Error) DangerRed else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("home_status_message_text")
                )

                if (isSpeaking) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyanDark.copy(alpha = 0.4f))
                            .clickable { onStopSpeaking() }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speaking",
                            tint = CyanPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "बोलत आहे... (थांबवा)",
                            color = CyanPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 5. Quick Suggestion Prompts
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "त्वरित प्रश्न व आज्ञा (Quick Prompts):",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val suggestions = when (profile.language.code) {
                        "hi-IN" -> listOf(
                            "🔦 टॉर्च चालू करो",
                            "🔊 आवाज बढ़ाओ",
                            "📞 फोन डायल करो",
                            "💬 WhatsApp खोलो",
                            "🚶 आज कितने कदम चले?",
                            "🏋️ चेस्ट वर्कआउट कैसे करें?",
                            "🔥 कितनी कैलोरी बर्न हुई?",
                            "➕ ५०० कदम जोड़ें"
                        )
                        "en-US" -> listOf(
                            "🔦 Turn on flashlight",
                            "🔊 Volume up",
                            "📞 Open phone dialer",
                            "💬 Open WhatsApp",
                            "🚶 How many steps today?",
                            "🏋️ How to do chest workout?",
                            "🔥 Calories burned today?",
                            "➕ Add 500 steps"
                        )
                        else -> listOf(
                            "🔦 टॉर्च चालू करा",
                            "🔊 आवाज वाढवा",
                            "📞 फोन लावा",
                            "💬 WhatsApp उघडा",
                            "🚶 आज किती पावले चाललो?",
                            "🏋️ चेस्ट वर्कआउट कसा करायचा?",
                            "🔥 आज किती कॅलरीज जळाल्या?",
                            "➕ ५०० पावले जोडा"
                        )
                    }

                    suggestions.forEach { prompt ->
                        SuggestionChip(
                            onClick = {
                                if (profile.isAiEnabled) {
                                    onSendMessage(prompt)
                                }
                            },
                            label = { Text(prompt, fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = SlateSurface.copy(alpha = 0.9f),
                                labelColor = TextPrimary
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = SlateBorder.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("home_suggestion_chip")
                        )
                    }
                }
            }
        }

        // 6. Text Input Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = if (profile.isAiEnabled) "मेसेज किंवा आज्ञा टाईप करा..." else "AI सध्या बंद आहे",
                            fontSize = 14.sp
                        )
                    },
                    enabled = profile.isAiEnabled,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = CyanPrimary,
                        focusedContainerColor = SlateSurface,
                        unfocusedContainerColor = SlateSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_text_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank() && profile.isAiEnabled) {
                            onSendMessage(textInput)
                            textInput = ""
                        }
                    },
                    enabled = textInput.isNotBlank() && profile.isAiEnabled,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = CyanPrimary,
                        contentColor = Color.White,
                        disabledContainerColor = SlateSurfaceVariant,
                        disabledContentColor = TextSecondary
                    ),
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .testTag("home_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 7. Streaming Response Preview Banner
        if (aiStatus is AiUiStatus.Streaming) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_streaming_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = SlateSurface.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = CyanPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ShivAI विचार करत आहे...",
                                color = CyanPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = aiStatus.partialText,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // 8. Chat History List
        items(chatHistory, key = { it.id }) { message ->
            ChatBubble(
                message = message,
                onSpeak = { onSpeakMessage(message.text) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun QuickControlChip(
    icon: String,
    label: String,
    isHighlight: Boolean = false,
    onClick: () -> Unit,
    tag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isHighlight) FitnessGreen.copy(alpha = 0.25f) else SlateSurface.copy(alpha = 0.9f)
            )
            .border(
                1.dp,
                if (isHighlight) FitnessGreen else SlateBorder.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = icon, fontSize = 14.sp)
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isHighlight) FitnessGreen else TextPrimary
            )
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isShivAi = message.sender == MessageSender.SHIVAI

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isShivAi) Arrangement.Start else Arrangement.End
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag(if (isShivAi) "chat_bubble_shivai" else "chat_bubble_user"),
            colors = CardDefaults.cardColors(
                containerColor = if (isShivAi) SlateSurface.copy(alpha = 0.95f) else CyanDark.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isShivAi) 4.dp else 16.dp,
                bottomEnd = if (isShivAi) 16.dp else 4.dp
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isShivAi) SlateBorder.copy(alpha = 0.4f) else CyanPrimary.copy(alpha = 0.4f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isShivAi) "ShivAI" else "तुम्ही (You)",
                        color = if (isShivAi) CyanPrimary else FitnessGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    if (isShivAi) {
                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("chat_listen_tts_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen",
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.text,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }
        }
    }
}
