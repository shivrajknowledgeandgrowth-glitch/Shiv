package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppLanguage
import com.example.data.ChatMessage
import com.example.data.MessageSender
import com.example.data.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ShivAI", appName)
    }

    @Test
    fun `verify user profile defaults and language support`() {
        val profile = UserProfile()
        assertEquals("Shivraj", profile.name)
        assertEquals("shiv_user", profile.username)
        assertEquals(6000, profile.dailyGoal)
        assertEquals(AppLanguage.MARATHI, profile.language)
        assertTrue(profile.isAiEnabled)
    }

    @Test
    fun `verify chat message creation`() {
        val message = ChatMessage(
            sender = MessageSender.SHIVAI,
            text = "नमस्कार! मी तुमचा ShivAI असिस्टंट आहे."
        )
        assertNotNull(message.id)
        assertEquals(MessageSender.SHIVAI, message.sender)
        assertTrue(message.text.contains("ShivAI"))
    }

    @Test
    fun `verify StepData and VoiceInteraction Room database operations`() = kotlinx.coroutines.test.runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val inMemoryDb = androidx.room.Room.inMemoryDatabaseBuilder(
            context,
            com.example.data.local.ShivAiDatabase::class.java
        ).allowMainThreadQueries().build()

        val stepDao = inMemoryDb.stepDataDao()
        val voiceDao = inMemoryDb.voiceInteractionDao()

        // 1. Test StepData entity insertion and query
        val stepData = com.example.data.local.StepData(
            dateString = "2026-11-06",
            steps = 5500,
            calories = 220,
            distanceKm = 4.1f,
            dailyGoal = 6000
        )
        val stepId = stepDao.insertOrUpdateStepData(stepData)
        assertTrue(stepId > 0)

        val retrievedStep = stepDao.getStepDataByDateOnce("2026-11-06")
        assertNotNull(retrievedStep)
        assertEquals(5500, retrievedStep?.steps)
        assertEquals(220, retrievedStep?.calories)

        // 2. Test VoiceInteraction entity insertion and query
        val voiceLog = com.example.data.local.VoiceInteraction(
            sender = "USER",
            text = "आज किती पावले चाललो?",
            languageCode = "mr-IN",
            isVoiceInput = true
        )
        val voiceId = voiceDao.insertInteraction(voiceLog)
        assertTrue(voiceId > 0)

        val assistantLog = com.example.data.local.VoiceInteraction(
            sender = "SHIVAI",
            text = "आज तुम्ही ५,५०० पावले चालला आहात.",
            languageCode = "mr-IN",
            isVoiceInput = false
        )
        val assistantId = voiceDao.insertInteraction(assistantLog)
        assertTrue(assistantId > 0)

        inMemoryDb.close()
    }

    @Test
    fun `verify WorkoutDatabase content and 3-step guides`() {
        val exercises = com.example.data.WorkoutDatabase.exercises
        assertTrue(exercises.isNotEmpty())

        val chestExercises = exercises.filter { it.bodyPart == com.example.data.BodyPart.CHEST }
        assertTrue(chestExercises.size >= 2)

        val dumbbellBench = chestExercises.first { it.id == "chest_dumbbell_bench_press" }
        assertEquals(com.example.data.WorkoutEquipment.DUMBBELL, dumbbellBench.equipment)
        assertEquals(1, dumbbellBench.step1.stepNumber)
        assertEquals(2, dumbbellBench.step2.stepNumber)
        assertEquals(3, dumbbellBench.step3.stepNumber)

        val pushups = chestExercises.first { it.id == "chest_bodyweight_pushups" }
        assertEquals(com.example.data.WorkoutEquipment.BODYWEIGHT, pushups.equipment)
        assertTrue(pushups.step1.descriptionMarathi.isNotEmpty())

        val tabs = com.example.data.AppTab.entries
        assertTrue(tabs.contains(com.example.data.AppTab.WORKOUT))
    }
}

