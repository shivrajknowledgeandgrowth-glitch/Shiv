package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserProfile
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FitnessGreen
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun InformationScreen(
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Avatar & Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("info_hero_card"),
                colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(3.dp, CyanPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_shivai_avatar),
                            contentDescription = "ShivAI Hero Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = profile.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "@${profile.username}",
                            fontSize = 13.sp,
                            color = CyanPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(FitnessGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "सक्रिय सदस्य (Active)",
                                fontSize = 11.sp,
                                color = FitnessGreen
                            )
                        }
                    }
                }
            }
        }

        // 1. माझी प्रोफाइल माहिती (matching HTML .info-card)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("info_profile_details_card"),
                colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "माझी प्रोफाइल माहिती",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    InfoRow(label = "नाव:", value = profile.name, isBold = true)
                    InfoRow(label = "वापरकर्ता (User):", value = profile.username)
                    InfoRow(label = "ॲप आवृत्ती:", value = "ShivAI v1.0")
                    InfoRow(label = "उद्दिष्ट:", value = "Fitness & Voice AI Assistant")
                    InfoRow(label = "निवडलेली भाषा:", value = profile.language.displayName)
                    InfoRow(label = "AI मॉडेल:", value = "Gemini 3.8 Flash (Firebase)", isLast = true)
                }
            }
        }

        // 2. ॲप विषयी (About ShivAI) (matching HTML second .info-card)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("info_about_card"),
                colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ॲप विषयी (About ShivAI)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Text(
                        text = "ShivAI हे तुमचे वैयक्तिक व्हॉइस असिस्टंट आणि फिटनेस ट्रॅकर ॲप आहे. हे मराठी, हिंदी आणि इंग्लिश भाषांमध्ये बोलते आणि दररोजच्या पावलांची नोंद ठेवते.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // 3. Core Features Badges
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "वैशिष्ट्ये (Key Capabilities)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    FeatureItem(
                        icon = Icons.Default.Mic,
                        title = "मराठी व हिंदी व्हॉइस इनपुट",
                        subtitle = "आवाजाद्वारे थेट प्रश्न विचारा व सूचना मिळवा"
                    )
                    FeatureItem(
                        icon = Icons.Default.RecordVoiceOver,
                        title = "टेक्स्ट-टू-स्पीच (TTS)",
                        subtitle = "ShivAI चे उत्तर थेट आवाजात ऐका"
                    )
                    FeatureItem(
                        icon = Icons.Default.DirectionsRun,
                        title = "३० दिवसांचा स्टेप ट्रॅकर",
                        subtitle = "दररोजची पावले आणि कॅलरी नोंद"
                    )
                    FeatureItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "Gemini 3.8 Flash AI",
                        subtitle = "जलद आणि अचूक बुद्धिमत्ता",
                        isLast = true
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isLast: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = TextSecondary
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
                color = if (isBold) TextPrimary else CyanPrimary
            )
        }

        if (!isLast) {
            HorizontalDivider(
                color = SlateBorder.copy(alpha = 0.2f),
                thickness = 0.8.dp
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyanPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }

    if (!isLast) {
        HorizontalDivider(
            color = SlateBorder.copy(alpha = 0.15f),
            thickness = 0.6.dp
        )
    }
}
