package com.example

import com.example.data.AppLanguage
import com.example.data.ChatMessage
import com.example.data.DayFitnessRecord
import com.example.data.MessageSender
import com.example.data.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testUserProfileDefaults() {
        val profile = UserProfile()
        assertEquals("Shivraj", profile.name)
        assertEquals("shiv_user", profile.username)
        assertEquals(6000, profile.dailyGoal)
        assertEquals(AppLanguage.MARATHI, profile.language)
        assertTrue(profile.isAiEnabled)
        assertTrue(profile.isAutoTtsEnabled)
    }

    @Test
    fun testAppLanguageCodes() {
        assertEquals(AppLanguage.MARATHI, AppLanguage.fromCode("mr-IN"))
        assertEquals(AppLanguage.HINDI, AppLanguage.fromCode("hi-IN"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en-US"))
        assertEquals(AppLanguage.MARATHI, AppLanguage.fromCode("unknown"))
    }

    @Test
    fun testDayFitnessRecordCalculations() {
        val record = DayFitnessRecord(
            dayNumber = 1,
            label = "दिवस १",
            steps = 5000,
            calories = 200,
            distanceKm = 3.75f
        )
        assertEquals(1, record.dayNumber)
        assertEquals(5000, record.steps)
        assertEquals(200, record.calories)
    }

    @Test
    fun testChatMessageCreation() {
        val message = ChatMessage(
            sender = MessageSender.SHIVAI,
            text = "नमस्कार! मी तुमचा ShivAI असिस्टंट आहे."
        )
        assertNotNull(message.id)
        assertEquals(MessageSender.SHIVAI, message.sender)
    }

    @Test
    fun testStepGoalComparison() {
        val todaySteps = 4250
        val dailyGoal = 6000
        val ratio = (todaySteps.toFloat() / dailyGoal).coerceIn(0f, 1f)
        val remaining = (dailyGoal - todaySteps).coerceAtLeast(0)
        val percentage = (ratio * 100).toInt()

        assertEquals(0.7083f, ratio, 0.001f)
        assertEquals(1750, remaining)
        assertEquals(70, percentage)
    }

    @Test
    fun testVoiceCommandItem() {
        val item = com.example.ui.components.VoiceCommandItem(
            category = "🏃 फिटनेस",
            commandText = "आज मी किती पावले चाललो?",
            englishHint = "How many steps today?",
            description = "आजच्या पावलांची माहिती."
        )
        assertEquals("🏃 फिटनेस", item.category)
        assertTrue(item.commandText.contains("पावले"))
        assertEquals("How many steps today?", item.englishHint)
    }
}
