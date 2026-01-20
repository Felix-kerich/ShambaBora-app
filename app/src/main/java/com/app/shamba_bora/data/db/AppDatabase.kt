package com.app.shamba_bora.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.shamba_bora.data.db.dao.SavedAdviceDao
import com.app.shamba_bora.data.db.entities.SavedAdvice

/**
 * Room database for local data persistence
 */
@Database(
    entities = [SavedAdvice::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedAdviceDao(): SavedAdviceDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shamba_bora_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
