package com.example.data.repository

import com.example.data.local.ShivAiDatabase
import com.example.data.local.StepData
import com.example.data.local.VoiceInteraction
import kotlinx.coroutines.flow.Flow

/**
 * Repository abstracting data access for Room entities StepData and VoiceInteraction.
 */
class ShivAiRepository(database: ShivAiDatabase) {

    private val stepDataDao = database.stepDataDao()
    private val voiceInteractionDao = database.voiceInteractionDao()

    val allStepData: Flow<List<StepData>> = stepDataDao.getAllStepData()
    val latestStepData: Flow<StepData?> = stepDataDao.getLatestStepData()
    val allInteractions: Flow<List<VoiceInteraction>> = voiceInteractionDao.getAllInteractions()

    fun getStepDataByDate(dateString: String): Flow<StepData?> =
        stepDataDao.getStepDataByDate(dateString)

    suspend fun getStepDataByDateOnce(dateString: String): StepData? =
        stepDataDao.getStepDataByDateOnce(dateString)

    suspend fun saveStepData(stepData: StepData): Long =
        stepDataDao.insertOrUpdateStepData(stepData)

    suspend fun logVoiceInteraction(
        sender: String,
        text: String,
        languageCode: String,
        isVoiceInput: Boolean = false
    ): Long {
        val interaction = VoiceInteraction(
            sender = sender,
            text = text,
            languageCode = languageCode,
            isVoiceInput = isVoiceInput
        )
        return voiceInteractionDao.insertInteraction(interaction)
    }

    suspend fun clearHistory() {
        voiceInteractionDao.clearHistory()
    }

    suspend fun deleteAllStepData() {
        stepDataDao.deleteAllStepData()
    }
}
