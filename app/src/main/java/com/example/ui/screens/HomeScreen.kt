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
    onToggleAi: () -> Unit,
    onStartMic: () -> Unit,
    onStopMic: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSpeakMessage: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onOpenVoiceCommands: () -> Unit,
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
        // 1. AI Status Toggle Container (matching HTML .switch-container)
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
                            text = if (profile.isAiEnabled) "AI सध्या चालू आहे" else "AI बंद आहे",
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

        // Voice Commands Info Guide Banner
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
                                text = "उपलब्ध व्हॉइस कमांड्स (Voice Commands)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "उदा. 'आज किती पावले चाललो?', 'ध्येय अपडेट करा'",
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

        // 2. Mic Box (matching HTML .mic-box with 80px circular button)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    // Outer glowing animated ripple when listening
                    if (aiStatus is AiUiStatus.Listening) {
                        Box(
                            modifier = Modifier
                                .size(105.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.25f))
                        )
                    }

                    // 80dp Mic Button
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
                                color = Color(0xFF0F172A),
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (aiStatus is AiUiStatus.Listening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Microphone",
                                tint = if (profile.isAiEnabled) Color(0xFF0F172A) else TextSecondary,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = statusMessage,
                    fontSize = 14.sp,
                    color = if (aiStatus is AiUiStatus.Listening) CyanPrimary else TextSecondary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("home_mic_status_message")
                )

                // TTS Speaking status and Stop button
                AnimatedVisibility(visible = isSpeaking) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(SlateSurface)
                            .clickable { onStopSpeaking() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
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

        // 3. Quick Suggestion Prompts
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "त्वरित प्रश्न (Quick Questions):",
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
                            "आज का फिटनेस टिप दो",
                            "ShivAI तुम क्या कर सकते हो?",
                            "पैदल चलने के फायदे बताओ",
                            "मेरा स्टेप गोल क्या है?"
                        )
                        "en-US" -> listOf(
                            "Give me a fitness tip",
                            "What can you do ShivAI?",
                            "Benefits of 10,000 steps",
                            "Motivate me for a workout"
                        )
                        else -> listOf(
                            "आजचा फिटनेस सल्ला द्या",
                            "ShivAI तू काय करू शकतोस?",
                            "पावले चालण्याचे फायदे सांगा",
                            "व्यायामासाठी प्रेरणा द्या"
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

        // 4. Text Input Bar (Send typed query)
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
                            text = if (profile.isAiEnabled) "मेसेज टाईप करा किंवा बोला..." else "AI सध्या बंद आहे",
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
                        focusedPlaceholderColor = TextSecondary,
                        unfocusedPlaceholderColor = TextSecondary,
                        focusedContainerColor = SlateSurface,
                        unfocusedContainerColor = SlateSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_text_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank() && profile.isAiEnabled) {
                            val msg = textInput
                            textInput = ""
                            onSendMessage(msg)
                        }
                    },
                    enabled = profile.isAiEnabled && textInput.isNotBlank(),
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            if (profile.isAiEnabled && textInput.isNotBlank()) CyanPrimary else SlateBorder
                        )
                        .testTag("home_send_button"),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color(0xFF0F172A)
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 5. Streaming Live Response Banner
        if (aiStatus is AiUiStatus.Streaming) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_streaming_card"),
                    colors = CardDefaults.cardColors(containerColor = CyanDark.copy(alpha = 0.3f)),
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

        // 6. Chat History List
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
