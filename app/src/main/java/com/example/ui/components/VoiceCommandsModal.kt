package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FitnessGreen
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class VoiceCommandItem(
    val category: String,
    val commandText: String,
    val englishHint: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCommandsModal(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSelectCommand: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    val commands = remember(selectedLanguage) {
        when (selectedLanguage) {
            AppLanguage.HINDI -> listOf(
                VoiceCommandItem(
                    category = "📱 मोबाइल कंट्रोल्स",
                    commandText = "टॉर्च चालू करो",
                    englishHint = "Turn on flashlight",
                    description = "मोबाइल का फ्लैशलाइट/टॉर्च तुरंत चालू करता है।"
                ),
                VoiceCommandItem(
                    category = "📱 मोबाइल कंट्रोल्स",
                    commandText = "आवाज बढ़ाओ",
                    englishHint = "Volume up",
                    description = "मोबाइल का मीडिया वॉल्यूम बढ़ाता है।"
                ),
                VoiceCommandItem(
                    category = "📱 मोबाइल कंट्रोल्स",
                    commandText = "फोन कॉल लगाओ",
                    englishHint = "Open phone dialer",
                    description = "कॉल करने के लिए फोन डायलर खोलता है।"
                ),
                VoiceCommandItem(
                    category = "📱 मोबाइल कंट्रोल्स",
                    commandText = "WhatsApp खोलो",
                    englishHint = "Open WhatsApp",
                    description = "मैसेजिंग के लिए सीधे WhatsApp खोलता है।"
                ),
                VoiceCommandItem(
                    category = "📱 मोबाइल कंट्रोल्स",
                    commandText = "कैमरा खोलो",
                    englishHint = "Open camera",
                    description = "फोटो खींचने के लिए तुरंत कैमरा चालू करता है।"
                ),
                VoiceCommandItem(
                    category = "📱 मोबाइल कंट्रोल्स",
                    commandText = "अलार्म खोलो",
                    englishHint = "Open alarm clock",
                    description = "अलार्म सेट करने के लिए अलार्म क्लॉक खोलता है।"
                ),
                VoiceCommandItem(
                    category = "🏃 फिटनेस और स्टेप्स",
                    commandText = "आज मैंने कितने कदम चले हैं?",
                    englishHint = "How many steps today?",
                    description = "24 घंटे बैकग्राउंड ट्रैकर से आज के कुल कदम और लक्ष्य की जानकारी।"
                ),
                VoiceCommandItem(
                    category = "🏃 फिटनेस और स्टेप्स",
                    commandText = "मैंने आज कितनी कैलोरी बर्न की?",
                    englishHint = "How many calories burned today?",
                    description = "आज खर्च की गई कैलोरी का विवरण।"
                ),
                VoiceCommandItem(
                    category = "🏋️ वर्कआउट गाइड",
                    commandText = "चेस्ट वर्कआउट कैसे करें?",
                    englishHint = "Chest workout guide",
                    description = "डंबल और बिना-डंबल 3-स्टेप चेस्ट एक्सरसाइज।"
                ),
                VoiceCommandItem(
                    category = "🎯 लक्ष्य और सेटिंग्स",
                    commandText = "मेरा दैनिक लक्ष्य 8,000 कदम कर दो",
                    englishHint = "Update my goal to 8000 steps",
                    description = "दैनिक स्टेप लक्ष्य को अपडेट करने के लिए।"
                ),
                VoiceCommandItem(
                    category = "🥗 स्वास्थ्य और डाइट टिप्स",
                    commandText = "आज का फिटनेस टिप दो",
                    englishHint = "Give me a fitness tip",
                    description = "व्यायाम, स्ट्रेचिंग या डाइट के लिए त्वरित सलाह।"
                ),
                VoiceCommandItem(
                    category = "🤖 ShivAI असिस्टेंट",
                    commandText = "ShivAI तुम क्या कर सकते हो?",
                    englishHint = "What can you do ShivAI?",
                    description = "24 घंटे फिटनेस ट्रैकिंग और मोबाइल कंट्रोल्स की क्षमताएं।"
                )
            )
            AppLanguage.ENGLISH -> listOf(
                VoiceCommandItem(
                    category = "📱 Mobile Controls",
                    commandText = "Turn on flashlight",
                    englishHint = "Turn on flashlight",
                    description = "Instantly turns on device torch / flash."
                ),
                VoiceCommandItem(
                    category = "📱 Mobile Controls",
                    commandText = "Volume up",
                    englishHint = "Volume up",
                    description = "Raises mobile media sound volume."
                ),
                VoiceCommandItem(
                    category = "📱 Mobile Controls",
                    commandText = "Open phone dialer",
                    englishHint = "Open phone dialer",
                    description = "Launches the phone calling dialer."
                ),
                VoiceCommandItem(
                    category = "📱 Mobile Controls",
                    commandText = "Open WhatsApp",
                    englishHint = "Open WhatsApp",
                    description = "Directly launches WhatsApp on the phone."
                ),
                VoiceCommandItem(
                    category = "📱 Mobile Controls",
                    commandText = "Open camera",
                    englishHint = "Open camera",
                    description = "Opens the device camera to take photos."
                ),
                VoiceCommandItem(
                    category = "📱 Mobile Controls",
                    commandText = "Open alarm clock",
                    englishHint = "Open alarm clock",
                    description = "Opens the alarm clock to set alarms."
                ),
                VoiceCommandItem(
                    category = "🏃 Fitness & Step Tracking",
                    commandText = "How many steps have I walked today?",
                    englishHint = "How many steps today?",
                    description = "Checks 24h background step count and progress."
                ),
                VoiceCommandItem(
                    category = "🏃 Fitness & Step Tracking",
                    commandText = "How many calories have I burned today?",
                    englishHint = "How many calories burned?",
                    description = "Reports estimated calories burned so far."
                ),
                VoiceCommandItem(
                    category = "🏋️ Workout Guides",
                    commandText = "How to do chest workout?",
                    englishHint = "Chest workout guide",
                    description = "3-step visual chest exercises with/without dumbbells."
                ),
                VoiceCommandItem(
                    category = "🎯 Goals & Preferences",
                    commandText = "Update my goal to 8,000 steps",
                    englishHint = "Update my goal",
                    description = "Modifies your daily fitness target."
                ),
                VoiceCommandItem(
                    category = "🥗 Workout & Health Advice",
                    commandText = "Give me a daily fitness tip",
                    englishHint = "Daily fitness tip",
                    description = "Actionable tips for workouts and hydration."
                ),
                VoiceCommandItem(
                    category = "🤖 Assistant Capabilities",
                    commandText = "What can you do, ShivAI?",
                    englishHint = "What can you do?",
                    description = "Learn about 24h tracking, workout guides, and mobile controls."
                )
            )
            else -> listOf(
                VoiceCommandItem(
                    category = "📱 मोबाईल कंट्रोल्स",
                    commandText = "टॉर्च चालू करा",
                    englishHint = "Turn on flashlight",
                    description = "मोबाईलचा फ्लॅशलाईट / टॉर्च लगेच चालू करतो."
                ),
                VoiceCommandItem(
                    category = "📱 मोबाईल कंट्रोल्स",
                    commandText = "आवाज वाढवा",
                    englishHint = "Volume up",
                    description = "मोबाईलचा मीडिया आवाज वाढवतो."
                ),
                VoiceCommandItem(
                    category = "📱 मोबाईल कंट्रोल्स",
                    commandText = "फोन कॉल लावा",
                    englishHint = "Open phone dialer",
                    description = "कॉल करण्यासाठी फोन डायलर स्क्रीन उघडतो."
                ),
                VoiceCommandItem(
                    category = "📱 मोबाईल कंट्रोल्स",
                    commandText = "WhatsApp उघडा",
                    englishHint = "Open WhatsApp",
                    description = "मेसेजिंगसाठी लगेच WhatsApp उघडतो."
                ),
                VoiceCommandItem(
                    category = "📱 मोबाईल कंट्रोल्स",
                    commandText = "कॅमेरा उघडा",
                    englishHint = "Open camera",
                    description = "फोटो काढण्यासाठी थेट कॅमेरा उघडतो."
                ),
                VoiceCommandItem(
                    category = "📱 मोबाईल कंट्रोल्स",
                    commandText = "अलार्म लावा",
                    englishHint = "Open alarm clock",
                    description = "अलार्म सेट करण्यासाठी घड्याळ उघडतो."
                ),
                VoiceCommandItem(
                    category = "📱 मोबाईल कंट्रोल्स",
                    commandText = "गुगल मॅप्स उघडा",
                    englishHint = "Open Google Maps",
                    description = "रस्ता व दिशा पाहण्यासाठी Google Maps उघडतो."
                ),
                VoiceCommandItem(
                    category = "📱 मोबाईल कंट्रोल्स",
                    commandText = "युट्यूब उघडा",
                    englishHint = "Open YouTube",
                    description = "गाणी व व्हिडिओसाठी YouTube उघडतो."
                ),
                VoiceCommandItem(
                    category = "🏃 फिटनेस आणि पावले",
                    commandText = "आज मी किती पावले चाललो आहे?",
                    englishHint = "How many steps today?",
                    description = "२४ तास अखंड ट्रॅकरद्वारे मोजलेली पावले आणि प्रगती सांगतो."
                ),
                VoiceCommandItem(
                    category = "🏃 फिटनेस आणि पावले",
                    commandText = "मी आज किती कॅलरी बर्न केल्या?",
                    englishHint = "How many calories burned today?",
                    description = "आज खर्च झालेल्या अंदाजे कॅलरींची माहिती."
                ),
                VoiceCommandItem(
                    category = "🎯 ध्येय आणि सेटिंग्स",
                    commandText = "माझे दैनिक ध्येय ८,००० पावले करा",
                    englishHint = "Update my goal to 8000 steps",
                    description = "दैनिक स्टेप टार्गेट अपडेट करण्यासाठी."
                ),
                VoiceCommandItem(
                    category = "🏋️ वर्कआउट व व्यायाम",
                    commandText = "छातीचा (Chest) व्यायाम कसा करावा?",
                    englishHint = "Chest workout guide",
                    description = "डंबेल आणि पुश-अप्सचे ३-स्टेप सविस्तर मार्गदर्शन."
                ),
                VoiceCommandItem(
                    category = "🏋️ वर्कआउट व व्यायाम",
                    commandText = "विना-डंबेल व्यायाम दाखवा",
                    englishHint = "Bodyweight workouts without dumbbells",
                    description = "घरच्या घरी साहित्याशिवाय करावयाचे व्यायाम."
                ),
                VoiceCommandItem(
                    category = "🥗 आरोग्य व पोषण सल्ला",
                    commandText = "आजचा फिटनेस सल्ला द्या",
                    englishHint = "Give me a daily fitness tip",
                    description = "व्यायाम, स्ट्रेचिंग व आरोग्यासाठी उपयुक्त टिप्स."
                ),
                VoiceCommandItem(
                    category = "🤖 ShivAI असिस्टंट",
                    commandText = "ShivAI तू काय करू शकतोस?",
                    englishHint = "What can you do ShivAI?",
                    description = "२४ तास फिटनेस ट्रॅकिंग व मोबाईल कंट्रोल्सची संपूर्ण माहिती."
                )
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SlateSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(SlateBorder)
            )
        },
        modifier = Modifier.testTag("voice_commands_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "व्हॉइस कमांड्स मार्गदर्शक",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Voice Commands Guide",
                            fontSize = 11.sp,
                            color = CyanPrimary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("voice_commands_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Language Selector FilterChips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLanguage.entries.forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanDark.copy(alpha = 0.4f),
                            selectedLabelColor = CyanPrimary,
                            containerColor = SlateBackground,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CyanPrimary else SlateBorder
                        ),
                        modifier = Modifier.testTag("voice_commands_lang_${lang.code}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "खालीलपैकी कोणतीही कमांड माईकवर बोला किंवा थेट टॅप करून विचारा:",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Commands List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(commands) { cmd ->
                    CommandCard(
                        item = cmd,
                        onTry = {
                            onDismiss()
                            onSelectCommand(cmd.commandText)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Close button at bottom
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("voice_commands_done_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SlateSurfaceVariant,
                    contentColor = TextPrimary
                )
            ) {
                Text("समजले (Got it)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CommandCard(
    item: VoiceCommandItem,
    onTry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTry() }
            .testTag("voice_command_item_${item.englishHint.replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = SlateBackground.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FitnessGreen
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyanDark.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Try",
                        tint = CyanPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "विचारा (Try)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "“${item.commandText}”",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.englishHint,
                fontSize = 11.sp,
                color = CyanPrimary.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
