package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DayFitnessRecord
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FitnessGreen
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FitnessScreen(
    todaySteps: Int,
    todayCalories: Int,
    dailyGoal: Int,
    monthHistory: List<DayFitnessRecord>,
    onAddSteps: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.getDefault()) }
    val progress = (todaySteps.toFloat() / dailyGoal.coerceAtLeast(1)).coerceIn(0f, 1f)
    val distanceKm = (todaySteps * 0.00075f)
    val activeMinutes = (todaySteps / 100).coerceAtLeast(1)
    var selectedDayRecord by remember { mutableStateOf<DayFitnessRecord?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fitness Overview",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanPrimary,
                    modifier = Modifier.testTag("fitness_header_title")
                )

                Text(
                    text = "Goal: ${numberFormat.format(dailyGoal)} steps",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 1. HERO CIRCULAR PROGRESS CARD: Today's Steps vs User-Defined Daily Goal
        item {
            StepGoalCircularProgressCard(
                todaySteps = todaySteps,
                dailyGoal = dailyGoal,
                todayCalories = todayCalories,
                distanceKm = distanceKm,
                numberFormat = numberFormat
            )
        }

        // 2. Metric Cards Grid (Today's Steps & Today's Calories)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today's Steps Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fitness_card_today_steps"),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "आजची पावले",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = numberFormat.format(todaySteps),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitnessGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = FitnessGreen,
                            trackColor = SlateSurfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                // Today's Calories Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fitness_card_today_calories"),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "आजच्या कॅलरी",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$todayCalories kcal",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitnessGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🔥 अंदाजे ऊर्जा",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // 3. Extra Distance & Active Time Badges
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Straighten, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "अंतर", fontSize = 11.sp, color = TextSecondary)
                            Text(text = String.format(Locale.getDefault(), "%.1f km", distanceKm), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "वेळ", fontSize = 11.sp, color = TextSecondary)
                            Text(text = "$activeMinutes min", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // 4. Quick Step Logger Controls (+250, +500, +1000)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "पावले जोडा (Quick Step Logger):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(250, 500, 1000).forEach { amount ->
                            Button(
                                onClick = { onAddSteps(amount) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("fitness_add_steps_$amount"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanDark.copy(alpha = 0.6f),
                                    contentColor = CyanPrimary
                                )
                            ) {
                                Text(
                                    text = "+$amount",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. 30-Day Activity Trend Visualizer (Compose Canvas)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "३० दिवसांचा ट्रेंड चार्ट",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "सरासरी: ५,२८० पावले",
                            fontSize = 11.sp,
                            color = FitnessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Canvas Bar Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxSteps = 8000f
                            val barCount = monthHistory.size.coerceAtLeast(1)
                            val spacing = size.width / barCount
                            val barWidth = (spacing * 0.65f).coerceAtLeast(3f)

                            monthHistory.forEachIndexed { index, record ->
                                val barHeight = (record.steps / maxSteps) * size.height
                                val x = index * spacing + (spacing - barWidth) / 2
                                val y = size.height - barHeight

                                val isGoalMet = record.steps >= dailyGoal
                                val barColor = if (isGoalMet) Color(0xFF4ADE80) else Color(0xFF38BDF8)

                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(barColor, barColor.copy(alpha = 0.3f)),
                                        startY = y,
                                        endY = size.height
                                    ),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(4f, 4f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. 1 Month History List Header
        item {
            Text(
                text = "१ महिन्याची माहिती (३० दिवसांचा इतिहास)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCBD5E1),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .testTag("fitness_history_section_title")
            )
        }

        // 7. History List Items
        items(monthHistory, key = { it.dayNumber }) { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedDayRecord = record }
                    .testTag("fitness_history_item_${record.dayNumber}"),
                colors = CardDefaults.cardColors(
                    containerColor = SlateSurface.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = SlateBorder.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${record.dayNumber}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = record.label,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${numberFormat.format(record.steps)} पावले",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (record.steps >= dailyGoal) FitnessGreen else CyanPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${record.calories} kcal)",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Prominent circular progress indicator comparing today's steps against user-defined daily goal.
 */
@Composable
fun StepGoalCircularProgressCard(
    todaySteps: Int,
    dailyGoal: Int,
    todayCalories: Int,
    distanceKm: Float,
    numberFormat: NumberFormat,
    modifier: Modifier = Modifier
) {
    val rawRatio = todaySteps.toFloat() / dailyGoal.coerceAtLeast(1)
    val clampedRatio = rawRatio.coerceIn(0f, 1f)
    val percentInt = (rawRatio * 100).toInt()
    val isGoalAchieved = todaySteps >= dailyGoal
    val remainingSteps = (dailyGoal - todaySteps).coerceAtLeast(0)

    // Entrance animation trigger when tab is opened
    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationStarted = true
    }

    // Smooth filling progress arc from 0 to current value
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) clampedRatio else 0f,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label = "circular_step_progress"
    )

    // Animated counting step numbers from 0 to current value
    val animatedDisplayedSteps by animateIntAsState(
        targetValue = if (animationStarted) todaySteps else 0,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label = "animated_steps_counter"
    )

    // Animated percentage from 0 to current percentage
    val animatedPercentage by animateIntAsState(
        targetValue = if (animationStarted) percentInt else 0,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label = "animated_percentage_counter"
    )

    // Subtle scale entrance for the circular gauge
    val gaugeScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.88f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "gauge_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fitness_circular_progress_card"),
        colors = CardDefaults.cardColors(
            containerColor = SlateSurface.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isGoalAchieved) FitnessGreen.copy(alpha = 0.6f) else CyanPrimary.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header within card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isGoalAchieved) FitnessGreen.copy(alpha = 0.2f) else CyanPrimary.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isGoalAchieved) Icons.Default.CheckCircle else Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = if (isGoalAchieved) FitnessGreen else CyanPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "दैनिक उद्दिष्ट प्रगती (Daily Goal Progress)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isGoalAchieved) "उद्दिष्ट पूर्ण झाले! 🎉" else "उद्दिष्टाच्या दिशेने 🎯",
                            fontSize = 11.sp,
                            color = if (isGoalAchieved) FitnessGreen else CyanPrimary
                        )
                    }
                }

                // Percentage Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isGoalAchieved) FitnessGreen.copy(alpha = 0.2f) else CyanPrimary.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$animatedPercentage%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGoalAchieved) FitnessGreen else CyanPrimary,
                        modifier = Modifier.testTag("fitness_circular_percentage_text")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CIRCULAR PROGRESS GAUGE WITH ENTRANCE SCALE
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .scale(gaugeScale)
                    .testTag("fitness_circular_progress_gauge")
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    val strokeWidth = 16.dp.toPx()
                    val arcSize = size.width - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // 1. Background Track Ring
                    drawArc(
                        color = Color(0xFF334155),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 2. Active Progress Arc with dynamic gradient
                    val gradientBrush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF38BDF8), // Cyan
                            Color(0xFF4ADE80), // Green
                            Color(0xFF38BDF8)
                        ),
                        center = center
                    )

                    val sweep = animatedProgress * 360f
                    if (sweep > 0f) {
                        drawArc(
                            brush = gradientBrush,
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                // Inner Stats Overlay inside the circle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = "Steps",
                        tint = if (isGoalAchieved) FitnessGreen else CyanPrimary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = numberFormat.format(animatedDisplayedSteps),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        modifier = Modifier.testTag("fitness_circular_steps_text")
                    )

                    Text(
                        text = "/ ${numberFormat.format(dailyGoal)} पावले",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isGoalAchieved) "Completed!" else "$animatedPercentage% गाठले",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isGoalAchieved) FitnessGreen else CyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comparison Summary Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isGoalAchieved) FitnessGreen.copy(alpha = 0.15f) else SlateBackground
                    )
                    .border(
                        1.dp,
                        if (isGoalAchieved) FitnessGreen.copy(alpha = 0.4f) else SlateBorder.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isGoalAchieved) {
                                "अभिनंदन! आजचे उद्दिष्ट पूर्ण झाले! 🏆"
                            } else {
                                "उद्दिष्टासाठी उर्वरित: ${numberFormat.format(remainingSteps)} पावले"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isGoalAchieved) FitnessGreen else TextPrimary
                        )
                        Text(
                            text = "सेटिंग्जमधील उद्दिष्ट: ${numberFormat.format(dailyGoal)} पावले",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    if (isGoalAchieved) {
                        Text(
                            text = "🎯 १००%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitnessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick comparison triple-pill row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GoalMetricPill(
                    label = "आजची पावले",
                    value = numberFormat.format(todaySteps),
                    color = if (isGoalAchieved) FitnessGreen else CyanPrimary,
                    modifier = Modifier.weight(1f)
                )

                GoalMetricPill(
                    label = "दैनिक ध्येय",
                    value = numberFormat.format(dailyGoal),
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                GoalMetricPill(
                    label = "कॅलरी बर्न",
                    value = "$todayCalories kcal",
                    color = Color(0xFFFBBF24), // Amber
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GoalMetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SlateBackground)
            .border(1.dp, SlateBorder.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}
