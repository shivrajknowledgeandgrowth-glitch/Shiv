package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Main Room Database for the ShivAI application.
 * Manages local persistence of StepData and VoiceInteraction logs.
 */
@Database(
    entities = [StepData::class, VoiceInteraction::class],
    version = 1,
    exportSchema = false
)
abstract class ShivAiDatabase : RoomDatabase() {

    abstract fun stepDataDao(): StepDataDao
    abstract fun voiceInteractionDao(): VoiceInteractionDao

    companion object {
        @Volatile
        private var INSTANCE: ShivAiDatabase? = null

        fun getDatabase(context: Context): ShivAiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShivAiDatabase::class.java,
                    "shivai_local_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
