package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local fitness step data.
 */
@Dao
interface StepDataDao {
    @Query("SELECT * FROM step_data ORDER BY timestamp DESC")
    fun getAllStepData(): Flow<List<StepData>>

    @Query("SELECT * FROM step_data WHERE dateString = :dateString LIMIT 1")
    fun getStepDataByDate(dateString: String): Flow<StepData?>

    @Query("SELECT * FROM step_data WHERE dateString = :dateString LIMIT 1")
    suspend fun getStepDataByDateOnce(dateString: String): StepData?

    @Query("SELECT * FROM step_data ORDER BY timestamp DESC LIMIT 1")
    fun getLatestStepData(): Flow<StepData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStepData(stepData: StepData): Long

    @Update
    suspend fun updateStepData(stepData: StepData)

    @Query("DELETE FROM step_data")
    suspend fun deleteAllStepData()
}

/**
 * Data Access Object for local conversation history and voice interaction logs.
 */
@Dao
interface VoiceInteractionDao {
    @Query("SELECT * FROM voice_interactions ORDER BY timestamp ASC")
    fun getAllInteractions(): Flow<List<VoiceInteraction>>

    @Query("SELECT * FROM voice_interactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentInteractions(limit: Int = 50): Flow<List<VoiceInteraction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: VoiceInteraction): Long

    @Query("DELETE FROM voice_interactions")
    suspend fun clearHistory()
}
