package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database entity representing daily fitness step records.
 * Persists user steps, burned calories, estimated distance, and target goals on device.
 */
@Entity(tableName = "step_data")
data class StepData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String, // e.g. "2026-11-06"
    val steps: Int,
    val calories: Int,
    val distanceKm: Float,
    val dailyGoal: Int = 6000,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Room Database entity representing voice and text interaction logs.
 * Persists conversation history between the user and ShivAI locally on device.
 */
@Entity(tableName = "voice_interactions")
data class VoiceInteraction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "USER" or "SHIVAI"
    val text: String,
    val languageCode: String, // "mr-IN", "hi-IN", "en-US"
    val isVoiceInput: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
