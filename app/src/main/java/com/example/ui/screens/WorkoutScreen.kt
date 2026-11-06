package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BodyPart
import com.example.data.ExerciseStep
import com.example.data.WorkoutDatabase
import com.example.data.WorkoutEquipment
import com.example.data.WorkoutExercise
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.CyanPrimaryVariant
import com.example.ui.theme.FitnessGreen
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkoutScreen(
    onSpeakText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBodyPart by remember { mutableStateOf<BodyPart?>(BodyPart.CHEST) }
    var selectedEquipment by remember { mutableStateOf<WorkoutEquipment?>(null) } // null = All
    var expandedExerciseId by remember { mutableStateOf<String?>("chest_dumbbell_bench_press") }

    val filteredExercises = remember(selectedBodyPart, selectedEquipment) {
        WorkoutDatabase.exercises.filter { exercise ->
            val matchBody = selectedBodyPart == null || exercise.bodyPart == selectedBodyPart
            val matchEquip = selectedEquipment == null || exercise.equipment == selectedEquipment
            matchBody && matchEquip
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. HERO BANNER WITH GRADIENT
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("workout_hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_workout_hero),
                        contentDescription = "Workout Hero Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF0F172A).copy(alpha = 0.85f),
                                        Color(0xFF0F172A)
                                    ),
                                    startY = 50f
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyanPrimary)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "WORKOUT MASTER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "३-स्टेप व्यायाम पद्धती",
                                fontSize = 12.sp,
                                color = FitnessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "डंबेल आणि विना-डंबेल व्यायाम मार्गदर्शक",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "प्रत्येक व्यायाम कसा करावा याचे सविस्तर ३-स्टेप मार्गदर्शन",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // 2. EQUIPMENT TOGGLE: [सर्व] | [🏋️ डंबेल] | [🤸 विना-डंबेल (बॉडीवेट)]
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "व्यायामाचा प्रकार निवडा (Equipment Type):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All Chip
                    FilterEquipmentButton(
                        text = "सर्व व्यायाम",
                        icon = "⚡",
                        isSelected = selectedEquipment == null,
                        onClick = { selectedEquipment = null },
                        modifier = Modifier.weight(1f)
                    )

                    // Dumbbell Chip
                    FilterEquipmentButton(
                        text = "डंबेलसह",
                        icon = "🏋️",
                        isSelected = selectedEquipment == WorkoutEquipment.DUMBBELL,
                        onClick = { selectedEquipment = WorkoutEquipment.DUMBBELL },
                        modifier = Modifier.weight(1f)
                    )

                    // Bodyweight Chip
                    FilterEquipmentButton(
                        text = "विना-डंबेल",
                        icon = "🤸",
                        isSelected = selectedEquipment == WorkoutEquipment.BODYWEIGHT,
                        onClick = { selectedEquipment = WorkoutEquipment.BODYWEIGHT },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. BODY PARTS SELECTOR ROW (Chest, Back, Arms, Shoulders, Legs, Abs)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "शरीराचा भाग (Body Part):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${filteredExercises.size} व्यायाम उपलब्ध",
                        fontSize = 11.sp,
                        color = CyanPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(BodyPart.entries) { part ->
                        val isSelected = selectedBodyPart == part
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) CyanPrimary else SlateSurface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) CyanPrimary else SlateBorder.copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedBodyPart = if (selectedBodyPart == part) null else part
                                }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                                .testTag("body_part_${part.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = part.icon, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = part.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF0F172A) else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. SPECIAL FEATURED CHEST ILLUSTRATION CARD (when chest is active)
        if (selectedBodyPart == BodyPart.CHEST || selectedBodyPart == null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workout_chest_illustration_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.9f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🛡️", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "छातीचे मुख्य व्यायाम (Chest Form Guide)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanPrimary
                                    )
                                    Text(
                                        text = "डंबेल बेंच प्रेस आणि पुश-अप्सचे प्रत्यक्ष प्रात्यक्षिक",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actual generated image asset for chest workout
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_workout_chest),
                                contentDescription = "Chest Dumbbell and Pushup Illustration",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // 5. EXERCISE CARDS LIST
        if (filteredExercises.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🏋️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "या फिल्टरसाठी व्यायाम उपलब्ध नाही.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                selectedBodyPart = null
                                selectedEquipment = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                        ) {
                            Text("सर्व व्यायाम पहा", color = Color(0xFF0F172A), fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredExercises, key = { it.id }) { exercise ->
                val isExpanded = expandedExerciseId == exercise.id

                ExerciseDetailCard(
                    exercise = exercise,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedExerciseId = if (isExpanded) null else exercise.id
                    },
                    onSpeak = {
                        val speechText = """
                            ${exercise.nameMarathi}. 
                            ${exercise.step1.titleMarathi}: ${exercise.step1.descriptionMarathi}. 
                            ${exercise.step2.titleMarathi}: ${exercise.step2.descriptionMarathi}. 
                            ${exercise.step3.titleMarathi}: ${exercise.step3.descriptionMarathi}. 
                            टीप: ${exercise.proTip}
                        """.trimIndent()
                        onSpeakText(speechText)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FilterEquipmentButton(
    text: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CyanDark.copy(alpha = 0.5f) else SlateSurface)
            .border(
                1.dp,
                if (isSelected) CyanPrimary else SlateBorder.copy(alpha = 0.35f),
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) CyanPrimary else TextPrimary
            )
        }
    }
}

@Composable
fun ExerciseDetailCard(
    exercise: WorkoutExercise,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDumbbell = exercise.equipment == WorkoutEquipment.DUMBBELL

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(300))
            .testTag("exercise_card_${exercise.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.95f)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isExpanded) CyanPrimary.copy(alpha = 0.5f) else SlateBorder.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Badges, Name, Expand toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Equipment and Body Part Badges
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isDumbbell) CyanPrimary.copy(alpha = 0.15f) else FitnessGreen.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = exercise.equipment.badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDumbbell) CyanPrimary else FitnessGreen
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SlateBackground)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = exercise.setsReps,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = exercise.nameMarathi,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = exercise.nameEnglish,
                        fontSize = 11.sp,
                        color = CyanPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Speak Guide",
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Target Muscles Tag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "स्नायू: ",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = exercise.targetMuscles,
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
            }

            // EXPANDED 3-STEP VISUAL INSTRUCTION DETAILS
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(
                        color = SlateBorder.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "कसे करावे? ३-स्टेप मार्गदर्शन (3-Step Guide):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    // Step 1: Starting Position
                    StepTimelineItem(
                        step = exercise.step1,
                        stepColor = CyanPrimary,
                        isLast = false
                    )

                    // Step 2: Movement & Lowering
                    StepTimelineItem(
                        step = exercise.step2,
                        stepColor = Color(0xFFFBBF24), // Amber
                        isLast = false
                    )

                    // Step 3: Peak Contraction & Return
                    StepTimelineItem(
                        step = exercise.step3,
                        stepColor = FitnessGreen,
                        isLast = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pro-Tip Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "💡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "महत्त्वाची टीप (Pro-Tip):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFBBF24)
                                )
                                Text(
                                    text = exercise.proTip,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepTimelineItem(
    step: ExerciseStep,
    stepColor: Color,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Step Number Badge with connector line
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(stepColor.copy(alpha = 0.2f))
                    .border(1.5.dp, stepColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${step.stepNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = stepColor
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(stepColor.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Step Content Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SlateBackground.copy(alpha = 0.7f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = step.titleMarathi,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = stepColor
                    )
                    Text(text = step.visualIcon, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = step.descriptionMarathi,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "🎯 लक्ष: ${step.focusTip}",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
