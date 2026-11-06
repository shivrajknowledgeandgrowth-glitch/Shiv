package com.example.data

import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String, val locale: Locale) {
    MARATHI("mr-IN", "मराठी (Marathi)", Locale("mr", "IN")),
    HINDI("hi-IN", "हिंदी (Hindi)", Locale("hi", "IN")),
    ENGLISH("en-US", "English", Locale.US);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code == code } ?: MARATHI
        }
    }
}

enum class MessageSender {
    USER, SHIVAI
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DayFitnessRecord(
    val dayNumber: Int,
    val label: String,
    val steps: Int,
    val calories: Int,
    val distanceKm: Float
)

data class UserProfile(
    val name: String = "Shivraj",
    val username: String = "shiv_user",
    val dailyGoal: Int = 6000,
    val language: AppLanguage = AppLanguage.MARATHI,
    val isAiEnabled: Boolean = true,
    val isAutoTtsEnabled: Boolean = true
)

sealed interface AiUiStatus {
    data object Idle : AiUiStatus
    data object Listening : AiUiStatus
    data object Thinking : AiUiStatus
    data class Streaming(val partialText: String) : AiUiStatus
    data class Success(val text: String) : AiUiStatus
    data class Error(val message: String) : AiUiStatus
}

enum class AppTab(val title: String, val iconText: String) {
    HOME("Home", "🏠"),
    FITNESS("Fitness", "🏃"),
    WORKOUT("Workout", "🏋️"),
    SETTING("Setting", "⚙️")
}
